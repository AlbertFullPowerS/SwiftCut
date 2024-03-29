package utez.edu.mx.IntegradoraPodec.Model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductBean,Long> {
    Optional<ProductBean> findById (Long id);
}
