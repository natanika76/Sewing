package ru.vilas.sewing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vilas.sewing.dto.TaskTypes;
import ru.vilas.sewing.dto.WorkedByDate;
import ru.vilas.sewing.dto.WorkedDto;
import ru.vilas.sewing.model.Category;
import ru.vilas.sewing.model.OperationData;
import ru.vilas.sewing.model.User;
import ru.vilas.sewing.repository.OperationDataRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class OperationsListSpecialServiceImpl {
    private final OperationDataRepository operationDataRepository;
    private final CategoryService categoryService;
    private final CustomUserDetailsService customUserDetailsService;
    @Autowired
    public OperationsListSpecialServiceImpl(OperationDataRepository operationDataRepository, CategoryService categoryService, CustomUserDetailsService customUserDetailsService) {
        this.operationDataRepository = operationDataRepository;
        this.categoryService = categoryService;
        this.customUserDetailsService = customUserDetailsService;
    }
    public List<WorkedDto> getWorkedDtosList(LocalDate startDate, LocalDate endDate) {

        List<User> users = customUserDetailsService.getAllUsers();
        List<WorkedDto> workedDtos = new ArrayList<>();
        List<Category> categories = categoryService.getAllCategories()
                .stream()
                .filter(Category::isActive) // Фильтрация по активным категориям
                .toList();
        for (User user: users) {
            if (user.getRoles().stream().anyMatch(role -> !role.getName().equals("ROLE_USER"))) {
                continue;
            }

            for (Category category : categories) {
                WorkedDto workedDto = new WorkedDto();

                workedDto.setSeamstressId(user.getId());
                workedDto.setSeamstressName(user.getName());
                workedDto.setCategory(category);
                workedDto.setWorkedByDateList(getWorkedByDateList(startDate, endDate, user.getId(), category));
                workedDto.setTotalWorked(workedSum(workedDto.getWorkedByDateList()));

                workedDtos.add(workedDto);
            }
        }
        return workedDtos;
    }

    public List<WorkedByDate> getWorkedByDateList(LocalDate startDate, LocalDate endDate, Long seamstressId,
                                              Category category) {
        List<OperationData> operationDataList = operationDataRepository.findBetweenDatesAndBySeamstressAndCategory(startDate, endDate, seamstressId, category);

        List<WorkedByDate> workedByDateList = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            WorkedByDate workedByDate = new WorkedByDate();

            workedByDate.setDate(currentDate);

            LocalDate finalCurrentDate = currentDate;

            workedByDate.setQuantitativeWorked(
                    operationDataList.stream()
                            .filter(operationData -> operationData.getDate().equals(finalCurrentDate))
                            .filter(operationData -> TaskTypes.QUANTITATIVE.equals(operationData.getTaskType()))
                            .mapToInt(OperationData::getCompletedOperations)
                            .sum()
            );

            workedByDate.setHourlyWorked(
                    operationDataList.stream()
                            .filter(operationData -> operationData.getDate().equals(finalCurrentDate))
                            .filter(operationData -> TaskTypes.HOURLY.equals(operationData.getTaskType()))
                            .map(OperationData::getHoursWorked)
                            .reduce(Duration.ZERO, Duration::plus)
            );

            workedByDate.setHourlyWorkedToString(String.format("%02d:%02d",
                    workedByDate.getHourlyWorked().toHours(),
                    workedByDate.getHourlyWorked().toMinutesPart()
            ));
//workedByDate.setHourlyWorkedToString(workedByDate.getHourlyWorked().stream().map(d -> String.format("%02d:%02d", d.toHours(), d.toMinutesPart()))
//                    .orElse("00:00"));

            workedByDate.setPackagingWorked(
                    operationDataList.stream()
                            .filter(operationData -> operationData.getDate().equals(finalCurrentDate))
                            .filter(operationData -> TaskTypes.PACKAGING.equals(operationData.getTaskType()))
                            .mapToInt(OperationData::getCompletedOperations)
                            .sum()
            );

            workedByDateList.add(workedByDate);
            currentDate = currentDate.plusDays(1);
        }

        return workedByDateList;
    }
    public static WorkedByDate workedSum(List<WorkedByDate> workedByDateList) {
        WorkedByDate sumWorkedByDate = new WorkedByDate();
        sumWorkedByDate.setDate(null);
        sumWorkedByDate.setQuantitativeWorked(
                workedByDateList.stream()
                        .mapToInt(WorkedByDate::getQuantitativeWorked)
                        .sum()
        );
        sumWorkedByDate.setHourlyWorked(
                workedByDateList.stream()
                        .map(WorkedByDate::getHourlyWorked)
                        .reduce(Duration.ZERO, Duration::plus)
        );

        sumWorkedByDate.setHourlyWorkedToString(String.format("%02d:%02d",
                sumWorkedByDate.getHourlyWorked().toHours(),
                sumWorkedByDate.getHourlyWorked().toMinutesPart()
        ));


        sumWorkedByDate.setPackagingWorked(
                workedByDateList.stream()
                        .mapToInt(WorkedByDate::getPackagingWorked)
                        .sum()
        );
        return sumWorkedByDate;
    }

}
