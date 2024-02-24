package ru.vilas.sewing.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vilas.sewing.model.Category;
import ru.vilas.sewing.model.Task;
import ru.vilas.sewing.model.User;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCategory(Category category);

    List<Task> findByCategoryIdOrderByName(Long categoryId);

    @NotNull
    @Query("SELECT t FROM Task t  ORDER BY t.name")
    List<Task> findAll();
}
