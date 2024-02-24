package ru.vilas.sewing.repository;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vilas.sewing.model.Warehouse;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {


    @Query("SELECT o FROM Warehouse o WHERE (:customerId is null or :customerId = 0 or o.customer.id = :customerId) AND o.archive = false")
    List<Warehouse> searchByActiveWarehouseArchiveAndCustomerId(@Param("customerId") Long customerId);

    @Transactional
    @Modifying
    @Query("UPDATE Warehouse w SET w.archive = true WHERE w.id = :warehouseId")
    void sendToArchive(@Param("warehouseId") Long warehouseId);

}



