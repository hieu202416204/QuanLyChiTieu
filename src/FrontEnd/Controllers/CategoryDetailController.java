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
import java.sql.SQLException;

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

    public void setChucNang(ChucNang cn) {
        this.currentChucNang = cn;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentChucNang != null) {
            // Tải lại ChucNang mới nhất từ MoneyManager (sau khi DB đã update)
            ChucNang updatedCn = moneyManager.getChucNangList().stream()
                    .filter(cn -> cn.getName().equals(currentChucNang.getName()))
                    .findFirst().orElse(currentChucNang);

            categoryNameLabel.setText("Chi Tiết: " + updatedCn.getName());
            currentBalanceLabel.setText(updatedCn.getMoneyOfChucNang()
                    .setScale(2, RoundingMode.HALF_UP).toPlainString() + " đ");
            totalSpentLabel.setText(updatedCn.getTongSoTienDaTieu()
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

            // Gọi hàm cập nhật tiền và lưu vào DB
            moneyManager.updateMoney(currentChucNang.getName(), amount);

            // Cập nhật giao diện chi tiết ngay lập tức (sau khi MoneyManager đã loadAllData())
            updateDisplay();
            topUpAmountField.clear();

            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã nạp thêm " + amount.toPlainString() + " đ vào danh mục.");

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi DB", "Lỗi SQL: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) categoryNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        // ... (Giữ nguyên logic showAlert)
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
// 1. Tùy chỉnh Icon Cửa Sổ
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            Image windowIcon = new Image(getClass().getResourceAsStream("/FrontEnd/Image/4.png"));
            if (!windowIcon.isError()) {
                stage.getIcons().add(windowIcon);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải icon cửa sổ.");
        }

        // 2. TÙY CHỈNH BIỂU TƯỢNG BÊN TRONG (Thay thế dấu X/chấm than)
        try {
            Image customSticker = new Image(getClass().getResourceAsStream("/FrontEnd/Image/2.png"));

            ImageView customImageView = new ImageView(customSticker);

            customImageView.setFitWidth(48);
            customImageView.setFitHeight(48);

            alert.setGraphic(customImageView);

        } catch (Exception e) {
            System.err.println("Lỗi tải sticker tùy chỉnh.");
        }
        alert.showAndWait();
    }
}