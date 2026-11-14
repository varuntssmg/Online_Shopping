
package online.store.controllers;

import online.store.model.CheckoutRequest;
import online.store.model.Order;

import online.store.services.OrdersService;
import online.store.services.ProductsService;
import online.store.services.CreditCardValidationService;

import online.store.exceptions.CreditCardValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashSet;
import java.util.Set;

@RestController
public class CheckoutController {

    private final OrdersService ordersService;
    private final ProductsService productsService;
    private final CreditCardValidationService creditCardValidationService;

    public CheckoutController(
            OrdersService ordersService,
            ProductsService productsService,
            CreditCardValidationService creditCardValidationService) {

        this.ordersService = ordersService;
        this.productsService = productsService;
        this.creditCardValidationService = creditCardValidationService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @RequestBody CheckoutRequest checkoutRequest) {

        Set<Order> orders = new HashSet<>(
                checkoutRequest.getProducts().size()
        );

        // 1. Check credit card is not empty
        if (isNullOrBlank(checkoutRequest.getCreditCard())) {
            return new ResponseEntity<>(
                    "Credit card information is missing",
                    HttpStatus.PAYMENT_REQUIRED
            );
        }

        // 2. Check first name
        if (isNullOrBlank(checkoutRequest.getFirstName())) {
            return new ResponseEntity<>(
                    "First name is missing",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 3. Check last name
        if (isNullOrBlank(checkoutRequest.getLastName())) {
            return new ResponseEntity<>(
                    "Last name is missing",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 4. Validate credit card before saving orders
        creditCardValidationService.validate(
                checkoutRequest.getCreditCard()
        );

        // 5. Create orders
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

        // 6. Save orders
        ordersService.placeOrders(orders);

        return new ResponseEntity<>(
                "success",
                HttpStatus.OK
        );
    }

    // Handle credit card validation exceptions
    @ExceptionHandler(CreditCardValidationException.class)
    public ResponseEntity<String> handleCreditCardError(Exception ex) {

        System.out.println(
                String.format(
                        "Request to /checkout path threw an exception %s",
                        ex.getMessage()
                )
        );

        return new ResponseEntity<>(
                "Credit card is invalid, please use another form of payment",
                HttpStatus.BAD_REQUEST
        );
    }

    private static boolean isNullOrBlank(String input) {
        return input == null ||
                input.isEmpty() ||
                input.trim().length() == 0;
    }
}
