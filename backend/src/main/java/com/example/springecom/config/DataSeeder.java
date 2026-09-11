package com.example.springecom.config;

import com.example.springecom.model.User;
import com.example.springecom.model.Product;
import com.example.springecom.repo.UserRepo;
import com.example.springecom.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.springecom.service.CloudinaryService cloudinaryService;

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin user if not exists
        if (userRepo.findByUsername("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@gmail.com");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("System Admin");
            admin.setRole("ROLE_ADMIN");
            userRepo.save(admin);
            System.out.println("Seeded admin account: admin@gmail.com / admin123");
        }

        // Seed regular User if not exists
        if (userRepo.findByUsername("user@gmail.com").isEmpty()) {
            User user = new User();
            user.setUsername("user@gmail.com");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setName("Regular User");
            user.setRole("ROLE_USER");
            userRepo.save(user);
            System.out.println("Seeded user account: user@gmail.com / user123");
        }

        // Seed products if database is empty
        seedProducts();
    }

    private void seedProducts() {
        boolean alreadySeeded = productRepo.findAll().stream()
                .anyMatch(p -> "MacBook Pro 16".equals(p.getName()));
        if (alreadySeeded) {
            return;
        }

        // Define categories, names, brands, descriptions, prices
        String[][] productData = {
            // Laptops
            {"MacBook Pro 16", "Apple", "Powerful laptop with M3 Max chip, 32GB RAM, 1TB SSD, Liquid Retina XDR display.", "59990000", "Laptop"},
            {"Dell XPS 15", "Dell", "Premium Windows laptop with 4K OLED touch screen, Intel Core i9, 32GB RAM, NVIDIA RTX 4060.", "48990000", "Laptop"},
            {"ThinkPad X1 Carbon", "Lenovo", "Business laptop featuring ultra-lightweight carbon fiber chassis, Intel Core i7, 16GB RAM.", "39990000", "Laptop"},
            {"ASUS ROG Zephyrus G14", "ASUS", "Compact gaming laptop with AMD Ryzen 9, NVIDIA RTX 4070, 120Hz display.", "37990000", "Laptop"},
            {"HP Spectre x360", "HP", "2-in-1 convertible laptop with stunning design, Intel Core i7, 16GB RAM, OLED touch screen.", "33990000", "Laptop"},
            {"Razer Blade 15", "Razer", "Ultra-thin gaming laptop with Intel Core i7, NVIDIA RTX 4080, 240Hz QHD display.", "67990000", "Laptop"},
            {"Acer Swift Go 14", "Acer", "Affordable OLED laptop with Intel Core i5, 16GB RAM, lightweight aluminium body.", "18990000", "Laptop"},
            {"Microsoft Surface Laptop 5", "Microsoft", "Sleek and elegant touch screen laptop with Intel Core i7, 16GB RAM, Alcantara keyboard.", "30990000", "Laptop"},
            {"MSI Creator 16 AI", "MSI", "Creator laptop with Intel Core i9, NVIDIA RTX 4070, mini-LED display for design professionals.", "52990000", "Laptop"},
            {"Lenovo Legion Pro 5", "Lenovo", "High-performance gaming laptop with AMD Ryzen 7, NVIDIA RTX 4060, RGB keyboard.", "29990000", "Laptop"},

            // Headphones
            {"Sony WH-1000XM5", "Sony", "Industry-leading noise-canceling wireless headphones with 30-hour battery life.", "8490000", "Headphone"},
            {"Bose QuietComfort Ultra", "Bose", "Premium over-ear noise-canceling headphones with immersive audio technology.", "9990000", "Headphone"},
            {"AirPods Max", "Apple", "High-fidelity wireless over-ear headphones with active noise cancellation and spatial audio.", "12990000", "Headphone"},
            {"Momentum 4 Wireless", "Sennheiser", "Audiophile-grade sound quality with outstanding 60-hour battery life.", "7990000", "Headphone"},
            {"Audio-Technica ATH-M50xBT2", "Audio-Technica", "Professional studio monitor headphones with Bluetooth capability.", "4690000", "Headphone"},
            {"JBL Live 660NC", "JBL", "Wireless over-ear noise-canceling headphones with signature JBL deep bass.", "2990000", "Headphone"},
            {"Beats Studio Pro", "Beats", "Premium wireless noise-canceling headphones with custom acoustic platform.", "7990000", "Headphone"},
            {"Jabra Elite 85h", "Jabra", "Smart active noise-canceling headphones with 36-hour battery and rain-resistant design.", "5490000", "Headphone"},
            {"HyperX Cloud III Wireless", "HyperX", "Ultra-comfortable gaming headset with DTS Headphone:X spatial audio.", "3490000", "Headphone"},
            {"Beyerdynamic DT 770 Pro", "Beyerdynamic", "Studio reference closed-back headphones, renowned for recording and monitoring.", "3890000", "Headphone"},

            // Mobiles
            {"iPhone 15 Pro Max", "Apple", "Titanium design, A17 Pro chip, 5x Telephoto camera, USB-C connector.", "29990000", "Mobile"},
            {"Galaxy S24 Ultra", "Samsung", "Titanium frame, built-in S Pen, Galaxy AI features, 200MP camera.", "30990000", "Mobile"},
            {"Pixel 8 Pro", "Google", "Pure Android experience, Google Tensor G3, advanced AI photo editing.", "21990000", "Mobile"},
            {"OnePlus 12", "OnePlus", "Flagship killer with Snapdragon 8 Gen 3, 100W fast charging, Hasselblad camera.", "17990000", "Mobile"},
            {"Xiaomi 14 Ultra", "Xiaomi", "Leica professional optics, quad-camera system, Snapdragon 8 Gen 3.", "25990000", "Mobile"},
            {"Asus ROG Phone 8 Pro", "Asus", "Ultimate gaming phone with cooling triggers, AniMe Vision, Snapdragon 8 Gen 3.", "27990000", "Mobile"},
            {"Nothing Phone (2)", "Nothing", "Unique transparent back design, Glyph Interface, Nothing OS 2.0.", "13990000", "Mobile"},
            {"Sony Xperia 1 V", "Sony", "4K HDR OLED display, professional video and photo features, Snapdragon 8 Gen 2.", "24990000", "Mobile"},
            {"Motorola Edge 50 Ultra", "Motorola", "Elegant wood back design, Snapdragon 8s Gen 3, 125W TurboPower.", "19990000", "Mobile"},
            {"Realme GT 5G", "Realme", "Affordable flagship with high performance, vegan leather finish.", "10990000", "Mobile"},

            // Electronics
            {"iPad Pro 13-inch", "Apple", "Ultra-thin design, Tandem OLED display, Apple M4 chip.", "31990000", "Electronics"},
            {"Apple Watch Ultra 2", "Apple", "Rugged sports watch with titanium case, dual-frequency GPS, 36h battery life.", "19990000", "Electronics"},
            {"Sony Bravia 65-inch 4K TV", "Sony", "Mini LED smart TV with XR processor, Dolby Vision, Acoustic Multi-Audio.", "34990000", "Electronics"},
            {"GoPro Hero 12 Black", "GoPro", "Action camera with 5.3K video, HyperSmooth 6.0 stabilization.", "9490000", "Electronics"},
            {"DJI Mini 4 Pro Fly More Combo", "DJI", "Ultralight folding camera drone with omnidirectional obstacle sensing.", "25990000", "Electronics"},
            {"Nintendo Switch OLED Model", "Nintendo", "7-inch OLED screen, wide adjustable stand, wired LAN port.", "7990000", "Electronics"},
            {"PlayStation 5 Slim", "Sony", "Ultra-high speed SSD, ray tracing, 4K-TV gaming.", "11990000", "Electronics"},
            {"Xbox Series X", "Microsoft", "True 4K gaming, 120 FPS, 1TB SSD storage.", "11990000", "Electronics"},
            {"Kindle Paperwhite 32GB", "Amazon", "Signature Edition with wireless charging, auto-adjusting front light.", "4290000", "Electronics"},
            {"Anker 737 Power Bank", "Anker", "Ultra-high capacity power bank with 140W fast charging and smart display.", "3290000", "Electronics"},

            // Toys
            {"LEGO Star Wars Millennium Falcon", "LEGO", "7500+ pieces building set, ultimate collector series model.", "19990000", "Toys"},
            {"Rubik's Connected Cube", "Rubik's", "Bluetooth-enabled smart Rubik's cube, tracks and teaches via mobile app.", "1190000", "Toys"},
            {"NERF Elite 2.0 Blaster", "NERF", "Motorized blaster with 24-dart rotating drum and custom accessories.", "890000", "Toys"},
            {"DJI Ryze Tello Drone", "DJI", "Fun toy drone for kids and adults, easy tricks, programmable with Scratch.", "2490000", "Toys"},
            {"Tamagotchi Pix", "Tamagotchi", "Interactive virtual pet with built-in camera to take photos and feed it.", "1390000", "Toys"},
            {"LEGO Technic Porsche 911 GT3 RS", "LEGO", "Supercar model building kit with realistic steering and gearbox.", "8490000", "Toys"},
            {"Hot Wheels Ultimate Garage", "Hot Wheels", "Huge playset with multi-level parking, race track, and shark attack obstacle.", "2990000", "Toys"},
            {"Settlers of Catan", "Catan Studio", "Classic strategy board game of resource trading and settlement building.", "490000", "Toys"},
            {"Sphero BOLT", "Sphero", "App-enabled robotic ball with LED matrix for learning coding and programming.", "3490000", "Toys"},
            {"Barbie Dreamhouse 2024", "Barbie", "75+ accessory playset with elevator, slide, and pool.", "4690000", "Toys"},

            // Fashion
            {"Nike Air Force 1 '07", "Nike", "Classic leather basketball shoe in clean triple-white finish.", "2890000", "Fashion"},
            {"Adidas Ultraboost Light", "Adidas", "High-performance running shoes with premium comfort and cushioning.", "4490000", "Fashion"},
            {"Levi's 501 Original Jeans", "Levi's", "Classic straight-leg denim jeans with signature button fly.", "1890000", "Fashion"},
            {"Patagonia Torrentshell 3L", "Patagonia", "Waterproof and breathable rain jacket made from recycled materials.", "3490000", "Fashion"},
            {"Casio G-Shock DW5600", "Casio", "Ultra-durable digital watch with shock and water resistance.", "1790000", "Fashion"},
            {"Ray-Ban Original Wayfarer Classic", "Ray-Ban", "Iconic sunglasses with crystal green lenses and black frames.", "3890000", "Fashion"},
            {"The North Face Borealis Backpack", "The North Face", "Comfortable backpack with dedicated laptop compartment and bungee cords.", "2390000", "Fashion"},
            {"Champion Reverse Weave Hoodie", "Champion", "Heavyweight fleece pullover hoodie with signature C logo.", "1590000", "Fashion"},
            {"Calvin Klein Boxer Briefs (3-Pack)", "Calvin Klein", "Classic comfortable cotton stretch underwear.", "990000", "Fashion"},
            {"Zara Slim Fit Blazer", "Zara", "Stylish men's suit jacket, perfect for smart-casual wear.", "2790000", "Fashion"}
        };

        Color[] colors = {
            new Color(41, 128, 185),  // Blue
            new Color(39, 174, 96),   // Green
            new Color(142, 68, 173),  // Purple
            new Color(230, 126, 34),  // Orange
            new Color(211, 84, 0),    // Dark Orange
            new Color(192, 57, 43),   // Red
            new Color(22, 160, 133),  // Teal
            new Color(44, 62, 80)     // Dark Blue/Gray
        };

        for (int i = 0; i < productData.length; i++) {
            String[] data = productData[i];
            Product product = new Product();
            product.setName(data[0]);
            product.setBrand(data[1]);
            product.setDescription(data[2]);
            product.setPrice(new BigDecimal(data[3]));
            product.setCategory(data[4]);
            
            // Random attributes
            product.setStockQuantity(10 + (i * 3) % 40);
            product.setReleaseDate(LocalDate.now().minusDays(30 + i * 2));
            product.setProductAvailable(true);
            
            // Image details
            String imgName = data[0].toLowerCase().replace(" ", "_") + ".png";
            product.setImageName(imgName);
            product.setImageType("image/png");

            // Upload high quality sample image from Unsplash to Cloudinary
            String sampleUrl = getSampleImageUrl(data[4], i);
            java.util.Map<String, String> uploadResult = cloudinaryService.uploadImageFromUrl(sampleUrl);
            
            if (uploadResult.containsKey("url") && !uploadResult.get("url").isEmpty()) {
                product.setImageUrl(uploadResult.get("url"));
                product.setImagePublicId(uploadResult.get("publicId"));
            } else {
                product.setImageUrl(sampleUrl);
            }
            
            productRepo.save(product);
        }
        
        System.out.println("Seeded " + productData.length + " diverse products into the database with Cloudinary images.");
    }

    private String getSampleImageUrl(String category, int index) {
        String[] laptopImages = {
            "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600",
            "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=600",
            "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600",
            "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600",
            "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=600"
        };
        String[] headphoneImages = {
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
            "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600",
            "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600",
            "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600"
        };
        String[] mobileImages = {
            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600",
            "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=600",
            "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=600",
            "https://images.unsplash.com/photo-1512499617640-c74ae3a79d37?w=600"
        };
        String[] electronicsImages = {
            "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600",
            "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=600",
            "https://images.unsplash.com/photo-1526738549149-8e07eca6c147?w=600",
            "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600"
        };
        String[] toyImages = {
            "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=600",
            "https://images.unsplash.com/photo-1558060370-d644479be6e7?w=600",
            "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=600"
        };
        String[] fashionImages = {
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
            "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?w=600",
            "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600"
        };

        return switch (category.toLowerCase()) {
            case "laptop" -> laptopImages[index % laptopImages.length];
            case "headphone" -> headphoneImages[index % headphoneImages.length];
            case "mobile" -> mobileImages[index % mobileImages.length];
            case "electronics" -> electronicsImages[index % electronicsImages.length];
            case "toys" -> toyImages[index % toyImages.length];
            case "fashion" -> fashionImages[index % fashionImages.length];
            default -> "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600";
        };
    }

    private byte[] generatePlaceholderImage(String text, Color bgColor) {
        try {
            BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // Fill background
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, 300, 300);
            
            // Draw text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (300 - fm.stringWidth(text)) / 2;
            int y = (300 - fm.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(text, x, y);
            g2d.dispose();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
