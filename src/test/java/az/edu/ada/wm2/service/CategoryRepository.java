package az.edu.ada.wm2.lab6.repository;

import az.edu.ada.wm2.lab6.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
