package FrontEnd;

import BackEnd.MoneyManager;
import BackEnd.User;
import FrontEnd.Controllers.MainController; // Import MainController
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip; // Cần import cho Tooltip fix
import javafx.stage.Stage;
import javafx.util.Duration; // Cần import cho Tooltip fix

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainApp extends Application {

    private MoneyManager manager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Khởi tạo dữ liệu mẫu
        User defaultUser = new User("Quản lý Chi tiêu", "USER001", BigDecimal.ZERO);
        manager = new MoneyManager(defaultUser, new ArrayList<>());

        // Thêm các danh mục mẫu
        manager.updateChucNangList("Ăn Uống", defaultUser, new BigDecimal("0"));
        manager.updateChucNangList("Tiết Kiệm", defaultUser, new BigDecimal("0"));
        manager.updateChucNangList("Giải Trí", defaultUser, new BigDecimal("0"));

        // Cập nhật tổng tiền lần đầu
        manager.totalMoney();

        // 2. Tải FXML (Sử dụng MainApp.class để tìm FXML chính xác)
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("views/MainView.fxml"));
        Parent root = loader.load();

        // 3. Truyền đối tượng manager vào MainController
        MainController mainController = loader.getController();
        mainController.setMoneyManager(manager);

        // 4. Thiết lập Stage và hiển thị
        primaryStage.setTitle("💰 Ứng Dụng Quản Lý Chi Tiêu");
        primaryStage.setScene(new Scene(root, 900, 650));
        primaryStage.show();
    }

    public static void main(String[] args) {
//        TooltipDelayFix.debugTooltipConstructors();
//        TooltipDelayFix.debugTooltipFields();
        launch(args);
    }
}