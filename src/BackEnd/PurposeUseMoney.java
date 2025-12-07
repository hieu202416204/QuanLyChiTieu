package BackEnd;

import BackEnd.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurposeUseMoney {
    private int idTransaction;
    private String purposeName;
    private BigDecimal money;
    private LocalDateTime localDateTime;
    private String chucNangCoDinh; // Tên danh mục (để hiển thị)
    private int idCategory;
    private boolean isExpense; // true: chi tiêu, false: nạp tiền

    // Constructor khi đọc từ DB
    public PurposeUseMoney(int idTransaction, String purposeName, BigDecimal money, LocalDateTime localDateTime, int idCategory, boolean isExpense) {
        this.idTransaction = idTransaction;
        this.purposeName = purposeName;
        this.money = money;
        this.localDateTime = localDateTime;
        this.idCategory = idCategory;
        this.isExpense = isExpense;
    }

    // Constructor khi tạo giao dịch mới
    public PurposeUseMoney(String purposeName, BigDecimal money, int idCategory, boolean isExpense) {
        this.purposeName = purposeName;
        this.money = money;
        this.localDateTime = LocalDateTime.now();
        this.idCategory = idCategory;
        this.isExpense = isExpense;
    }

    // Getters (dùng cho TableView trong HistoryController)
    public int getIdTransaction() { return idTransaction; }
    public String getPurposeName() { return purposeName; }
    public BigDecimal getMoney() { return money; }
    public LocalDateTime getLocalDateTime() { return localDateTime; }
    public String getChucNangCoDinh() { return chucNangCoDinh; }
    public int getIdCategory() { return idCategory; }
    public boolean isExpense() { return isExpense; }

    // Setters
    public void setChucNangCoDinh(String chucNangCoDinh) { this.chucNangCoDinh = chucNangCoDinh; }
    public void setMoney(BigDecimal money) {
        this.money = money;
    }
    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

}