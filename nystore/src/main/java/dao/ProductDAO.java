package dao;

import java.util.List;

import entity.Product;

public interface ProductDAO {
    void insertProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(int prdId);
    void updateProduct(int prdId, int newStock);
    void deleteProduct(int prdId);

    List<Product> searchProductsByName(String keyword);
}
