package org.example.demows.repository;

import org.example.demows.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ExchangeRate entity
 */
@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);

    List<ExchangeRate> findByFromCurrency(String fromCurrency);

    List<ExchangeRate> findByToCurrency(String toCurrency);

    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency")
    Optional<ExchangeRate> findExchangeRate(@Param("fromCurrency") String fromCurrency, 
                                           @Param("toCurrency") String toCurrency);
}
