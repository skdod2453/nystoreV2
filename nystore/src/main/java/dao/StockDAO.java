package dao;

import java.util.List;

import entity.Stock;

public interface StockDAO {
    void insertStock(Stock stock);
    List<Stock> getAllStockHistory();
}
