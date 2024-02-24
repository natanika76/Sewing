package ru.vilas.sewing.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vilas.sewing.model.SizeByDate;

import java.util.List;

public interface SizeByDateRepository extends JpaRepository<SizeByDate, Long> {
    @Query("SELECT s FROM SizeByDate s WHERE s.warehouseId = :warehouseId")
    List<SizeByDate> findByWarehouseId(@Param("warehouseId") Long warehouseId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SizeByDate s WHERE s.warehouseId = :warehouseId")
    void deleteByWarehouseId(@Param("warehouseId") Long warehouseId);


}
