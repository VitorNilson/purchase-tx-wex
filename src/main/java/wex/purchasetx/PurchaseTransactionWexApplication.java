package wex.purchasetx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableR2dbcAuditing
@SpringBootApplication
public class PurchaseTransactionWexApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurchaseTransactionWexApplication.class, args);
	}

}
