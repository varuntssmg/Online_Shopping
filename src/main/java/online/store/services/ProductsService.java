
package online.store.services;

import online.store.model.Product;
import online.store.repositories.ProductCategoryRepository;
import online.store.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductsService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductsService(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository) {

        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    // 1. Load all supported categories
    public List<String> getAllSupportedCategories() {

        return productCategoryRepository.findAll()
                .stream()
                .map(productCategory -> productCategory.getCategory())
                .collect(Collectors.toList());
    }

    // 2. Load deals of the day
    public List<Product> getDealsOfTheDay(
            int atMostNumberOfProducts) {

        return productRepository.findAtMostNumberOfProducts(
                atMostNumberOfProducts);
    }

    // 3. NEW: Load products by category
    public List<Product> getProductsByCategory(
            String productCategory) {

        return productRepository.findByCategory(productCategory);
    }
}
