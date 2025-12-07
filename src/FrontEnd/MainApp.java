package FrontEnd;

import BackEnd.DatabaseManager;
import BackEnd.MoneyManager;
import FrontEnd.Controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            // 1. Khởi tạo DB
            DatabaseManager.initializeDatabase();

            // 2. Khởi tạo MoneyManager (chỉ truyền idUser)
            MoneyManager moneyManager = new MoneyManager("default_user");

            // 3. Load giao diện
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("views/MainView.fxml"));
            Scene scene = new Scene(loader.load());

            // 4. Gắn MoneyManager vào Controller
            MainController controller = loader.getController();
            controller.setMoneyManager(moneyManager);

            primaryStage.setTitle("Quản Lý Chi Tiêu");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khởi tạo ứng dụng: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
