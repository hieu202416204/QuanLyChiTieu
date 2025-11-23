package FrontEnd.Controllers;

import BackEnd.MoneyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class CategoryController {
    @FXML private TextField nameField;
    @FXML private TextField initialMoneyField;
    private MoneyManager moneyManager;

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
    }

    @FXML
    public void handleSaveCategory() {
        String name = nameField.getText().trim();
        BigDecimal money;

        if (name.isEmpty() || initialMoneyField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        // 1. KIỂM TRA: DANH MỤC ĐÃ TỒN TẠI CHƯA?
        boolean categoryExists = moneyManager.getChucNangList().stream()
                .anyMatch(cn -> cn.getName().equalsIgnoreCase(name));

        if (categoryExists) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Danh mục **" + name + "** đã tồn tại. Vui lòng sử dụng chức năng Nạp Tiền.");
            return; // Dừng lại nếu tên đã tồn tại
        }

        try {
            money = new BigDecimal(initialMoneyField.getText());
            if (money.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền ban đầu không được âm.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
            return;
        }

        // 2. TẠO DANH MỤC MỚI
        moneyManager.updateChucNangList(name, moneyManager.getUser(), money);
        showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Danh mục **" + name + "** đã được tạo thành công với " + money + " đ.");

        closeWindow();
    }
    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
        // Phương thức openNewWindow trong MainController sẽ tự động gọi handleRefresh() sau khi cửa sổ này đóng.
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}