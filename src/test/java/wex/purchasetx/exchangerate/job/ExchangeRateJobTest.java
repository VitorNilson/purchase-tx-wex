package wex.purchasetx.exchangerate.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import wex.purchasetx.exchangerate.dto.ExchangeApiResponseMetaDto;
import wex.purchasetx.exchangerate.dto.ExchangeRateApiResponseDto;
import wex.purchasetx.exchangerate.model.ExchangeRate;
import wex.purchasetx.exchangerate.repository.ExchangeRateRepository;
import wex.purchasetx.exchangerate.service.ExchangeRateService;
import wex.purchasetx.exchangerate.service.HttpRequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExchangeRateJobTest {

    @Autowired
    private ExchangeRateService service;

    @Autowired
    private ExchangeRateRepository repository;

    @MockBean
    private HttpRequestService httpRequestService;

    private ExchangeRateJob job;

    @BeforeAll
    void setUp() {
        this.job = new ExchangeRateJob(service, httpRequestService);
    }

    @BeforeEach
    void cleanUp() {
        repository.deleteAll().block();
    }

    @Test
    void givenRunJob_WhenTheResponseContainsXPages_ThenShouldIterateOverEachPageAndSaveAllDataFromThosePages() {

        Mockito.doReturn(getResponse(7L, 2, "Ruble", "Russia")).when(httpRequestService).find(1);
        Mockito.doReturn(getResponse(7L, 1, "Peso", "Uruguay")).when(httpRequestService).find(3);
        Mockito.doReturn(getResponse(7L, 1, "Guarani", "Paraguay")).when(httpRequestService).find(2);
        Mockito.doReturn(getResponse(7L, 1, "Peso", "Argentina")).when(httpRequestService).find(4);
        Mockito.doReturn(getResponse(7L, 1, "Rublo", "Russia")).when(httpRequestService).find(5);
        Mockito.doReturn(getResponse(7L, 1, "Kwanza", "Angola")).when(httpRequestService).find(6);
        Mockito.doReturn(getResponse(7L, 2, "Kwanza", "Angola")).when(httpRequestService).find(7);

        job.run();

        Assertions.assertEquals(service.findById("Russia").block().getRate(), new BigDecimal("5.00"));
        Assertions.assertEquals(service.findById("Russia").block().getEffectiveDate(), LocalDate.now().minus(1, ChronoUnit.MONTHS));
        Assertions.assertEquals(service.findById("Angola").block().getEffectiveDate(), LocalDate.now().minus(1, ChronoUnit.MONTHS));
        Assertions.assertEquals(service.findById("Paraguay").block().getRate(), new BigDecimal("5.00"));
        Assertions.assertEquals(service.findById("Argentina").block().getRate(), new BigDecimal("5.00"));
        Assertions.assertEquals(service.findById("Uruguay").block().getRate(), new BigDecimal("5.00"));


    }

    private Mono<ExchangeRateApiResponseDto> getResponse(Long totalPages, int minusMonths, String currency, String country) {

        var meta = ExchangeApiResponseMetaDto.builder()
                .totalPages(totalPages)
                .build();

        return Mono.just(new ExchangeRateApiResponseDto(Arrays.asList(
                ExchangeRate.builder()
                        .rate(new BigDecimal("5.00"))
                        .currency(currency)
                        .country(country)
                        .effectiveDate(LocalDate.now().minus(minusMonths, ChronoUnit.MONTHS))
                        .build()), meta));

    }

}
