package BackEnd;

import java.math.BigDecimal;

public class User {
    private String nameUser;
    private String idUser;
    private BigDecimal moneyOfUser = BigDecimal.ZERO;

    public User() {
    }

    public User(String nameUser, String idUser, BigDecimal moneyOfUser) {
        this.nameUser = nameUser;
        this.idUser = idUser;
        this.moneyOfUser = moneyOfUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setMoneyOfUser(BigDecimal moneyOfUser) {
        this.moneyOfUser = moneyOfUser;
    }

    public String getNameUser() {
        return this.nameUser;
    }

    public BigDecimal getMoneyOfUser() {
        return this.moneyOfUser;
    }

    public String getIdUser() {
        return this.idUser;
    }
    public void inTTin(){
        System.out.println(this.nameUser);
        System.out.println(this.idUser);
        System.out.println(this.moneyOfUser);
    }
}
