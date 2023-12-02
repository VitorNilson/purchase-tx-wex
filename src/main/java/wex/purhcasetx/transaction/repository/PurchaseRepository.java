package wex.purhcasetx.transaction.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wex.purhcasetx.transaction.model.PurchaseTransaction;

import java.time.ZonedDateTime;


@Repository
public interface PurchaseRepository extends ReactiveSortingRepository<PurchaseTransaction, String>, ReactiveCrudRepository<PurchaseTransaction, String> {

    Flux<PurchaseTransaction> findAllBy(Pageable pageable);

    @Query("select * from tb_purchase_transaction p where " +
            " ((?1 is null ) or (p.transaction_date >= ?1)) " +
            "and ((?2 is null) or (p.transaction_date <= ?2)) " +
            "ORDER BY " +
            "CASE WHEN ?3 = 'ASC' THEN p.transaction_date END ASC, " +
            "CASE WHEN ?3 = 'DESC' THEN p.transaction_date END DESC " +
            "LIMIT (?4) OFFSET (?4 * ?5) ")
    Flux<PurchaseTransaction> findAllByStartAndEndDate(ZonedDateTime start, ZonedDateTime end, String direction, int size, int page);

    @Query("select count(*) from tb_purchase_transaction p where ((?1 is null ) or (p.transaction_date >= ?1)) and ((?2 is null) or (p.transaction_date <= ?2)) ")
    Mono<Integer> countBy(ZonedDateTime start, ZonedDateTime end);
}
