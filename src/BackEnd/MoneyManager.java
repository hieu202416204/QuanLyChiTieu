package BackEnd;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MoneyManager - lớp quản lý Categories, Transactions và đồng bộ trường moneyOfUser trong bảng Users.
 * Thiết kế:
 *  - Toàn bộ thao tác số tiền dùng BigDecimal để giữ chính xác.
 *  - Mọi thay đổi trên Categories/Transactions được thực hiện trong 1 transaction và sau đó
 *    gọi recomputeUserTotal(...) để đảm bảo Users.moneyOfUser luôn bằng tổng balance của các Categories.
 *  - Các PreparedStatement dùng setBigDecimal/getBigDecimal khi phù hợp.
 */
public class MoneyManager {
    private User user;
    private final List<ChucNang> chucNangList = new ArrayList<>();

    public MoneyManager(String idUser) throws SQLException {
        if (idUser == null || idUser.isBlank()) throw new IllegalArgumentException("idUser không hợp lệ");
        loadUser(idUser);
        loadAllData();
    }

    // ========================== LOAD DATA ==========================

    private void loadUser(String idUser) throws SQLException {
        String sql = "SELECT idUser, nameUser, moneyOfUser FROM Users WHERE idUser = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idUser);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal money = rs.getBigDecimal("moneyOfUser");
                    if (money == null) money = BigDecimal.ZERO;
                    user = new User(rs.getString("idUser"), rs.getString("nameUser"), money);
                } else {
                    // Nếu chưa tồn tại, tự tạo
                    BigDecimal initMoney = BigDecimal.ZERO;
                    String insertSql = "INSERT INTO Users(idUser, nameUser, moneyOfUser) VALUES (?, ?, ?)";
                    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                        ins.setString(1, idUser);
                        ins.setString(2, "Người dùng mặc định");
                        ins.setBigDecimal(3, initMoney);
                        ins.executeUpdate();
                    }
                    user = new User(idUser, "Người dùng mặc định", initMoney);
                }
            }
        }
    }

    public void loadAllData() throws SQLException {
        chucNangList.clear();
        String sql = "SELECT idCategory, name, balance, totalSpent FROM Categories WHERE idUser = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getIdUser());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idCategory = rs.getInt("idCategory");
                    String name = rs.getString("name");

                    BigDecimal balance = rs.getBigDecimal("balance");
                    if (balance == null) balance = BigDecimal.ZERO;

                    BigDecimal totalSpent = rs.getBigDecimal("totalSpent");
                    if (totalSpent == null) totalSpent = BigDecimal.ZERO;

                    ChucNang cn = new ChucNang(idCategory, name, balance, totalSpent, user);
                    cn.setHistory(loadHistoryForCategory(idCategory));
                    cn.setCN();
                    chucNangList.add(cn);
                }
            }
        }
    }

    private List<PurposeUseMoney> loadHistoryForCategory(int idCategory) throws SQLException {
        List<PurposeUseMoney> history = new ArrayList<>();
        String sql = "SELECT idTransaction, purposeName, money, localDateTime, isExpense FROM Transactions WHERE idCategory = ? ORDER BY idTransaction";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCategory);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idTransaction = rs.getInt("idTransaction");
                    String purpose = rs.getString("purposeName");

                    BigDecimal money = rs.getBigDecimal("money");
                    if (money == null) money = BigDecimal.ZERO;

                    LocalDateTime ldt = null;
                    try {
                        String ldtStr = rs.getString("localDateTime");
                        if (ldtStr != null) ldt = LocalDateTime.parse(ldtStr);
                    } catch (DateTimeParseException ignored) {
                        // Nếu DB lưu Timestamp, cố gắng đọc về từ ResultSet
                        try {
                            Timestamp ts = rs.getTimestamp("localDateTime");
                            if (ts != null) ldt = ts.toLocalDateTime();
                        } catch (Exception ex) {
                            ldt = null;
                        }
                    }

                    boolean isExpense = rs.getInt("isExpense") == 1;

                    history.add(new PurposeUseMoney(idTransaction, purpose, money, ldt, idCategory, isExpense));
                }
            }
        }
        return history;
    }

    // ========================== CRUD OPERATIONS (transaction-safe) ==========================

    /**
     * Tạo 1 danh mục mới cùng giao dịch nạp tiền ban đầu, sau đó tái tính tổng User từ Categories.
     */
    public void createNewCategory(String name, BigDecimal initialMoney) throws SQLException, Exception {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Tên danh mục không hợp lệ");
        if (initialMoney == null) initialMoney = BigDecimal.ZERO;

        // Kiểm tra tồn tại cục bộ
        if (chucNangList.stream().anyMatch(cn -> cn.getName().equalsIgnoreCase(name))) {
            throw new Exception("Danh mục đã tồn tại.");
        }

        String insertCategorySql = "INSERT INTO Categories (name, balance, totalSpent, idUser) VALUES (?, ?, ?, ?)";
        String insertTransactionSql = "INSERT INTO Transactions (purposeName, money, localDateTime, isExpense, idCategory) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertCategorySql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement transPstmt = conn.prepareStatement(insertTransactionSql)) {

                pstmt.setString(1, name);
                pstmt.setBigDecimal(2, initialMoney);
                pstmt.setBigDecimal(3, BigDecimal.ZERO);
                pstmt.setString(4, user.getIdUser());
                pstmt.executeUpdate();

                int idCategory;
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) idCategory = keys.getInt(1);
                    else throw new SQLException("Không lấy được idCategory sau khi insert");
                }

                // Giao dịch nạp tiền ban đầu
                transPstmt.setString(1, "Nạp tiền ban đầu");
                transPstmt.setBigDecimal(2, initialMoney);
                transPstmt.setString(3, LocalDateTime.now().toString());
                transPstmt.setInt(4, 0);
                transPstmt.setInt(5, idCategory);
                transPstmt.executeUpdate();

                // Tái tính tổng Users.moneyOfUser = SUM(balance)
                recomputeUserTotal(conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        loadAllData();
    }

    /**
     * Nạp thêm tiền vào 1 danh mục.
     */
    public void updateMoney(String categoryName, BigDecimal amount) throws SQLException, Exception {
        if (amount == null) throw new IllegalArgumentException("amount không hợp lệ");

        Optional<ChucNang> optionalCn = chucNangList.stream().filter(cn -> cn.getName().equals(categoryName)).findFirst();
        if (optionalCn.isEmpty()) throw new Exception("Không tìm thấy danh mục.");

        ChucNang cn = optionalCn.get();

        String updateCategorySql = "UPDATE Categories SET balance = balance + ? WHERE idCategory = ?";
        String insertTransactionSql = "INSERT INTO Transactions (purposeName, money, localDateTime, isExpense, idCategory) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateCategorySql);
                 PreparedStatement transPstmt = conn.prepareStatement(insertTransactionSql)) {

                updatePstmt.setBigDecimal(1, amount);
                updatePstmt.setInt(2, cn.getIdCategory());
                updatePstmt.executeUpdate();

                transPstmt.setString(1, "Nạp thêm tiền");
                transPstmt.setBigDecimal(2, amount);
                transPstmt.setString(3, LocalDateTime.now().toString());
                transPstmt.setInt(4, 0);
                transPstmt.setInt(5, cn.getIdCategory());
                transPstmt.executeUpdate();

                recomputeUserTotal(conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        loadAllData();
    }

    /**
     * Chi tiêu từ 1 danh mục.
     * Trả về true nếu chi thành công, false nếu không đủ tiền hoặc lỗi SQL.
     */
    public boolean spendMoney(String categoryName, BigDecimal amount, String purposeName) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return false;

        Optional<ChucNang> optionalCn = chucNangList.stream().filter(cn -> cn.getName().equals(categoryName)).findFirst();
        if (optionalCn.isEmpty()) return false;

        ChucNang cn = optionalCn.get();
        if (cn.getMoneyOfChucNang().compareTo(amount) < 0) return false; // không đủ tiền

        String updateCategorySql = "UPDATE Categories SET balance = balance - ?, totalSpent = totalSpent + ? WHERE idCategory = ?";
        String insertTransactionSql = "INSERT INTO Transactions (purposeName, money, localDateTime, isExpense, idCategory) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateCategorySql);
                 PreparedStatement transPstmt = conn.prepareStatement(insertTransactionSql)) {

                updatePstmt.setBigDecimal(1, amount);
                updatePstmt.setBigDecimal(2, amount);
                updatePstmt.setInt(3, cn.getIdCategory());
                updatePstmt.executeUpdate();

                transPstmt.setString(1, purposeName == null ? "Chi tiêu" : purposeName);
                transPstmt.setBigDecimal(2, amount);
                transPstmt.setString(3, LocalDateTime.now().toString());
                transPstmt.setInt(4, 1);
                transPstmt.setInt(5, cn.getIdCategory());
                transPstmt.executeUpdate();

                recomputeUserTotal(conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        loadAllData();
        return true;
    }

    /**
     * Xóa danh mục: xóa Transactions trước, sau đó xóa Category, cuối cùng tái tính tổng Users.
     */
    public void deleteChucNang(String name) throws SQLException {
        Optional<ChucNang> optionalCn = chucNangList.stream().filter(cn -> cn.getName().equals(name)).findFirst();
        if (optionalCn.isEmpty()) return;

        ChucNang cn = optionalCn.get();

        String deleteTransSql = "DELETE FROM Transactions WHERE idCategory = ?";
        String deleteCatSql = "DELETE FROM Categories WHERE idCategory = ?";

        try (Connection conn = DatabaseManager.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteTransPstmt = conn.prepareStatement(deleteTransSql);
                 PreparedStatement deleteCatPstmt = conn.prepareStatement(deleteCatSql)) {

                deleteTransPstmt.setInt(1, cn.getIdCategory());
                deleteTransPstmt.executeUpdate();

                deleteCatPstmt.setInt(1, cn.getIdCategory());
                deleteCatPstmt.executeUpdate();

                recomputeUserTotal(conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }

        loadAllData();
    }

    // ========================== HELPERS ==========================

    /**
     * Tái tính moneyOfUser = SUM(balance) của tất cả Categories của user
     * Thực hiện trên Connection đang mở (để nằm trong cùng transaction khi cần).
     */
    private void recomputeUserTotal(Connection conn) throws SQLException {
        String sumSql = "SELECT COALESCE(SUM(balance), 0) as total FROM Categories WHERE idUser = ?";
        try (PreparedStatement sumPstmt = conn.prepareStatement(sumSql)) {
            sumPstmt.setString(1, user.getIdUser());
            try (ResultSet rs = sumPstmt.executeQuery()) {
                BigDecimal total = BigDecimal.ZERO;
                if (rs.next()) {
                    total = rs.getBigDecimal("total");
                    if (total == null) total = BigDecimal.ZERO;
                }

                String updateUserSql = "UPDATE Users SET moneyOfUser = ? WHERE idUser = ?";
                try (PreparedStatement upPstmt = conn.prepareStatement(updateUserSql)) {
                    upPstmt.setBigDecimal(1, total);
                    upPstmt.setString(2, user.getIdUser());
                    upPstmt.executeUpdate();

                    // Cập nhật object user ở bộ nhớ để nhất quán
                    user.setMoneyOfUser(total);
                }
            }
        }
    }

    // ========================== API (getters) ==========================

    public void refresh() throws SQLException {
        loadUser(user.getIdUser());
        loadAllData();
    }

    public User getUser() {
        return user;
    }

    public List<ChucNang> getChucNangList() {
        return chucNangList;
    }
}
