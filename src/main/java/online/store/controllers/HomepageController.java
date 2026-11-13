
package online.store.controllers;

import online.store.model.wrappers.ProductsWrapper;
import online.store.services.ProductsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class HomepageController {

    private final ProductsService productsService;

    // Constructor Dependency Injection
    public HomepageController(ProductsService productsService) {
        this.productsService = productsService;
    }

    // 1. Get product categories
    @GetMapping("/categories")
    public String getProductCategories() {

        return productsService.getAllSupportedCategories()
                .stream()
                .collect(Collectors.joining(","));
    }

    // 2. Get deals of the day
    @GetMapping("/deals_of_the_day/{number_of_products}")
    public ProductsWrapper getDealsOfTheDay(
            @PathVariable(name = "number_of_products")
            int numberOfProducts) {

        return new ProductsWrapper(
                productsService.getDealsOfTheDay(numberOfProducts)
        );
    }

    // 3. Get products by category
    @GetMapping("/products")
    public ProductsWrapper getProductsForCategory(
            @RequestParam(name = "category") String category) {

        return new ProductsWrapper(
                productsService.getProductsByCategory(category)
        );
    }
}
