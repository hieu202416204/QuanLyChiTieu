package FrontEnd.Controllers;

import BackEnd.MoneyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class SpendController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField moneyField;
    @FXML private TextField purposeField;

    private MoneyManager moneyManager;

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
        // Khởi tạo ComboBox với danh sách tên các ChucNang
        if (moneyManager.getChucNangList() != null) {
            categoryComboBox.setItems(moneyManager.getChucNangList().stream()
                    .map(cn -> cn.getName())
                    .collect(Collectors.toCollection(javafx.collections.FXCollections::observableArrayList)));
        }
    }

    // Phương thức này hiện không cần thiết vì việc refresh đã được MainApp xử lý sau khi cửa sổ này đóng.
    // public void setMainController(MainController mainController) { ... }

    @FXML
    public void handleSaveSpend() {
        String categoryName = categoryComboBox.getValue();
        String purposeName = purposeField.getText();
        BigDecimal money;

        if (categoryName == null || purposeName == null || purposeName.trim().isEmpty() || moneyField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng chọn danh mục và điền đầy đủ thông tin.");
            return;
        }

        try {
            money = new BigDecimal(moneyField.getText());
            if (money.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
            return;
        }

        boolean success = moneyManager.spendMoney(categoryName, money, purposeName);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Chi tiêu đã được ghi nhận thành công!");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi Chi Tiêu", "Số dư trong danh mục **" + categoryName + "** không đủ (" + moneyManager.getChucNangList().stream().filter(cn -> cn.getName().equals(categoryName)).findFirst().get().getMoneyOfChucNang() + " đ còn lại).");
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) moneyField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // 1. Tùy chỉnh Icon Cửa Sổ
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            Image windowIcon = new Image(getClass().getResourceAsStream("/FrontEnd/Image/4.jpg"));
            if (!windowIcon.isError()) {
                stage.getIcons().add(windowIcon);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải icon cửa sổ.");
        }

        // 2. TÙY CHỈNH BIỂU TƯỢNG BÊN TRONG (Thay thế dấu X/chấm than)
        try {
            // Tải ảnh sticker/icon tùy chỉnh
            Image customSticker = new Image(getClass().getResourceAsStream("/FrontEnd/Image/2.jpg"));

            ImageView customImageView = new ImageView(customSticker);

            // Đặt kích thước cho sticker để nó không quá lớn
            customImageView.setFitWidth(48);
            customImageView.setFitHeight(48);

            // Đặt ImageView tùy chỉnh làm graphic của Alert
            alert.setGraphic(customImageView);

        } catch (Exception e) {
            System.err.println("Lỗi tải sticker tùy chỉnh.");
        }
        alert.showAndWait();
    }
}