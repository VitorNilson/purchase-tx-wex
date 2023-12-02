package wex.purhcasetx.exchangerate.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import wex.purhcasetx.exchangerate.model.ExchangeRate;
import wex.purhcasetx.exchangerate.service.ExchangeRateService;

@Tag(name = "Exchange Rates")
@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    @GetMapping
    @ApiResponse(responseCode = "200", description = "List all available Exchange currency.")
    public ResponseEntity<Mono<Page<ExchangeRate>>> findAll(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                                            @RequestParam(name = "size", defaultValue = "50", required = false) int size,
                                                            @RequestParam(name = "sortBy", defaultValue = "country", required = false) String sortBy,
                                                            @RequestParam(name = "direction", defaultValue = "ASC", required = false) Sort.Direction direction) {
        return ResponseEntity.ok(service.findAll(page, size, sortBy, direction));
    }


}
