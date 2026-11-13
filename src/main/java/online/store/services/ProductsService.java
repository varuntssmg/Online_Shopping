
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

    // 1. Get all supported categories
    public List<String> getAllSupportedCategories() {

        return productCategoryRepository.findAll()
                .stream()
                .map(productCategory -> productCategory.getCategory())
                .collect(Collectors.toList());
    }

    // 2. Get deals of the day
    public List<Product> getDealsOfTheDay(
            int atMostNumberOfProducts) {

        return productRepository.findAtMostNumberOfProducts(
                atMostNumberOfProducts);
    }

    // 3. Get products by category
    public List<Product> getProductsByCategory(
            String productCategory) {

        return productRepository.findByCategory(productCategory);
    }

    // 4. Get all products
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // 5. NEW: Get product by ID
    public Product getProductById(long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        String.format(
                                "Product with id %s doesn't exist", id
                        )
                ));
    }
}
