package FrontEnd.Controllers;

import BackEnd.ChucNang;
import BackEnd.MoneyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CategoryDetailController {

    @FXML private Label categoryNameLabel;
    @FXML private Label currentBalanceLabel;
    @FXML private Label totalSpentLabel;
    @FXML private TextField topUpAmountField;

    private MoneyManager moneyManager;
    private ChucNang currentChucNang;

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
    }

    // Phương thức này được gọi từ MainController để truyền danh mục đang được chọn
    public void setChucNang(ChucNang cn) {
        this.currentChucNang = cn;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentChucNang != null) {
            categoryNameLabel.setText("Chi Tiết: " + currentChucNang.getName());
            currentBalanceLabel.setText(currentChucNang.getMoneyOfChucNang()
                    .setScale(2, RoundingMode.HALF_UP).toPlainString() + " đ");
            totalSpentLabel.setText(currentChucNang.getTongSoTienDaTieu()
                    .setScale(2, RoundingMode.HALF_UP).toPlainString() + " đ");
        }
    }

    @FXML
    public void handleTopUp() {
        String amountText = topUpAmountField.getText();
        if (amountText == null || amountText.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập số tiền muốn nạp.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền nạp phải lớn hơn 0.");
                return;
            }

            // Gọi hàm cập nhật tiền trong MoneyManager
            moneyManager.updateMoney(currentChucNang.getName(), amount);

            // Cập nhật giao diện chi tiết ngay lập tức
            updateDisplay();
            topUpAmountField.clear();

            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã nạp thêm " + amount.toPlainString() + " đ vào danh mục.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) categoryNameLabel.getScene().getWindow();
        stage.close();
        // MainController sẽ tự động refresh sau khi cửa sổ này đóng
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
// 1. Tùy chỉnh Icon Cửa Sổ (giữ nguyên code của bạn)
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
            // Tải ảnh sticker/icon tùy chỉnh của bạn
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