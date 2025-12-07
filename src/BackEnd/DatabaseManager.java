package BackEnd;

import java.sql.*;
import java.util.Properties; // Cần cho cấu hình kết nối

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:money_manager.db";

    /**
     * Thiết lập kết nối cơ sở dữ liệu và tạo các bảng nếu chúng chưa tồn tại.
     */
    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                // Tạo bảng Users
                String sqlUser = "CREATE TABLE IF NOT EXISTS Users (\n"
                        + "    idUser TEXT PRIMARY KEY,\n"
                        + "    nameUser TEXT NOT NULL,\n"
                        + "    moneyOfUser REAL NOT NULL\n"
                        + ");";
                conn.createStatement().execute(sqlUser);

                // Tạo bảng Categories
                String sqlCategory = "CREATE TABLE IF NOT EXISTS Categories (\n"
                        + "    idCategory INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "    name TEXT NOT NULL UNIQUE,\n"
                        + "    balance REAL NOT NULL,\n"
                        + "    totalSpent REAL NOT NULL,\n"
                        + "    idUser TEXT NOT NULL,\n"
                        + "    FOREIGN KEY (idUser) REFERENCES Users(idUser)\n"
                        + ");";
                conn.createStatement().execute(sqlCategory);

                // Tạo bảng Transactions
                String sqlTransaction = "CREATE TABLE IF NOT EXISTS Transactions (\n"
                        + "    idTransaction INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + "    purposeName TEXT,\n"
                        + "    money REAL NOT NULL,\n"
                        + "    localDateTime TEXT NOT NULL,\n"
                        + "    isExpense INTEGER NOT NULL, \n"
                        + "    idCategory INTEGER NOT NULL,\n"
                        + "    FOREIGN KEY (idCategory) REFERENCES Categories(idCategory)\n"
                        + ");";
                conn.createStatement().execute(sqlTransaction);

                System.out.println("Cơ sở dữ liệu đã được khởi tạo thành công.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo DB: " + e.getMessage());
        }
    }

    /**
     * Trả về đối tượng Connection để thực hiện các thao tác SQL.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}