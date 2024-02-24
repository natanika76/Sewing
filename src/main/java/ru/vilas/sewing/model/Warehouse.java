package ru.vilas.sewing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vilas.sewing.dto.UnitsOfMeasurement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean archive = false;             // флаг архива
    @Column(name = "nameMaterial")
    private String nameMaterial;                // наименование материала

    @Enumerated(EnumType.STRING)
    UnitsOfMeasurement unitsOfMeasurement;        // единицы измерения
    private Integer quantityOfMaterial;        // количество материала
    private String expenditure;                //расход материала
    private Integer numberOfProducts;          //количество изделий
    private Integer target;                     //задание
    private Integer balance;                    // баланс
    private Integer normPerDay;                  // норма в день
    private Integer inTotal;                    // итого
    private Integer remains;                    // остаток
    private LocalDate startWork;                   // начало работ
    private LocalDate endWork;                     // конец работ

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;


   }
