package com.ecommerce.asbeza.config;

import com.ecommerce.asbeza.dtos.ProductRequestDTO;
import com.ecommerce.asbeza.dtos.UserRequestDTO;
import com.ecommerce.asbeza.services.ProductService;
import com.ecommerce.asbeza.services.UserService;
import com.ecommerce.asbeza.types.Role;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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
                        "Hiwot Assefa", "admin@asbeza.com", "Admin@123"
                ),
                Role.ADMIN
        );

        List<UserRequestDTO> deliveryPersonnels = List.of(
                new UserRequestDTO(
                        "Yohannes Ayele", "delivery1@asbeza.com", "Delivery@123"
                ),
                new UserRequestDTO(
                        "Mulugeta Kebede", "delivery2@asbeza.com", "Delivery@123"
                ),
                new UserRequestDTO(
                        "Amina Mohammed", "delivery3@asbeza.com", "Delivery@123"
                )
        );
        deliveryPersonnels.forEach(deliveryPersonnel -> userService.registerUser(deliveryPersonnel, Role.DELIVERY_PERSONNEL));

        // Create customers
        List<UserRequestDTO> customers = List.of(
                new UserRequestDTO(
                        "Dawit Mekonnen", "customer1@asbeza.com", "Customer@123"
                ),
                new UserRequestDTO(
                        "Muluwork Tekle", "customer2@asbeza.com", "Customer@123"
                ),
                new UserRequestDTO(
                        "Genet Tesfaye", "customer3@asbeza.com", "Customer@123"
                )
        );
        customers.forEach(customer -> userService.registerUser(customer, Role.CUSTOMER));

        List<ProductRequestDTO> products = List.of(
                new ProductRequestDTO(
                        "Injera",
                        "Traditional Ethiopian flatbread, made from teff flour.",
                        "Bakery",
                        new BigDecimal("15.00"),
                        "",
                        50,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Berbere Spice",
                        "Ethiopian spice mix used in various dishes.",
                        "Spices",
                        new BigDecimal("300.00"),
                        "kg",
                        40,
                        new BigDecimal("5.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Shiro Powder",
                        "Ground chickpea flour mixed with spices, perfect for making shiro wot.",
                        "Grains",
                        new BigDecimal("180.00"),
                        "kg",
                        30,
                        new BigDecimal("10.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Mitmita Spice",
                        "Hot Ethiopian chili powder, often used as a seasoning.",
                        "Spices",
                        new BigDecimal("350.00"),
                        "kg",
                        25,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Coffee Beans",
                        "Premium Ethiopian coffee beans, known for their rich flavor.",
                        "Beverages",
                        new BigDecimal("500.00"),
                        "kg",
                        60,
                        new BigDecimal("15.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Teff Flour",
                        "Fine ground teff flour for making injera.",
                        "Grains",
                        new BigDecimal("120.00"),
                        "kg",
                        70,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Honey Wine (Tej)",
                        "Traditional Ethiopian honey wine, Tej, available in bottles.",
                        "Beverages",
                        new BigDecimal("400.00"),
                        "litre",
                        15,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Shinkurt (Onion)",
                        "Fresh Ethiopian onions, used in stews and sauces.",
                        "Vegetables",
                        new BigDecimal("50.00"),
                        "kg",
                        80,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Nech Shinkurt (Garlic)",
                        "Ethiopian garlic, used as a key ingredient in traditional dishes.",
                        "Vegetables",
                        new BigDecimal("200.00"),
                        "kg",
                        30,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Dinich (Potato)",
                        "Fresh Ethiopian potatoes, perfect for making Alicha.",
                        "Vegetables",
                        new BigDecimal("25.00"),
                        "kg",
                        100,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Tikel Gomen (Cabbage)",
                        "Fresh Ethiopian cabbage, ideal for Tikel Gomen stew.",
                        "Vegetables",
                        new BigDecimal("30.00"),
                        "kg",
                        50,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Karia (Green Chili)",
                        "Ethiopian green chilies, adding a spicy kick to dishes.",
                        "Spices",
                        new BigDecimal("80.00"),
                        "kg",
                        20,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Timatim (Tomato)",
                        "Fresh Ethiopian tomatoes, a staple for salads and sauces.",
                        "Vegetables",
                        new BigDecimal("35.00"),
                        "kg",
                        90,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Telba (Flaxseed)",
                        "Ethiopian flaxseed, perfect for making telba juice.",
                        "Grains",
                        new BigDecimal("150.00"),
                        "kg",
                        60,
                        new BigDecimal("0.0"),
                        null
                ),
                new ProductRequestDTO(
                        "Kibe (Spiced Butter)",
                        "Traditional Ethiopian spiced butter, used in many dishes.",
                        "Dairy",
                        new BigDecimal("400.00"),
                        "kg",
                        20,
                        new BigDecimal("0.0"),
                        null
                )
        );


        products.forEach(product -> productService.addProduct(
                product,
                getProductImageAsMultipartFile(product.getName())
        ));


    }

    private MultipartFile getProductImageAsMultipartFile(String productName) {
        try {
            // Map product names to resource image paths
            String imagePath = switch (productName) {
                case "Injera" -> "/productImages/injera.jpg";
                case "Berbere Spice" -> "/productImages/berbere.jpg";
                case "Mitmita Spice" -> "/productImages/mitmita.jpg";
                case "Coffee Beans" -> "/productImages/coffee.jpg";
                case "Teff Flour" -> "/productImages/teff.jpg";
                case "Honey Wine (Tej)" -> "/productImages/tej.jpg";
                case "Shinkurt (Onion)" -> "/productImages/shinkurt.jpg";
                case "Nech Shinkurt (Garlic)" -> "/productImages/garlic.jpg";
                case "Dinich (Potato)" -> "/productImages/dinich.jpg";
                case "Tikel Gomen (Cabbage)" -> "/productImages/tikel_gomen.jpg";
                case "Karia (Green Chili)" -> "/productImages/karia.jpg";
                case "Timatim (Tomato)" -> "/productImages/timatim.jpg";
                case "Telba (Flaxseed)" -> "/productImages/telba.jpg";
                case "Kibe (Spiced Butter)" -> "/productImages/kibe.jpg";
                case "Shiro Powder" -> "/productImages/shiro.jpg";
                default -> null;
            };

            // Read the image file from resources
            InputStream inputStream = null;
            if (imagePath != null) {
                inputStream = getClass().getResourceAsStream(imagePath);
            }

            if (inputStream == null) {
                return null;
            }

            // Create a MockMultipartFile with the input stream data
            byte[] content = IOUtils.toByteArray(inputStream);
            return new MockMultipartFile("file", productName + ".jpg", "image/jpeg", content);

        } catch (Exception e) {
            System.err.println("Failed to load image for product: " + productName);
            return null;
        }
    }
}
