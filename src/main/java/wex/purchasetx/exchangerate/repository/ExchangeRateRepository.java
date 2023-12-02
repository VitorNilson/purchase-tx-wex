package wex.purchasetx.exchangerate.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wex.purchasetx.exchangerate.model.ExchangeRate;

import java.time.LocalDate;

@Repository
public interface ExchangeRateRepository extends ReactiveCrudRepository<ExchangeRate, String> {

    @Modifying
    Mono<Void> deleteByEffectiveDateLessThanEqual(LocalDate delete);

    Flux<ExchangeRate> findAllBy(Pageable pageable);

}
