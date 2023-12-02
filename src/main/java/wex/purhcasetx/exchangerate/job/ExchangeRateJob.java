package wex.purhcasetx.exchangerate.job;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wex.purhcasetx.exchangerate.dto.ExchangeRateApiResponseDto;
import wex.purhcasetx.exchangerate.model.ExchangeRate;
import wex.purhcasetx.exchangerate.service.ExchangeRateService;
import wex.purhcasetx.exchangerate.service.HttpRequestService;

import java.util.logging.Logger;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ExchangeRateJob implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(ExchangeRateJob.class.getName());
    private final ExchangeRateService service;
    private final HttpRequestService requestService;

    @Scheduled(cron = "0 0 23 1 * *")
    public void run() {
        logger.info("Running ExchangeRateJob.");

        requestService.find(1)
                .flatMapMany(this::processInitialPage)
                .subscribe();
    }

    @Override
    public void run(String... args) throws Exception {
        run();
    }

    private Flux<ExchangeRate> processInitialPage(ExchangeRateApiResponseDto initialResponse) {
        final int totalPages = initialResponse.meta().getTotalPages().intValue();

        Flux<ExchangeRate> initialDataProcessing = Flux.fromIterable(initialResponse.data())
                .flatMap(this::processExchangeRate);

        Flux<ExchangeRate> subsequentPagesProcessing = Flux.range(2, totalPages - 1)
                .concatMap(this::processNextPages);

        return Flux.concat(initialDataProcessing, subsequentPagesProcessing);
    }

    private Mono<ExchangeRate> processExchangeRate(ExchangeRate item) {
        return service.findById(item.getCountry())
                .flatMap(existingItem -> {
                    if (item.getEffectiveDate().isAfter(existingItem.getEffectiveDate())) {
                        item.setNew(false); // Spring Data needs this to know if it needs to update or insert.
                        return service.save(item);
                    }
                    return Mono.just(existingItem);
                })
                .switchIfEmpty(service.save(item));
    }

    private Flux<ExchangeRate> processNextPages(int page) {
        return requestService.find(page)
                .flatMapMany(response -> Flux.fromIterable(response.data())
                        .flatMap(this::processExchangeRate)
                );
    }


}


