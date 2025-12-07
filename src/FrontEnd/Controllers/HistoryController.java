package FrontEnd.Controllers;

import BackEnd.MoneyManager;
import BackEnd.PurposeUseMoney;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Collectors;

public class HistoryController {

    @FXML private TableView<PurposeUseMoney> historyTable;
    @FXML private TableColumn<PurposeUseMoney, LocalDateTime> dateColumn;
    @FXML private TableColumn<PurposeUseMoney, String> categoryColumn;
    @FXML private TableColumn<PurposeUseMoney, String> purposeColumn;
    @FXML private TableColumn<PurposeUseMoney, BigDecimal> moneyColumn;
    @FXML private Label filterLabel;

    private MoneyManager moneyManager;

    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
        initializeTable();
        loadHistory();
    }

    private void initializeTable() {
        // Các tên thuộc tính này phải khớp với getter trong PurposeUseMoney (ví dụ: getLocalDateTime)
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("localDateTime"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("chucNangCoDinh"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purposeName"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("money"));

        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadHistory() {
        if (moneyManager.getChucNangList() != null) {

            // MoneyManager đã load lịch sử từ DB vào bộ nhớ (History List trong mỗi ChucNang)
            ObservableList<PurposeUseMoney> allHistory = moneyManager.getChucNangList().stream()
                    .flatMap(cn -> {
                        // setCN() đã được gọi trong MoneyManager.loadAllData()
                        return cn.getHistory().stream();
                    })
                    // Sắp xếp theo thời gian mới nhất (DESC)
                    .sorted(Comparator.comparing(PurposeUseMoney::getLocalDateTime).reversed())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            historyTable.setItems(allHistory);
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}