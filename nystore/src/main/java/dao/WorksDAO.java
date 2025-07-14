package dao;

public interface WorksDAO {
	void insertLoginRecord(String empId);
    void updateLogoutRecord(String empId);
}
