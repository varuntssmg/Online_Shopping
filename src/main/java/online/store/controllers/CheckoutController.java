
package online.store.controllers;

import online.store.model.CheckoutRequest;
import online.store.model.Order;

import online.store.services.OrdersService;
import online.store.services.ProductsService;
import online.store.services.CreditCardValidationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> checkout(
            @RequestBody CheckoutRequest checkoutRequest) {

        // Step 1: Validate credit card information
        if (isNullOrBlank(checkoutRequest.getCreditCard())) {

            return new ResponseEntity<>(
                    "Credit card information is missing",
                    HttpStatus.PAYMENT_REQUIRED
            );
        }

        // Step 2: Validate first name
        if (isNullOrBlank(checkoutRequest.getFirstName())) {

            return new ResponseEntity<>(
                    "First name is missing",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Step 3: Validate last name
        if (isNullOrBlank(checkoutRequest.getLastName())) {

            return new ResponseEntity<>(
                    "Last name is missing",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Step 4: Validate credit card format and stolen card status
        creditCardValidationService.validate(
                checkoutRequest.getCreditCard()
        );

        // Step 5: Create orders
        Set<Order> orders = new HashSet<>(
                checkoutRequest.getProducts().size()
        );

        for (CheckoutRequest.ProductInfo productInfo :
                checkoutRequest.getProducts()) {

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

        // Step 6: Save orders
        ordersService.placeOrders(orders);

        // Step 7: Return success
        return new ResponseEntity<>(
                "success",
                HttpStatus.OK
        );
    }

    // Helper method to check null, empty and blank strings
    private static boolean isNullOrBlank(String input) {

        return input == null ||
                input.isEmpty() ||
                input.trim().length() == 0;
    }
}
