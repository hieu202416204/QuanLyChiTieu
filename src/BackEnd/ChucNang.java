package BackEnd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChucNang {
    private String name;
    private User user;
    private BigDecimal moneyOfChucNang = BigDecimal.ZERO;
    private BigDecimal tongSoTienDaTieu = BigDecimal.ZERO;
    private List<PurposeUseMoney> history = new ArrayList<>();

    public ChucNang(){}
    public ChucNang(String name, User user, BigDecimal money){
        this.name = name;
        this.user = user;
        this.moneyOfChucNang = money;
    }

    public void setCN(){
        for(PurposeUseMoney p : this.history){
            p.setChucNangCoDinh(this.name);
        }
    }
    public void updateHitory(User user, String purposeName, BigDecimal bigDecimal) {
        this.history.add(new PurposeUseMoney(user, purposeName, bigDecimal));
    }

    public void updateTongSoTienDaTieu(BigDecimal money) {
        this.tongSoTienDaTieu = this.tongSoTienDaTieu.add(money);
    }

    public BigDecimal getTongSoTienDaTieu() {
        return this.tongSoTienDaTieu;
    }

    public List<PurposeUseMoney> getHistory() {
        return this.history;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setMoneyOfChucNang(BigDecimal moneyOfChucNang) {
        this.moneyOfChucNang = moneyOfChucNang;
    }

    public BigDecimal getMoneyOfChucNang() {
        return this.moneyOfChucNang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public void updateMoney(BigDecimal money){
        this.moneyOfChucNang = this.moneyOfChucNang.add(money);
    }
    public boolean spendMoney(String purposeName, BigDecimal money){
        if(this.getMoneyOfChucNang().compareTo(money)<0){
            return false;
        }
        this.moneyOfChucNang = this.moneyOfChucNang.subtract(money);
        this.updateTongSoTienDaTieu(money);
        this.updateHitory(this.user, purposeName, money);
        return true;
    }
}
