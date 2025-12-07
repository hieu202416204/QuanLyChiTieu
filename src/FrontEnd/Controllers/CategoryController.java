package FrontEnd.Controllers;

import BackEnd.MoneyManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;

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

        try {
            money = new BigDecimal(initialMoneyField.getText());
            if (money.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền ban đầu không được âm.");
                return;
            }

            // GỌI LOGIC LƯU VÀO DB trong MoneyManager
            moneyManager.createNewCategory(name, money);

            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Danh mục == " + name + " == đã được tạo thành công với " + money + " đ.");
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số tiền không hợp lệ.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi DB", "Lỗi SQL: " + e.getMessage());
        } catch (Exception e) {
            // Lỗi danh mục đã tồn tại
            showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage() + " Vui lòng sử dụng chức năng Nạp Tiền.");
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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
            Image windowIcon = new Image(getClass().getResourceAsStream("/FrontEnd/Image/4.png"));
            if (!windowIcon.isError()) {
                stage.getIcons().add(windowIcon);
            }
        } catch (Exception e) {
            System.err.println("Lỗi tải icon cửa sổ.");
        }

        // 2. TÙY CHỈNH BIỂU TƯỢNG BÊN TRONG
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