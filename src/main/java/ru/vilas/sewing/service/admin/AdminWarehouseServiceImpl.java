package ru.vilas.sewing.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vilas.sewing.dto.SizeByDateDto;
import ru.vilas.sewing.model.SizeByDate;
import ru.vilas.sewing.dto.WarehouseDto;
import ru.vilas.sewing.model.Category;
import ru.vilas.sewing.model.Customer;
import ru.vilas.sewing.model.Warehouse;
import ru.vilas.sewing.repository.SizeByDateRepository;
import ru.vilas.sewing.repository.WarehouseRepository;
import ru.vilas.sewing.service.CategoryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminWarehouseServiceImpl implements AdminWarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final CategoryService categoryService;
    private final AdminCustomerService customer;
    private final SizeByDateRepository sizeByDateRepository;

    @Autowired
    public AdminWarehouseServiceImpl(WarehouseRepository warehouseRepository, CategoryService categoryService, AdminCustomerService customer, SizeByDateRepository sizeByDateRepository) {
        this.warehouseRepository = warehouseRepository;
        this.categoryService = categoryService;
        this.customer = customer;
        this.sizeByDateRepository = sizeByDateRepository;
    }
    @Override
    public List<WarehouseDto> showAllActiveTasksCut(Long customerId, Long categoryId) {
        List<WarehouseDto> warehouseDtos = new ArrayList<>();
        List<Warehouse> warehouseAllDate = warehouseRepository.searchByActiveWarehouseArchiveAndCustomerId(customerId);

        for (int i = 0; i < warehouseAllDate.size(); i++) {
            WarehouseDto warehouseDto = new WarehouseDto();

            warehouseDto.setId(warehouseAllDate.stream()
                    .map(Warehouse::getId)
                    .collect(Collectors.toList()).get(i));

            warehouseDto.setCustomerName(warehouseAllDate.stream()
                    .map(Warehouse::getCustomer)
                    .collect(Collectors.toList()).get(i).getName().toString());

            warehouseDto.setCategoryName(warehouseAllDate.stream()
                    .map(Warehouse::getCategory)
                    .collect(Collectors.toList()).get(i).getName().toString());


            warehouseDto.setNameMaterial(warehouseAllDate.stream()
                    .map(Warehouse::getNameMaterial)
                    .collect(Collectors.toList()).get(i).toString());

            warehouseDto.setTarget(warehouseAllDate.stream()
                    .map(Warehouse::getTarget)
                    .collect(Collectors.toList()).get(i));


            warehouseDtos.add(warehouseDto);
        }

        return warehouseDtos;
    }
    @Override
    public Warehouse convertToEntity(WarehouseDto warehouseDto) {
        Warehouse warehouse = new Warehouse();

        // Устанавливаем значения атрибутов сущности Warehouse из объекта WarehouseDto

        warehouse.setNameMaterial(warehouseDto.getNameMaterial());
        warehouse.setUnitsOfMeasurement(warehouseDto.getUnitsOfMeasurement());
        warehouse.setQuantityOfMaterial(warehouseDto.getQuantityOfMaterial());
        warehouse.setExpenditure(warehouseDto.getExpenditure());
        warehouse.setNumberOfProducts(warehouseDto.getNumberOfProducts());
        warehouse.setTarget(warehouseDto.getTarget());
        warehouse.setBalance(warehouseDto.getBalance());
        warehouse.setNormPerDay(warehouseDto.getNormPerDay());
        warehouse.setInTotal(warehouseDto.getInTotal());
        warehouse.setRemains(warehouseDto.getRemains());
        warehouse.setStartWork(warehouseDto.getStartWork());
        warehouse.setEndWork(warehouseDto.getEndWork());
        warehouse.setCustomer(warehouse.getCustomer());
        warehouse.setCategory(warehouse.getCategory());
        // Преобразуем категорию и заказчика из DTO в сущности, если они присутствуют
        if (warehouseDto.getCustomer() != null) {
            Customer customer = new Customer();
            customer.setId(warehouseDto.getCustomer().getId()); // предполагается, что getId() возвращает идентификатор заказчика
            // дополнительно может потребоваться установить другие атрибуты заказчика
            warehouse.setCustomer(customer);
        }
        if (warehouseDto.getCategory() != null) {
            Category category = new Category();
            category.setId(warehouseDto.getCategory().getId()); // предполагается, что getId() возвращает идентификатор категории
            // дополнительно может потребоваться установить другие атрибуты категории
            warehouse.setCategory(category);
        }

        Long id = warehouseRepository.save(warehouse).getId();
        List<Integer> height = warehouseDto.getHeight();
        List<String> size = warehouseDto.getSize();
        List<Integer> quantity = warehouseDto.getQuantity();

        for(int i = 0; i < size.size(); i++){
            SizeByDate sizeByDate = new SizeByDate();
            sizeByDate.setSize(size.get(i));
            sizeByDate.setHeight(height.get(i));
            sizeByDate.setQuantity(quantity.get(i));
            sizeByDate.setWarehouseId(id);
            sizeByDateRepository.save(sizeByDate);
        }
        return warehouse;
    }
    @Override
    public Warehouse getWarehouseById(Long id){return warehouseRepository.findById(id).orElse(null);}
    @Override
    public List<SizeByDateDto> getSizeByDateById(Long id){
        List<SizeByDateDto>sizeByDateDtos = new ArrayList<>();
        List<SizeByDate> sizeByDates = sizeByDateRepository.findByWarehouseId(id);

        for(int i = 0; i < sizeByDates.size(); i++){
            SizeByDateDto sizeByDateDto = new SizeByDateDto();
            sizeByDateDto.setSize(sizeByDates.stream()
                    .map(SizeByDate::getSize)
                    .collect(Collectors.toList()).get(i).toString());
            sizeByDateDto.setHeight(sizeByDates.stream()
                    .map(SizeByDate::getHeight)
                    .collect(Collectors.toList()).get(i));
            sizeByDateDto.setQuantity(sizeByDates.stream()
                    .map(SizeByDate::getQuantity)
                    .collect(Collectors.toList()).get(i));
            sizeByDateDtos.add(sizeByDateDto);
        }
        return sizeByDateDtos;
    }
    @Override
    public void updatedWarehouse(WarehouseDto warehouseDto){

        Warehouse updatedWarehouse = warehouseRepository.findById(warehouseDto.getId()).orElse(null);

        if (updatedWarehouse  != null) {
            updatedWarehouse.setId(warehouseDto.getId());
            updatedWarehouse.setNameMaterial(warehouseDto.getNameMaterial());
            updatedWarehouse.setQuantityOfMaterial(warehouseDto.getQuantityOfMaterial());
            updatedWarehouse.setExpenditure(warehouseDto.getExpenditure());
            updatedWarehouse.setNumberOfProducts(warehouseDto.getNumberOfProducts());
            updatedWarehouse.setTarget(warehouseDto.getTarget());
            updatedWarehouse.setBalance(warehouseDto.getBalance());
            updatedWarehouse.setNormPerDay(warehouseDto.getNormPerDay());
            updatedWarehouse.setInTotal(warehouseDto.getInTotal());
            updatedWarehouse.setRemains(warehouseDto.getRemains());
            updatedWarehouse.setStartWork(warehouseDto.getStartWork());
            updatedWarehouse.setEndWork(warehouseDto.getEndWork());
            updatedWarehouse.setUnitsOfMeasurement(warehouseDto.getUnitsOfMeasurement());

            Long id =warehouseDto.getId();
            List<Integer> height = warehouseDto.getHeight();
            List<String> size = warehouseDto.getSize();
            List<Integer> quantity = warehouseDto.getQuantity();

            sizeByDateRepository.deleteByWarehouseId(id);
            for(int i = 0; i < size.size(); i++){
                if(quantity.get(i) == 0) {
                    continue;
                }
                SizeByDate sizeByDate = new SizeByDate();
                sizeByDate.setSize(size.get(i));
                sizeByDate.setHeight(height.get(i));
                sizeByDate.setQuantity(quantity.get(i));
                sizeByDate.setWarehouseId(id);
                sizeByDateRepository.save(sizeByDate);
            }
            warehouseRepository.save(updatedWarehouse);
        }
    }

}

