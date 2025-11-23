package BackEnd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MoneyManager {
    private User user;
    private List<ChucNang> chucNangList = new ArrayList<>();
    public MoneyManager(){
    }
    public MoneyManager(User user, List<ChucNang> chucNangList){
        this.user = user;
        this.chucNangList = chucNangList;
    }
    public void totalMoney(){
        BigDecimal bigDecimal = BigDecimal.ZERO;
        for(ChucNang cn: this.chucNangList){
            bigDecimal = bigDecimal.add(cn.getMoneyOfChucNang());
        }
        this.user.setMoneyOfUser(bigDecimal);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void updateChucNangList(String name, User user, BigDecimal bigDecimal) {
        this.chucNangList.add(new ChucNang(name, user, bigDecimal));
    }

    public List<ChucNang> getChucNangList() {
        return this.chucNangList;
    }

    public void updateMoney(String nameOfChucNang, BigDecimal money){
        for(ChucNang cn : this.chucNangList){
            if(cn.getName().equals(nameOfChucNang)){
                cn.updateMoney(money);
            }
        }
        this.totalMoney();
    }
    public boolean spendMoney(String nameOfChucNang, BigDecimal money, String purposeName){
        for(ChucNang cn : this.chucNangList){
            if(cn.getName().equals(nameOfChucNang)){
                this.totalMoney();
                return cn.spendMoney(purposeName, money);
            }
        }
        return false;
    }
}
