package wex.purchasetx.exchangerate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import wex.purchasetx.exchangerate.model.ExchangeRate;
import wex.purchasetx.exchangerate.repository.ExchangeRateRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository repository;

    public Mono<ExchangeRate> save(ExchangeRate exchangeRate) {
        return repository.save(exchangeRate);
    }

    public Mono<Void> removeOlderThan(Integer months) {
        return repository.deleteByEffectiveDateLessThanEqual(LocalDate.now().minus(months, ChronoUnit.MONTHS));
    }

    public Mono<ExchangeRate> findById(String id) {
        return repository.findById(id);
    }

    public Mono<Page<ExchangeRate>> findAll(int page, int size, String sortBy, Sort.Direction direction) {
        var pageable = PageRequest.of(page, Math.min(size, 250), Sort.by(direction, sortBy));
        return this.repository.findAllBy(pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }


}
