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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.math.RoundingMode;
import java.util.Objects;

public class MainController {

    @FXML private Label totalMoneyLabel;
    @FXML private ListView<ChucNang> categoryListView;

    private MoneyManager moneyManager;
    private final ObservableList<ChucNang> chucNangList = FXCollections.observableArrayList();

    // ============================================================
    // SETUP & REFRESH
    // ============================================================

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
        handleRefresh();
    }

    @FXML
    public void handleRefresh() {
        if (moneyManager == null) return;

        try {
            moneyManager.refresh(); // Reload user và danh sách từ DB
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể làm mới dữ liệu từ DB.");
            e.printStackTrace();
        }

        // Cập nhật tổng tiền
        totalMoneyLabel.setText(
                moneyManager.getUser().getMoneyOfUser()
                        .setScale(2, RoundingMode.HALF_UP) + " đ"
        );

        // Cập nhật danh sách danh mục
        chucNangList.setAll(moneyManager.getChucNangList());
        categoryListView.setItems(chucNangList);
        categoryListView.setCellFactory(list -> new CategoryCell(this));

        // Nhấn đúp mở chi tiết danh mục
        categoryListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ChucNang cn = categoryListView.getSelectionModel().getSelectedItem();
                if (cn != null) openCategoryDetailWindow(cn);
            }
        });
    }

    // ============================================================
    // XÓA DANH MỤC
    // ============================================================

    public void deleteChucNangInList(String name) {
        try {
            moneyManager.deleteChucNang(name);
            handleRefresh();
            showAlert(Alert.AlertType.INFORMATION, "Thành Công", "Đã xóa danh mục: " + name);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa danh mục: " + name);
            e.printStackTrace();
        }
    }

    // ============================================================
    // CUSTOM CELL (Category Card)
    // ============================================================

    public static class CategoryCell extends ListCell<ChucNang> {

        private final HBox cardLayout;
        private final ImageView categoryIcon = new ImageView();
        private final Label nameLabel = new Label();
        private final Label balanceLabel = new Label();
        private final Label spentLabel = new Label();
        private final Button deleteButton = new Button("🗑️");

        private final MainController controller;

        public CategoryCell(MainController mainController) {
            this.controller = mainController;

            categoryIcon.setFitWidth(40);
            categoryIcon.setFitHeight(40);

            nameLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");
            balanceLabel.getStyleClass().add("label-balance");
            spentLabel.getStyleClass().add("label-spent");

            VBox textBox = new VBox(5, nameLabel, spentLabel);
            HBox.setHgrow(textBox, Priority.ALWAYS);

            deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 30px;");

            cardLayout = new HBox(15, categoryIcon, textBox, balanceLabel, deleteButton);
            cardLayout.getStyleClass().add("category-card");
            cardLayout.setPadding(new javafx.geometry.Insets(10));
        }

        /** Load icon theo tên danh mục */
        private Image loadIcon(String name, int index) {
            String base = "FrontEnd/Image/";
            String file;

            if (name == null) {
                file = "default.png";
            } else if (name.toLowerCase().contains("ăn uống")) {
                file = "1.png";
            } else if (name.toLowerCase().contains("tiết kiệm")) {
                file = "2.png";
            } else if (name.toLowerCase().contains("giải trí")) {
                file = "5.png";
            } else if (name.toLowerCase().contains("lương")) {
                file = "4.png";
            } else {
                file = ((index % 8) + 1) + ".png";
            }

            try {
                return new Image(Objects.requireNonNull(getClass().getClassLoader()
                        .getResource(base + file)).toExternalForm());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void updateItem(ChucNang cn, boolean empty) {
            super.updateItem(cn, empty);

            if (empty || cn == null) {
                setGraphic(null);
                return;
            }

            nameLabel.setText(cn.getName());
            balanceLabel.setText(
                    cn.getMoneyOfChucNang().setScale(2, RoundingMode.HALF_UP) + " đ"
            );
            spentLabel.setText(
                    "Đã tiêu: " + cn.getTongSoTienDaTieu().setScale(2, RoundingMode.HALF_UP) + " đ"
            );

            int index = getIndex();
            categoryIcon.setImage(loadIcon(cn.getName(), index));

            // Fade-in chỉ chạy 1 lần
            if (!cardLayout.getProperties().containsKey("animated")) {
                cardLayout.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(250), cardLayout);
                ft.setDelay(Duration.millis(index * 35));
                ft.setToValue(1);
                ft.play();
                cardLayout.getProperties().put("animated", true);
            }

            deleteButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Bạn muốn xóa '" + cn.getName() + "'?\nKhông thể hoàn tác.");
                if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    controller.deleteChucNangInList(cn.getName());
                }
            });

            setGraphic(cardLayout);
        }
    }

    // ============================================================
    // MỞ CỬA SỔ KHÁC
    // ============================================================

    @FXML
    public void handleSpendMoney() {
        openWindow("views/SpendView.fxml", "Thêm Giao Dịch Chi Tiêu", 500, 400);
    }

    @FXML
    public void handleAddCategory() {
        openWindow("views/CategoryView.fxml", "Thêm Danh Mục Mới", 400, 300);
    }

    @FXML
    public void handleShowHistory() {
        openWindow("views/HistoryView.fxml", "Lịch Sử Giao Dịch", 400, 600);
    }

    private void openCategoryDetailWindow(ChucNang cn) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("views/CategoryDetailView.fxml"));
            Parent root = loader.load();

            CategoryDetailController c = loader.getController();
            c.setMoneyManager(moneyManager);
            c.setChucNang(cn);

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Danh Mục - " + cn.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            fadeIn(root);
            stage.showAndWait();

            handleRefresh();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở cửa sổ chi tiết.");
        }
    }

    private void openWindow(String path, String title, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(path));
            Parent root = loader.load();

            Object c = loader.getController();
            if (c instanceof MoneyManagerSetter mm) mm.setMoneyManager(moneyManager);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, w, h));
            stage.initModality(Modality.APPLICATION_MODAL);

            fadeIn(root);
            stage.showAndWait();

            handleRefresh();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở cửa sổ: " + path);
        }
    }

    private void fadeIn(Parent root) {
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250), root);
        ft.setToValue(1);
        ft.play();
    }

    // ============================================================
    // ALERT
    // ============================================================

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }

    // Giúp openWindow gọi giống nhau cho mọi controller
    public interface MoneyManagerSetter {
        void setMoneyManager(MoneyManager m);
    }
}
