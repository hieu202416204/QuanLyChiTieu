package BackEnd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChucNang {
    private int idCategory;
    private String name;
    private BigDecimal moneyOfChucNang; // balance
    private BigDecimal tongSoTienDaTieu;
    private List<PurposeUseMoney> history = new ArrayList<>();
    private User user; // Dù không dùng trực tiếp, giữ lại để tương thích

    // Constructor khi đọc từ DB
    public ChucNang(int idCategory, String name, BigDecimal moneyOfChucNang, BigDecimal tongSoTienDaTieu, User user) {
        this.idCategory = idCategory;
        this.name = name;
        this.moneyOfChucNang = moneyOfChucNang;
        this.tongSoTienDaTieu = tongSoTienDaTieu;
        this.user = user;
    }

    // Constructor khi tạo mới
    public ChucNang(String name, BigDecimal moneyOfChucNang, User user) {
        this.name = name;
        this.moneyOfChucNang = moneyOfChucNang;
        this.tongSoTienDaTieu = BigDecimal.ZERO;
        this.user = user;
    }

    // Getters (cần thiết cho MainController & CategoryDetailController)
    public int getIdCategory() { return idCategory; }
    public String getName() { return name; }
    public BigDecimal getMoneyOfChucNang() { return moneyOfChucNang; }
    public BigDecimal getTongSoTienDaTieu() { return tongSoTienDaTieu; }
    public List<PurposeUseMoney> getHistory() { return history; }
    public User getUser() { return user; }

    // Setters (để cập nhật dữ liệu từ DB)
    public void setHistory(List<PurposeUseMoney> history) { this.history = history; }
    public void setMoneyOfChucNang(BigDecimal moneyOfChucNang) { this.moneyOfChucNang = moneyOfChucNang; }
    public void setTongSoTienDaTieu(BigDecimal tongSoTienDaTieu) { this.tongSoTienDaTieu = tongSoTienDaTieu; }

    // Phương thức giả lập từ file .class, có thể bị loại bỏ khi dùng DB
    public void setCN() {
        // Trong DB, việc này được thay thế bằng JOIN khi tải lịch sử
        history.forEach(p -> p.setChucNangCoDinh(this.name));
    }
}