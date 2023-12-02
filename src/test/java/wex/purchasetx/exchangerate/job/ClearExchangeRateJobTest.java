package wex.purchasetx.exchangerate.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import wex.purchasetx.exchangerate.model.ExchangeRate;
import wex.purchasetx.exchangerate.repository.ExchangeRateRepository;
import wex.purchasetx.exchangerate.service.ExchangeRateService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClearExchangeRateJobTest {

    @Autowired
    private ExchangeRateService service;

    @Autowired
    private ExchangeRateRepository repository;

    private ClearExchangeRateJob job;

    @BeforeAll
    void setUp() {
        this.job = new ClearExchangeRateJob(service);
    }

    @BeforeEach
    void cleanUp() {
        repository.deleteAll().block();
    }

    @Test
    void givenRunJob_WhenTheresDataOlderThan6Months_ThenAfterTheJobRunsTheresNotSupposeToExistsNothingOlderThan6Months() {
        saveExchangeRate("germany", 6);
        saveExchangeRate("france", 6);
        saveExchangeRate("australia", 7);
        saveExchangeRate("madagascar", 7);
        saveExchangeRate("curacao", 5);

        job.run();

        Assertions.assertEquals(1, repository.count().block());
        Assertions.assertEquals("curacao", repository.findAll().collectList().block().get(0).getCountry());

    }

    private void saveExchangeRate(String country, int months) {
        service.save(ExchangeRate.builder()
                .rate(new BigDecimal("5.00"))
                .currency("money")
                .country(country)
                .effectiveDate(LocalDate.now().minus(months, ChronoUnit.MONTHS))
                .build()).block();
    }
}
