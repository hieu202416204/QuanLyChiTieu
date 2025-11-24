package FrontEnd.Controllers;

import BackEnd.MoneyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Danh mục == " + name + " == đã tồn tại. Vui lòng sử dụng chức năng Nạp Tiền.");
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
        showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Danh mục == " + name + " == đã được tạo thành công với " + money + " đ.");

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

            // Đặt kích thước cho sticker để nó không quá lớn (rất quan trọng)
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