
package az.edu.ada.wm2.lab6.repository;

import az.edu.ada.wm2.lab6.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Custom queries
    List<Product> findByExpirationDateBefore(LocalDate date);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
