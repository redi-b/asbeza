package com.ecommerce.asbeza.config;

import com.ecommerce.asbeza.dtos.ProductRequestDTO;
import com.ecommerce.asbeza.dtos.UserRequestDTO;
import com.ecommerce.asbeza.services.ProductService;
import com.ecommerce.asbeza.services.UserService;
import com.ecommerce.asbeza.types.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final ProductService productService;

    @Override
    public void run(String... args) {

        // Create ADMIN user if not exists
        userService.registerUser(
                new UserRequestDTO(
                        "Admin", "admin@asbeza.com", "Admin@123"
                ),
                Role.ADMIN
        );

        List<String> deliveryEmails = List.of("delivery1@asbeza.com", "delivery2@asbeza.com", "delivery3@asbeza.com");
        for (String email : deliveryEmails) {
            userService.registerUser(
                    new UserRequestDTO(
                            "Delivery Personnel", email, "Delivery@123"
                    ),
                    Role.DELIVERY_PERSONNEL
            );
        }

        // Create customers
        List<String> customerEmails = List.of("customer1@asbeza.com", "customer2@asbeza.com", "customer3@asbeza.com");
        for (String email : customerEmails) {
            userService.registerUser(
                    new UserRequestDTO(
                            "Customer", email, "Customer@123"
                    ),
                    Role.CUSTOMER
            );
        }

        List<ProductRequestDTO> products = List.of(
                new ProductRequestDTO(
                        "Injera",
                        "Traditional Ethiopian flatbread, made from teff flour.",
                        "Bakery",
                        new BigDecimal("15.00"),
                        "",
                        50,
                        new BigDecimal("0.0"),
                        "https://fakeimg.pl/100x100?text=Injera"
                ),
                new ProductRequestDTO(
                        "Berbere Spice",
                        "Ethiopian spice mix used in various dishes.",
                        "Spices",
                        new BigDecimal("300.00"),
                        "kg",
                        40,
                        new BigDecimal("5.0"),
                        "https://fakeimg.pl/100x100?text=Berbere"
                ),
                new ProductRequestDTO(
                        "Shiro Powder",
                        "Ground chickpea flour mixed with spices, perfect for making shiro wot.",
                        "Grains",
                        new BigDecimal("180.00"),
                        "kg",
                        30,
                        new BigDecimal("10.0"),
                        "https://fakeimg.pl/100x100?text=Shiro"
                ),
                new ProductRequestDTO(
                        "Mitmita Spice",
                        "Hot Ethiopian chili powder, often used as a seasoning.",
                        "Spices",
                        new BigDecimal("350.00"),
                        "kg",
                        25,
                        new BigDecimal("0.0"),
                        "https://fakeimg.pl/100x100?text=Mitmita"
                ),
                new ProductRequestDTO(
                        "Coffee Beans",
                        "Premium Ethiopian coffee beans, known for their rich flavor.",
                        "Beverages",
                        new BigDecimal("500.00"),
                        "kg",
                        60,
                        new BigDecimal("15.0"),
                        "https://fakeimg.pl/100x100?text=Coffee"
                ),
                new ProductRequestDTO(
                        "Teff Flour",
                        "Fine ground teff flour for making injera.",
                        "Grains",
                        new BigDecimal("120.00"),
                        "kg",
                        70,
                        new BigDecimal("0.0"),
                        "https://fakeimg.pl/100x100?text=Teff"
                ),
                new ProductRequestDTO(
                        "Honey Wine (Tej)",
                        "Traditional Ethiopian honey wine, Tej, available in bottles.",
                        "Beverages",
                        new BigDecimal("400.00"),
                        "kg",
                        15,
                        new BigDecimal("0.0"),
                        "https://fakeimg.pl/100x100?text=Tej"
                )
        );

        products.forEach(product -> productService.addProduct(product, null));


    }
}
