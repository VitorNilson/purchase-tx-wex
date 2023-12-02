package wex.purchasetx.exchangerate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class HttpRequestServiceTest {

    @Autowired
    private HttpRequestService requestService;


    @Test
    void givenFind_WhenPageIsGreaterThan0_ThenShouldReturnABodyContaining0OrMoreExchangeRates() throws InterruptedException {
        var result = requestService.find(1).block();

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.meta().getTotalPages());
    }
}
