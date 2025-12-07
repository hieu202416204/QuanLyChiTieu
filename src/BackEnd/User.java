package BackEnd;

import java.math.BigDecimal;

public class User {
    private String idUser;
    private String nameUser;
    private BigDecimal moneyOfUser;

    public User(String idUser, String nameUser, BigDecimal moneyOfUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.moneyOfUser = moneyOfUser;
    }

    // Constructor đơn giản cho MainController
    public User() {
        this("default_user", "Người Dùng Mặc Định", BigDecimal.ZERO);
    }

    // Getters
    public String getIdUser() { return idUser; }
    public String getNameUser() { return nameUser; }
    public BigDecimal getMoneyOfUser() { return moneyOfUser; }

    // Setters
    public void setMoneyOfUser(BigDecimal moneyOfUser) { this.moneyOfUser = moneyOfUser; }
    public void setNameUser(String nameUser) { this.nameUser = nameUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }
}