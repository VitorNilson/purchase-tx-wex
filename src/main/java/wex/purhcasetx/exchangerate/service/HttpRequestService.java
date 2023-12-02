package wex.purhcasetx.exchangerate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import wex.purhcasetx.exchangerate.dto.ExchangeRateApiResponseDto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class HttpRequestService {

    public Mono<ExchangeRateApiResponseDto> find(int page) {
        return WebClient.create("https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange")
                .get()
                .uri(builder -> builder
                        .queryParam("filter", "effective_date:gte:" + LocalDate.now().minus(6, ChronoUnit.MONTHS).toString())
                        .queryParam("page[number]", page)
                        .build())
                .retrieve().bodyToMono(ExchangeRateApiResponseDto.class)
                .retry(3); // Circuit Breaker.
    }
}
