package wex.purchasetx.transaction.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import wex.purchasetx.common.exception.ApiError;
import wex.purchasetx.common.exception.ConstraintViolationResponse;
import wex.purchasetx.transaction.dto.PurchaseTransactionResponseDto;
import wex.purchasetx.transaction.model.PurchaseTransaction;
import wex.purchasetx.transaction.service.PurchaseService;

import java.net.URI;
import java.time.ZonedDateTime;

@Tag(name = "Purchase Transactions")
@RestController
@RequestMapping("/purchase-transactions")
@RequiredArgsConstructor
public class PurchaseTransactionController {

    private final PurchaseService purchaseService;

    @PostMapping
    @Operation(summary = "Create a Purchase Transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase Transaction created.",
                    content = @Content(schema = @Schema(implementation = PurchaseTransaction.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400",
                    content = @Content(schema = @Schema(implementation = ConstraintViolationResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                    description = "One or more provided attributes violated constraints."),
    })
    public Mono<ResponseEntity<PurchaseTransaction>> create(@RequestBody PurchaseTransaction purchaseTransaction, ServerHttpRequest serverRequest) {

        return purchaseService.create(purchaseTransaction)
                .map(transaction -> ResponseEntity.created(URI.create(serverRequest.getURI().getRawPath() + "/" + transaction.getTransactionId())).body(transaction));
    }

    @GetMapping
    @Operation(summary = "Find all Purchase Transactions.")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Page.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ConstraintViolationResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE),
                            description = "One or more provided parameters are not in the right format."),
                    @ApiResponse(responseCode = "400", description = "Parameter 'country' is mandatory."),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ApiError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                            , description = "Provided country's currency is not supported.")
            })
    public ResponseEntity<Mono<Page<PurchaseTransactionResponseDto>>> findAll(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                                              @RequestParam(name = "size", defaultValue = "50", required = false) int size,
                                                                              @RequestParam(name = "sortBy", defaultValue = "transactionDate", required = false) String sortBy,
                                                                              @RequestParam(name = "direction", defaultValue = "ASC", required = false) Sort.Direction direction,
                                                                              @RequestParam(name = "start", required = false) ZonedDateTime start,
                                                                              @RequestParam(name = "end", required = false) ZonedDateTime end,
                                                                              @RequestParam(name = "country") String country
    ) {
        return ResponseEntity.ok(purchaseService.findAll(page, size, sortBy, direction, start, end, country.toLowerCase().trim()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find Purchase Transaction by Id.")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Page.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Parameter 'country' is mandatory."),
                    @ApiResponse(responseCode = "404",
                            content = @Content(schema = @Schema(implementation = ApiError.class), mediaType = MediaType.APPLICATION_JSON_VALUE)
                            , description = "Provided country's currency is not supported.")
            })
    public ResponseEntity<Mono<PurchaseTransactionResponseDto>> findAll(@PathVariable("id") String id, @RequestParam(name = "country") String country) {
        return ResponseEntity.ok(purchaseService.findById(id, country));
    }
}
