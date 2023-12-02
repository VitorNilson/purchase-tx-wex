package wex.purhcasetx.exchangerate.job;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wex.purhcasetx.exchangerate.service.ExchangeRateService;

import java.util.logging.Logger;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class ClearExchangeRateJob {

    private static final Logger logger = Logger.getLogger(ClearExchangeRateJob.class.getName());
    private final ExchangeRateService service;

    @Scheduled(cron = "0 0 1 * * ?")
    public void run() {
        logger.info("Running ClearExchangeRateJob.");
        service.removeOlderThan(6).subscribe();
    }
}
