package wex.purchasetx.exchangerate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import wex.purchasetx.exchangerate.model.ExchangeRate;
import wex.purchasetx.exchangerate.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeRateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ExchangeRateRepository repository;

    @BeforeAll
    void setUp(){
        repository.save(buildExchangeRate("test  0")).block();
        repository.save(buildExchangeRate("test 1")).block();
        repository.save(buildExchangeRate("test  2")).block();
        repository.save(buildExchangeRate("test   3")).block();
    }

    @AfterAll
    void cleanUp(){
        repository.deleteAll().block();
    }

    @Test
    void givenFindAll_WhenParametersAreNotPresent_ThenShouldReturn200AndOnlyTheFirstPage() {
        webTestClient.get()
                .uri("/exchange-rates")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(4)
                .jsonPath("$.pageable.pageNumber").isEqualTo(0);
    }

    @Test
    void givenFindAll_WhenParametersArePresent_ThenShouldReturn200AndRespectTheParameters() {
        webTestClient.get()
                .uri(builder -> builder
                        .path("/exchange-rates")
                        .queryParam("page", 0)
                        .queryParam("size", 1)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .jsonPath("$.totalPages").isEqualTo(4)
                .jsonPath("$.totalElements").isEqualTo(4);

    }

    private ExchangeRate buildExchangeRate(String country) {
        return ExchangeRate.builder()
                .rate(new BigDecimal("5.00"))
                .currency("money")
                .country(country)
                .effectiveDate(LocalDate.now().minus(1, ChronoUnit.MONTHS))
                .build();
    }
}
