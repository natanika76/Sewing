package ru.vilas.sewing.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.vilas.sewing.dto.EarningsDto;
import ru.vilas.sewing.dto.SeamstressDto;
import ru.vilas.sewing.model.Category;
import ru.vilas.sewing.model.Customer;
import ru.vilas.sewing.service.CategoryServiceImpl;
import ru.vilas.sewing.service.CustomUserDetailsService;
import ru.vilas.sewing.service.CustomerService;
import ru.vilas.sewing.service.OperationDataService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class OneSeamstressController {

    private final OperationDataService operationDataService;
    private final CategoryServiceImpl categoryService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomerService customerService;
    public OneSeamstressController(OperationDataService operationDataService, CategoryServiceImpl categoryService, CustomUserDetailsService customUserDetailsService, CustomerService customerService) {
        this.operationDataService = operationDataService;
        this.categoryService = categoryService;
        this.customUserDetailsService = customUserDetailsService;

        this.customerService = customerService;
    }

    @GetMapping("/seamstressReport")

    public String getSeamstressReport(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "category", required = false) Long category,
            Authentication authentication,
            Model model) {

        Object principal =  authentication.getPrincipal();
        String userName = null;

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userName = userDetails.getUsername();
        }

        // Получаем идентификатор пользователя
        Long currentUserId = customUserDetailsService.getUserIdByUsername(userName);


        // Если параметры не переданы, устанавливаем значения по умолчанию
        if (endDate == null) {
            endDate = LocalDate.now().with(DayOfWeek.THURSDAY);
        }

        if (startDate == null) {
            startDate = endDate.minusDays(6);
        }

        List<String> headers = operationDataService.getDatesInPeriod(startDate, endDate);
        SeamstressDto seamstressDto = operationDataService.getSeamstressDto(startDate, endDate);

        List<Category> categories = categoryService.getAllCategories()
                .stream()
                .filter(Category::isActive)
                .collect(Collectors.toList());

        List<Category> categoryList = new ArrayList<>();
        if(category != null && category != 0){
            categoryList.add(categoryService.getCategoryById(category));
        } else {
            categoryList = categories;
        }


        List<EarningsDto> earningsDtos = operationDataService.getEarningsDtosList(startDate, endDate, categoryList);

        List<EarningsDto> earningsDtosList;

        if (category == null || category == 0) {
            earningsDtosList = operationDataService.getCommonEarningsDtosList(startDate, endDate);
        } else {
            earningsDtosList = earningsDtos.stream().filter(dto -> dto.getCategory().getId().equals(category))
                    .collect(Collectors.toList());
        }

        BigDecimal salarySum = earningsDtosList.stream()
                .map(EarningsDto::getSalary)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal totalAmountSum = earningsDtosList.stream()
                .map(EarningsDto::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_HALF_UP);


        model.addAttribute("categories", categories);
        model.addAttribute("seamstressDto", seamstressDto);
        model.addAttribute("tableHeaders", headers);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("earningsList", earningsDtosList);
        model.addAttribute("selectedCategoryId", category);

        model.addAttribute("salarySum", salarySum);
        model.addAttribute("totalAmountSum", totalAmountSum);
        model.addAttribute("currentUserId", currentUserId);

        return "seamstressReport";
    }

    @GetMapping("/cuttingTaskReport")
public String cuttingReport(Model model,Authentication authentication,
                            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam(name = "customerId", required = false) Long customerId,
                            @RequestParam(name = "categoryId", required = false) Long categoryId){

        Object principal =  authentication.getPrincipal();
        String userName = null;

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userName = userDetails.getUsername();
        }
        // Получаем идентификатор пользователя
        String currentUserName = customUserDetailsService.getNameByUsername(userName);


        // Если параметры не переданы, устанавливаем значения по умолчанию
        if (date == null) {
            date = LocalDate.now();
        }
        List<Customer> customers = customerService.getAllCustomers();
        customers.sort(Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER));
                //Собираем лист категорий в зависимости от фильтров
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> categories = new ArrayList<>();
        if ((customerId == null || customerId == 0) && (categoryId == null || categoryId == 0)) {
            categories = allCategories;
        } else if (customerId != null && customerId != 0 && (categoryId == null || categoryId == 0)) {
            categories = allCategories.stream().filter(c -> Objects.equals(c.getCustomer().getId(), customerId)).toList();
        } else {
            categories.add(categoryService.getCategoryById(categoryId));
        }



        //  model.addAttribute("earningsList", earningsDtosList);


        model.addAttribute("currentUserName",currentUserName);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", allCategories);
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("customers", customers);
       //model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("date", date);

    return "cuttingTaskReport";
    }
}

