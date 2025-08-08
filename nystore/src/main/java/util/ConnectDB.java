package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    public static Connection getConnection() {
        Connection connection = null;

        // 직접 DB 정보 입력
        String url = "jdbc:oracle:thin:@10.10.108.158:1521:XE"; // 여러분의 DB 주소
        String user = "c##nystore"; // 사용자 계정
        String password = "nystore"; // 비밀번호

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ DB 연결 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC 드라이버 누락");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ DB 접속 실패: " + e.getMessage());
            e.printStackTrace();
        }

        if (connection == null) {
            System.out.println("❌ DB 연결이 실패했습니다.");
        }

        
        return connection;
    }
}
