package dao;

import java.util.List;

import entity.Sales;

public interface SalesDAO {
	void insertSale(Sales sales);
    List<Sales> getSalesByDate(String date);
}
