/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package productmanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
import java.util.Comparator;


/**
 *
 * @author User
 */
public class InventoryManager {
    private List<Product> inventory;
    private Connection connection;

    public InventoryManager() {
        this.inventory = new ArrayList<>();
        this.connection = database.initializeDatabaseConnection();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== E-Commerce Inventory Management System ===");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Sort Products");
            System.out.println("4. Search Product");
            System.out.println("5. Filter Products");
            System.out.println("6. Backup and Restore");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    sortProducts(scanner);
                    break;
                case 4:
                    searchProduct(scanner);
                    break;
                case 5:
                    filterProducts(scanner);
                    break;
                case 6:
                    backupAndRestore(scanner);
                    break;
                case 7:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

     private void addProduct(Scanner scanner) {
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Product Price: ");
        double price = scanner.nextDouble();

        System.out.print("Enter Product Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.print("Enter Product Category: ");
        String category = scanner.nextLine();

        if (price < 0 || quantity < 0) {
            System.out.println("Price and Quantity cannot be negative.");
            return;
        }

        inventory.add(new Product(id, name, price, quantity, category));
        System.out.println("Product added successfully!");

        // Save to database
        try {
            String query = "INSERT INTO product (productID, name, price, quantity, category) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, category);
            pstmt.executeUpdate();
            System.out.println("Product saved to database successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to save product to database: " + e.getMessage());
        }
    }

    private void viewProducts() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            System.out.println("\n=== Product List ===");
            for (Product product : inventory) {
                System.out.println(product);
            }
        }

        // Fetch from database
        try {
            String query = "SELECT * FROM product";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n=== Products in Database ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("productID") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price") + ", Quantity: " + rs.getInt("quantity") + ", Category: " + rs.getString("category"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve products from database: " + e.getMessage());
        }
    }

    private void sortProducts(Scanner scanner) {
    // Fetch products from the database
    inventory.clear(); // Clear the current inventory to avoid duplicates
    try {
        String query = "SELECT * FROM product";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            inventory.add(new Product(
                rs.getString("productID"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getString("category")
            ));
        }
    } catch (SQLException e) {
        System.out.println("Failed to fetch products from the database: " + e.getMessage());
        return;
    }

    if (inventory.isEmpty()) {
        System.out.println("No products in the inventory to sort.");
        return;
    }

    // Ask for sorting criteria
    System.out.println("Choose sorting method: ");
    System.out.println("1. By Price ");
    System.out.println("2. By Name ");
    System.out.println("3. By Quantities");
    System.out.print("Enter your choice: ");
    int choice = scanner.nextInt();
    scanner.nextLine(); // consume newline

    // Perform sorting based on the selected method
    switch (choice) {
        case 1:
            SortingAlgorithms.quickSort(inventory, 0, inventory.size() - 1, Comparator.comparingDouble(Product::getPrice).reversed());
            System.out.println("Products sorted by Price.");
            break;
        case 2:
        inventory = SortingAlgorithms.mergeSort(inventory, Comparator.comparing(product -> product.getName().toLowerCase()));
        System.out.println("Products sorted by Name (case-insensitive).");
        break;
        case 3:
            SortingAlgorithms.shellSort(inventory, Comparator.comparingInt(Product::getQuantity));
            System.out.println("Products sorted by Quantity.");
            break;
        default:
            System.out.println("Invalid sorting choice.");
            return;
    }

    // Print the sorted list
    System.out.println("\n=== Sorted Product List ===");
    for (Product product : inventory) {
        System.out.println(product);
    }
}

    private void searchProduct(Scanner scanner) {
         System.out.print("Enter Product ID to search: ");
        String productId = scanner.nextLine();

        // Search in memory
        for (Product product : inventory) {
            if (product.getId().equalsIgnoreCase(productId)) {
                System.out.println("Product found in inventory: " + product);
                return;
            }
        }

        // Search in database
        try {
            String query = "SELECT * FROM product WHERE productID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Product found in database:");
                System.out.println("ID: " + rs.getString("productID") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price") + ", Quantity: " + rs.getInt("quantity") + ", Category: " + rs.getString("category"));
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to search product in database: " + e.getMessage());
        }
    }

    private void filterProducts(Scanner scanner) {
        System.out.println("Enter minimum price to filter: ");
        double minPrice = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        System.out.println("Enter maximum price to filter: ");
        double maxPrice = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        
        System.out.println("Enter category to filter: ");
        String category = scanner.nextLine();

        // Fetch and filter products from the database
        try {
            String query = "SELECT * FROM product WHERE price BETWEEN ? AND ? AND LOWER(category) = LOWER(?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);
            pstmt.setString(3, category);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("Filtered Products:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println(
                    "Product{ID='" + rs.getString("productID") +
                    "', Name='" + rs.getString("name") +
                    "', Price=" + rs.getDouble("price") +
                    ", Quantity=" + rs.getInt("quantity") +
                    ", Category='" + rs.getString("category") + "'}"
                );
            }

            if (!found) {
                System.out.println("No products found matching the criteria.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to filter products: " + e.getMessage());
        }
    }


    private void backupAndRestore(Scanner scanner) {
        System.out.println("1. Backup Inventory  2. Restore Inventory");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory_backup.csv"))) {
                    writer.write("ID,Name,Price,Quantity,Category\n");
                    for (Product product : inventory) {
                        writer.write(product.getId() + "," + product.getName() + "," + product.getPrice() + "," + product.getQuantity() + "," + product.getCategory() + "\n");
                    }
                    System.out.println("Backup completed successfully.");
                } catch (IOException e) {
                    System.out.println("Failed to backup inventory: " + e.getMessage());
                }
                break;
            case 2:
                try (BufferedReader reader = new BufferedReader(new FileReader("inventory_backup.csv"))) {
                    String line = reader.readLine(); // Skip header
                    inventory.clear();
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split(",");
                        inventory.add(new Product(fields[0], fields[1], Double.parseDouble(fields[2]), Integer.parseInt(fields[3]), fields[4]));
                    }
                    System.out.println("Restore completed successfully.");
                } catch (IOException e) {
                    System.out.println("Failed to restore inventory: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Failed to close the database connection: " + e.getMessage());
            }
        }
    }
}

