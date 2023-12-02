package wex.purchasetx.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import wex.purchasetx.transaction.model.PurchaseTransaction;
import wex.purchasetx.transaction.repository.PurchaseRepository;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/*
 * This is an integration test. Chose because it's the middle term between an end-to-end and a unit test.
 * */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PurchaseTransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @BeforeAll
    void setUp() {
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(15))
                .build();
    }

    @BeforeEach
    void cleanUp() {
        exchangeRateRepository.deleteAll().block();
        purchaseRepository.deleteAll().block();
    }

    @Test
    void givenCreate_WhenTransactionAmountIsPresentAndTransactionDateIsInAcceptedPatterns_ThenShouldReturn201() {
        createPurchaseTransaction("125.238938948", "2023-11-30T08:45:14.113966052-03:00");
        createPurchaseTransaction("125.238938948", "2023-11-30T08:45:14-03:00");
    }

    @Test
    void givenCreate_WhenTheDateIsInAnUnsupportedFormat_ThenShouldReturn400() throws JsonProcessingException {
        doPostRequest("125.238938948", "2023-12-02 09:08:05")
                .expectStatus().isBadRequest();
    }

    @Test
    void givenCreate_WhenThereIsOneOrMoreConstraintViolations_ThenShouldReturn400() {

        var body = """
                {
                	"description":"Neque porro quisquam est qui dolorem ipsum quia",
                	"transactionDate": "2023-11-30T08:45:14.113966052-03:00"
                }
                """;

        var result = webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();


        System.out.println(result.returnResult(Object.class).getResponseBody());


        var body2 = """
                {
                	"description":"Neque porro quisquam est qui dolorem ipsum quia",
                	"transactionDate": "2023-11-30T08:45:14-03:00"
                }
                """;

        webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body2)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void givenCreate_WhenPurchaseAmountHasMoreThen2Digits_ThenShouldReturn201WithPurchaseAmountRoundedToTheNearestCent() {

        doPostRequest("125.238938948", "2023-11-30T08:45:14.113966052-03:00")
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.purchaseAmount").isEqualTo(125.24);

        doPostRequest("99.9899939393939393939999999999999999999099302900000000000999999999999999999023293999329999", "2023-11-30T08:45:14.113966052-03:00")
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.purchaseAmount").isEqualTo(99.99);

        doPostRequest("99.9999939393939393939999999999999999999099302900000000000999999999999999999023293999329999", "2023-11-30T08:45:14.113966052-03:00")
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.purchaseAmount").isEqualTo(100);

        doPostRequest("0.000000000000000000000000000000000000000000000000000000000000000000000000000000001", "2023-11-30T08:45:14.113966052-03:00")
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.purchaseAmount").isEqualTo(0.00);

        doPostRequest("0.01000000000000000000000000000000000000000000000000000000000000000000000000000000", "2023-11-30T08:45:14.113966052-03:00")
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.purchaseAmount").isEqualTo(0.01);

    }

    @Test
    void givenCreate_WhenDescriptionHasMoreThan50Characters_ThenShouldReturn400WithAMessageOnBody() {

        // 179 Characters.
        webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                        	"description":"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum luctus dolor a massa viverra, vitae cursus eros finibus. Mauris ut sagittis purus. Integer ut consequat sapien.",
                        	"purchaseAmount":125.238938948,
                        	"transactionDate": "2023-11-30T08:45:14.113966052-03:00"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Constraint Violations.")
                .jsonPath("$.violations[0].field").isEqualTo("description");


        // 51 characters
        webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                        	"description":"Lorem ipsum dolor sit amet, consectetur adipiscing.",
                        	"purchaseAmount":125.238938948,
                        	"transactionDate": "2023-11-30T08:45:14-03:00"
                        }
                        """)
                .exchange()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Constraint Violations.")
                .jsonPath("$.violations[0].field").isEqualTo("description");
    }

    @Test
    void givenFindAll_WhenTheExchangeRateExists_ThenShouldReturn200WithTheValueConverted() {

        createExchange();
        createPurchaseTransaction("1845.438938948", "2023-11-30T08:45:14-03:00");


        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.content[0].convertedValue").isEqualTo(9227.20)
                .jsonPath("$.content[0].description").isEqualTo("Neque porro quisquam est qui dolorem ipsum quia")
                .jsonPath("$.content[0].transactionDate").isEqualTo("2023-11-30T08:45:14-03:00")
                .jsonPath("$.content[0].originalValue").isEqualTo(1845.44)
                .jsonPath("$.content[0].usedRate").isEqualTo(5.00)
                .jsonPath("$.content[0].countryName").isEqualTo("brazil");

    }

    @Test
    void givenFindAll_WhenTheExchangeRateDoesNotExists_ThenShouldReturn404() {


        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void givenFindAll_WhenStartDateOrEndDateIsPresent_ThenShouldReturn200AndFilteredByTheDateRange() {

        createPurchaseTransaction("125.238938948", "2023-11-30T08:45:14-03:00");
        createPurchaseTransaction("125.238938948", "2023-11-30T09:45:14-03:00");
        createPurchaseTransaction("125.238938948", "2023-11-30T10:45:14-03:00");
        createPurchaseTransaction("125.238938948", "2023-11-30T11:45:14-03:00");

        createExchange();

        // Should return all Transactions, since the minor starts at 2023-11-30T08:45:14-03:00.
        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("start", "2023-11-30T08:00:00-03:00")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.numberOfElements").isEqualTo(4);

        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("start", "2023-11-30T10:00:00-03:00")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2);

        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("end", "2023-11-30T10:00:00-03:00")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfElements").isEqualTo(2);

        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("start", "2023-11-30T10:00:00-03:00")
                                .queryParam("end", "2023-11-30T11:00:00-03:00")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.numberOfElements").isEqualTo(1);


        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions")
                                .queryParam("start", "2023-11-30T12:00:00-03:00")
                                .queryParam("end", "2023-11-30T14:00:00-03:00")
                                .queryParam("country", "brazil")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.numberOfElements").isEqualTo(0);

    }

    @Test
    void givenFindById_WhenTheCurrencyExistsAndThePurchaseTransactionExists_ThenShouldReturn200AndThePuchaseTransactionOnTheBody() throws JsonProcessingException {

        createExchange();

        var result = createPurchaseTransaction("125.238938948", "2023-12-02T09:08:05-05:00");


        webTestClient.get()
                .uri(builder ->
                        builder
                                .path("/purchase-transactions/{id}")
                                .queryParam("country", "brazil")
                                .build(result.getTransactionId().toString())
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.originalValue").isEqualTo(125.24)
                .jsonPath("$.convertedValue").isEqualTo(626.20)
                .jsonPath("$.description").isEqualTo("Neque porro quisquam est qui dolorem ipsum quia")
                .jsonPath("$.transactionDate").isEqualTo("2023-12-02T09:08:05-05:00")
                .jsonPath("$.usedRate").isEqualTo(5.00)
                .jsonPath("$.countryName").isEqualTo("brazil");

    }

    private PurchaseTransaction createPurchaseTransaction() {
        return createPurchaseTransaction("125.238938948", "2023-11-30T08:45:14.113966052-03:00");
    }

    private WebTestClient.ResponseSpec doPostRequest(String purchaseAmount, String transactionDate) {
        var body = """
                {
                	"description":"Neque porro quisquam est qui dolorem ipsum quia",
                	"purchaseAmount": %s,
                	"transactionDate": "%s"
                }
                """;

        return webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format(body, purchaseAmount, transactionDate))
                .exchange();

    }

    private PurchaseTransaction createPurchaseTransaction(String purchaseAmount, String transactionDate) {

        var body = """
                {
                	"description":"Neque porro quisquam est qui dolorem ipsum quia",
                	"purchaseAmount": %s,
                	"transactionDate": "%s"
                }
                """;


        var result = webTestClient.post()
                .uri("/purchase-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(String.format(body, purchaseAmount, transactionDate))
                .exchange()
                .expectStatus().isCreated()
                .expectBody().returnResult();


        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new Adapter())
                .create()
                .fromJson(new String(result.getResponseBody()), PurchaseTransaction.class);

    }

    private void createExchange() {
        exchangeRateRepository.save(ExchangeRate.builder()
                        .country("brazil")
                        .rate(new BigDecimal("5.00"))
                        .currency("real")
                        .effectiveDate(LocalDate.now())
                        .build())
                .subscribe();
    }

    private class Adapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ZonedDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        @Override
        public JsonElement serialize(ZonedDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }
    }


}
