package wex.purchasetx.transaction.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wex.purchasetx.common.exception.ValidationError;
import wex.purchasetx.common.exception.ValidationException;
import wex.purchasetx.exchangerate.exception.ExchangeRateNotFoundException;
import wex.purchasetx.exchangerate.model.ExchangeRate;
import wex.purchasetx.exchangerate.service.ExchangeRateService;
import wex.purchasetx.transaction.dto.PurchaseTransactionResponseDto;
import wex.purchasetx.transaction.mapper.PurchaseTransactionMapper;
import wex.purchasetx.transaction.model.PurchaseTransaction;
import wex.purchasetx.transaction.repository.PurchaseRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository repository;
    private final Validator validator;
    private final ExchangeRateService exchangeRateService;
    private final PurchaseTransactionMapper mapper;

    public Mono<PurchaseTransaction> create(PurchaseTransaction purchaseTransaction) {
        var validations = validator.validate(purchaseTransaction);
        if (!validations.isEmpty()) {
            var violations = validations.stream().map(value -> new ValidationError(value.getPropertyPath().toString(), value.getMessage())).toList();
            throw new ValidationException("Constraint Violations.", violations);
        }

        purchaseTransaction.setPurchaseAmount(round(purchaseTransaction.getPurchaseAmount()));

        return repository.save(purchaseTransaction);
    }

    public Mono<Page<PurchaseTransactionResponseDto>> findAll(int page, int size, String sortBy, Sort.Direction direction, ZonedDateTime start, ZonedDateTime end, String country) {

        var pageable = PageRequest.of(page, Math.min(size, 250), Sort.by(direction, sortBy));

        return exchangeRateService.findById(country)
                .flatMap(exchangeRate -> {
                    if (start != null || end != null)
                        return findAllByDates(pageable, direction, start, end, exchangeRate);

                    return findAllDefault(pageable, exchangeRate);
                }).switchIfEmpty(Mono.error(new ExchangeRateNotFoundException()));
    }

    public Mono<PurchaseTransactionResponseDto> findById(String id, String country) {

        return exchangeRateService.findById(country).flatMap(
                rate -> repository.findById(id).map(item -> calculatePurchaseAmountByRate(item, rate))
        ).switchIfEmpty(Mono.error(new ExchangeRateNotFoundException()));

    }

    private Mono<Page<PurchaseTransactionResponseDto>> findAllDefault(Pageable pageable, ExchangeRate exchangeRate) {
        return this.repository.findAllBy(pageable)
                .collectList()
                .map(content -> calculatePurchaseAmountByRate(content, exchangeRate))
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    private Mono<Page<PurchaseTransactionResponseDto>> findAllByDates(Pageable pageable, Sort.Direction direction, ZonedDateTime start, ZonedDateTime end, ExchangeRate exchangeRate) {
        return repository.findAllByStartAndEndDate(start, end, direction.name(), pageable.getPageSize(), pageable.getPageNumber()).collectList()
                .map(content -> calculatePurchaseAmountByRate(content, exchangeRate))
                .zipWith(this.repository.countBy(start, end))
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    public List<PurchaseTransactionResponseDto> calculatePurchaseAmountByRate(List<PurchaseTransaction> content, ExchangeRate exchangeRate) {
        return content.stream().map(item -> calculatePurchaseAmountByRate(item, exchangeRate)).toList();
    }

    private PurchaseTransactionResponseDto calculatePurchaseAmountByRate(PurchaseTransaction transaction, ExchangeRate exchangeRate) {
        var rate = exchangeRate.getRate();
        var response = mapper.domainToResponseDto(transaction);

        response.setUsedRate(rate);
        response.setConvertedValue(round(transaction.getPurchaseAmount().multiply(rate)));
        response.setCountryName(exchangeRate.getCountry());

        return response;
    }

    public BigDecimal round(BigDecimal purchaseAmount) {
        return purchaseAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
