package ru.vilas.sewing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vilas.sewing.model.Category;
import ru.vilas.sewing.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

