package BackEnd;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurposeUseMoney {
    private User user;
    private String purposeName;
    private LocalDateTime localDateTime;
    private String chucNangCoDinh;
    private BigDecimal money;

    public PurposeUseMoney() {
    }

    public PurposeUseMoney(User user, String purposeName, BigDecimal bigDecimal) {
        this.user = user;
        this.purposeName = purposeName;
        this.money = bigDecimal;
        this.localDateTime = LocalDateTime.now();
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getMoney() {
        return this.money;
    }

    public void setChucNangCoDinh(String chucNangCoDinh) {
        this.chucNangCoDinh = chucNangCoDinh;
    }

    public String getChucNangCoDinh() {
        return chucNangCoDinh;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

    public String getPurposeName() {
        return purposeName;
    }
}