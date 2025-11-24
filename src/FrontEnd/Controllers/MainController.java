package FrontEnd.Controllers;

import BackEnd.ChucNang;
import BackEnd.MoneyManager;
import FrontEnd.MainApp;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;       // Cần import cho Image
import javafx.scene.image.ImageView;    // Cần import cho ImageView
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Objects;              // Cần import cho getResource

public class MainController {

    @FXML private Label totalMoneyLabel;
    @FXML private ListView<ChucNang> categoryListView;
    private MoneyManager moneyManager;
    private ObservableList<ChucNang> chucNangList = FXCollections.observableArrayList();

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
        initializeData();
    }

    @FXML
    public void handleRefresh() {
        initializeData();
    }

    // ----------------------------------------------------------------------
    // PHƯƠNG THỨC LOGIC & CẬP NHẬT GIAO DIỆN
    // ----------------------------------------------------------------------

    private void initializeData() {
        if (moneyManager != null) {

            // 1. Cập nhật Tổng Tiền User
            moneyManager.totalMoney();

            // 2. Cập nhật Label Tổng Số Dư
            totalMoneyLabel.setText(
                    moneyManager.getUser().getMoneyOfUser()
                            .setScale(2, RoundingMode.HALF_UP)
                            .toPlainString() + " đ"
            );

            // 3. Cập nhật danh sách các Danh Mục Chi Tiêu (ChucNang)
            chucNangList.clear();
            chucNangList.addAll(moneyManager.getChucNangList());

            // 4. Áp dụng Custom Cell Factory và dữ liệu
            categoryListView.setItems(chucNangList);
            categoryListView.setCellFactory(lv -> new CategoryCell());

            // 5. Thiết lập Listener cho sự kiện nhấn đúp chuột (mở Chi Tiết Danh Mục)
            categoryListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && categoryListView.getSelectionModel().getSelectedItem() != null) {
                    handleCategoryDoubleClick(categoryListView.getSelectionModel().getSelectedItem());
                }
            });
        }
    }

    // Xử lý sự kiện nhấn đúp chuột vào một danh mục
    private void handleCategoryDoubleClick(ChucNang selectedChucNang) {
        if (selectedChucNang != null) {
            openCategoryDetailWindow(selectedChucNang);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy chi tiết danh mục.");
        }
    }

    // ***************************************************************
    // LỚP NỘI BỘ: CUSTOM CELL FACTORY ĐỂ HIỂN THỊ CARD (ĐÃ TÍCH HỢP STICKER)
    // ***************************************************************
    public static class CategoryCell extends ListCell<ChucNang> {
        private final HBox cardLayout;
        private final ImageView categoryIcon; // THÊM ImageView
        private final Label nameLabel;
        private final Label balanceLabel;
        private final Label spentLabel;
        private final VBox textContainer;

        public CategoryCell() {
            // Khởi tạo các thành phần giao diện
            categoryIcon = new ImageView();
            categoryIcon.setFitWidth(40); // Kích thước icon
            categoryIcon.setFitHeight(40);

            nameLabel = new Label();
            balanceLabel = new Label();
            spentLabel = new Label();

            // Áp dụng CSS classes và style cơ bản
            nameLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
            balanceLabel.getStyleClass().add("label-balance");
            spentLabel.getStyleClass().add("label-spent");

            // Container cho tên và số tiền đã tiêu
            textContainer = new VBox(5, nameLabel, spentLabel);
            textContainer.setPrefWidth(250);
            HBox.setHgrow(textContainer, Priority.ALWAYS); // Cho phép mở rộng

            // Thiết lập layout Card: THÊM categoryIcon vào HBox
            cardLayout = new HBox(15, categoryIcon, textContainer, balanceLabel);
            cardLayout.getStyleClass().add("category-card");
            cardLayout.setPadding(new javafx.geometry.Insets(10));

            // Đảm bảo Cell luôn có khoảng cách dọc
            setPadding(new javafx.geometry.Insets(5, 0, 5, 0));
        }

        // Phương thức TẢI VÀ GÁN ICON dựa trên tên danh mục
        private Image getIconForCategory(String name, int index) {
            // Đảm bảo đường dẫn này khớp với vị trí ảnh của bạn
            String imagePath = "FrontEnd/Image/";
            String fileName = "";

            // Logic gán ảnh dựa trên Tên Danh Mục
            if (name == null) {
                fileName = "default.jpg"; // Dùng ảnh mặc định
            } else if (name.toLowerCase().contains("ăn uống")) {
                fileName = "1.jpg";
            } else if (name.toLowerCase().contains("tiết kiệm")) {
                fileName = "2.jpg";
            } else if (name.toLowerCase().contains("giải trí")) {
                fileName = "5.jpg";
            } else if (name.toLowerCase().contains("lương")) {
                fileName = "4.jpg";
            } else {
                // Xoay vòng các ảnh còn lại nếu không khớp tên (Dùng 8 ảnh)
                int imageNum = (index % 8) + 1;
                fileName = imageNum + ".jpg";
            }

            try {
                return new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(imagePath + fileName)).toExternalForm());
            } catch (NullPointerException e) {
                // Fallback nếu không tìm thấy ảnh
                System.err.println("Không tìm thấy sticker: " + imagePath + fileName);
                return null;
            }
        }

        @Override
        protected void updateItem(ChucNang cn, boolean empty) {
            super.updateItem(cn, empty);

            if (empty || cn == null) {
                setGraphic(null);
                setText(null);
            } else {
                // Gán dữ liệu vào các Label
                nameLabel.setText(cn.getName());

                balanceLabel.setText(cn.getMoneyOfChucNang()
                        .setScale(2, RoundingMode.HALF_UP).toPlainString() + " đ");

                spentLabel.setText("Đã tiêu: " + cn.getTongSoTienDaTieu()
                        .setScale(2, RoundingMode.HALF_UP).toPlainString() + " đ");

                // GÁN STICKER
                int index = getListView().getItems().indexOf(cn);
                categoryIcon.setImage(getIconForCategory(cn.getName(), index));

                setGraphic(cardLayout);

                // HIỆU ỨNG: FADE IN khi Cell được tạo
                if (!this.getStyleClass().contains("faded-in")) {

                    cardLayout.setOpacity(0);

                    FadeTransition fade = new FadeTransition(Duration.millis(500), cardLayout);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();

                    this.getStyleClass().add("faded-in");
                } else {
                    cardLayout.setOpacity(1);
                }
            }
        }
    }

    @FXML
    public void handleSpendMoney() {
        openNewWindow("views/SpendView.fxml", "Thêm Giao Dịch Chi Tiêu", 500, 400);
    }

    @FXML
    public void handleAddCategory() {
        openNewWindow("views/CategoryView.fxml", "Thêm Danh Mục Mới", 400, 300);
    }

    @FXML
    public void handleShowHistory() {
        openNewWindow("views/HistoryView.fxml", "Lịch Sử Giao Dịch", 800, 600);
    }

    private void openCategoryDetailWindow(ChucNang cn) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("views/CategoryDetailView.fxml"));
            Parent root = loader.load();

            CategoryDetailController controller = loader.getController();
            controller.setMoneyManager(moneyManager);
            controller.setChucNang(cn);

            Stage newStage = new Stage();
            newStage.setTitle("Chi Tiết Danh Mục - " + cn.getName());
            newStage.setScene(new Scene(root, 550, 450));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();

            handleRefresh();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Tải Giao Diện", "Không thể tải CategoryDetailView.fxml");
        }
    }

    private void openNewWindow(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof SpendController) {
                ((SpendController) controller).setMoneyManager(moneyManager);
            } else if (controller instanceof CategoryController) {
                ((CategoryController) controller).setMoneyManager(moneyManager);
            } else if (controller instanceof HistoryController) {
                ((HistoryController) controller).setMoneyManager(moneyManager);
            }

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(new Scene(root, width, height));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();

            handleRefresh();
        }catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Ứng Dụng", "Không tìm thấy file FXML hoặc lỗi tải tài nguyên: " + fxmlPath);
        }
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
    }
}