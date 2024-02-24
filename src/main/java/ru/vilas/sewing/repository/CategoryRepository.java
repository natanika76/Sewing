package ru.vilas.sewing.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vilas.sewing.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Object> findByName(String categoryId);

    @NotNull
    @Query("SELECT c FROM Category c ORDER BY c.name")
    List<Category> findAll();

}

