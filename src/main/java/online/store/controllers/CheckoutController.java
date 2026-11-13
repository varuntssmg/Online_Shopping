
package online.store.controllers;

import online.store.model.CheckoutRequest;
import online.store.model.Order;

import online.store.services.OrdersService;
import online.store.services.ProductsService;
import online.store.services.CreditCardValidationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class CheckoutController {

    private final OrdersService ordersService;
    private final ProductsService productsService;
    private final CreditCardValidationService creditCardValidationService;

    // Constructor Dependency Injection
    public CheckoutController(
            OrdersService ordersService,
            ProductsService productsService,
            CreditCardValidationService creditCardValidationService) {

        this.ordersService = ordersService;
        this.productsService = productsService;
        this.creditCardValidationService = creditCardValidationService;
    }

    // Complete purchase API
    @PostMapping("/checkout")
    public String checkout(
            @RequestBody CheckoutRequest checkoutRequest) {

        // Step 1: Validate credit card
        creditCardValidationService.validate(
                checkoutRequest.getCreditCard()
        );

        // Step 2: Create a collection of orders
        Set<Order> orders = new HashSet<>(
                checkoutRequest.getProducts().size()
        );

        // Step 3: Process each purchased product
        for (CheckoutRequest.ProductInfo productInfo :
                checkoutRequest.getProducts()) {

            // Get product details using product ID
            Order order = new Order(
                    checkoutRequest.getFirstName(),
                    checkoutRequest.getLastName(),
                    checkoutRequest.getEmail(),
                    checkoutRequest.getShippingAddress(),
                    productInfo.getQuantity(),
                    productsService.getProductById(
                            productInfo.getProductId()
                    ),
                    checkoutRequest.getCreditCard()
            );

            orders.add(order);
        }

        // Step 4: Save all orders
        ordersService.placeOrders(orders);

        // Step 5: Return success
        return "success";
    }
}
