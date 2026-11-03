package ShopEase;

import java.util.*;

public class ProductCatalog {

    private ArrayList<Product> products;

    public ProductCatalog() {
        products = new ArrayList<>();
    }

    public void addProduct(Product p) throws DuplicateProductException {
        for (Product prod : products) {
            if (prod.getProductId() == p.getProductId()) {
                throw new DuplicateProductException("Product already exists!");
            }
        }
        products.add(p);
    }

    public void removeProduct(int id) throws ProductNotFoundException {
        Product toRemove = null;

        for (Product p : products) {
            if (p.getProductId() == id) {
                toRemove = p;
                break;
            }
        }

        if (toRemove == null) {
            throw new ProductNotFoundException("Product not found!");
        }

        products.remove(toRemove);
    }

    public Product searchById(int id) throws ProductNotFoundException {
        for (Product p : products) {
            if (p.getProductId() == id) {
                return p;
            }
        }
        throw new ProductNotFoundException("Product not found!");
    }

    public List<Product> searchByName(String keyword) {
        List<Product> result = new ArrayList<>();

        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(p);
            }
        }

        return result;
    }

    public List<Product> browseByCategory(String category) {
        List<Product> result = new ArrayList<>();

        for (Product p : products) {
            if (p.getCategory().equalsIgnoreCase(category)) {
                result.add(p);
            }
        }

        return result;
    }

    public List<Product> getLowStockProducts() {
        List<Product> result = new ArrayList<>();

        for (Product p : products) {
            if (p.getStockQty() < 5) {
                result.add(p);
            }
        }

        return result;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public int getTotalProductCount() {
        return products.size();
    }
}