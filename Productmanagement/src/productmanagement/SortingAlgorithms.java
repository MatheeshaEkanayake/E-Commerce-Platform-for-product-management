/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package productmanagement;

/**
 *
 * @author User
 */
import java.util.*;

public class SortingAlgorithms {

    public static void quickSort(List<Product> list, int low, int high, Comparator<Product> comparator) {
        if (low < high) {
            int pi = partition(list, low, high, comparator);
            quickSort(list, low, pi - 1, comparator);
            quickSort(list, pi + 1, high, comparator);
        }
    }

    private static int partition(List<Product> list, int low, int high, Comparator<Product> comparator) {
        Product pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    public static List<Product> mergeSort(List<Product> list, Comparator<Product> comparator) {
        if (list.size() <= 1) {
            return list;
        }

        int mid = list.size() / 2;
        List<Product> left = mergeSort(new ArrayList<>(list.subList(0, mid)), comparator);
        List<Product> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())), comparator);

        return merge(left, right, comparator);
    }

    private static List<Product> merge(List<Product> left, List<Product> right, Comparator<Product> comparator) {
        List<Product> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        while (i < left.size()) {
            merged.add(left.get(i++));
        }

        while (j < right.size()) {
            merged.add(right.get(j++));
        }

        return merged;
    }

    public static void shellSort(List<Product> list, Comparator<Product> comparator) {
        int n = list.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Product temp = list.get(i);
                int j;
                for (j = i; j >= gap && comparator.compare(list.get(j - gap), temp) > 0; j -= gap) {
                    list.set(j, list.get(j - gap));
                }
                list.set(j, temp);
            }
        }
    }
}

// Sample Product class for testing purposes
class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String category;

    public Product(String id, String name, double price, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID='" + id + '\'' +
                ", Name='" + name + '\'' +
                ", Price=" + price +
                ", Quantity=" + quantity +
                ", Category='" + category + '\'' +
                '}';
    }
}

