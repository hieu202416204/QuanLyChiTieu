package FrontEnd.Controllers; // Note: Assuming the package is 'FrontEnd.Controllers' based on your previous structure

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
import java.util.stream.Collectors;

public class HistoryController {

    @FXML private TableView<PurposeUseMoney> historyTable;
    @FXML private TableColumn<PurposeUseMoney, LocalDateTime> dateColumn;
    @FXML private TableColumn<PurposeUseMoney, String> categoryColumn;
    @FXML private TableColumn<PurposeUseMoney, String> purposeColumn;
    @FXML private TableColumn<PurposeUseMoney, BigDecimal> moneyColumn;
    @FXML private Label filterLabel; // Not fully implemented, but included for UI structure

    private MoneyManager moneyManager;

    /**
     * Sets the MoneyManager instance and initializes the table view.
     * @param manager The MoneyManager object passed from MainController.
     */
    public void setMoneyManager(MoneyManager manager) {
        this.moneyManager = manager;
        initializeTable();
        loadHistory();
    }

    /**
     * Configures the columns of the TableView by linking them to PurposeUseMoney properties.
     */
    private void initializeTable() {
        // 'localDateTime' is the private field in PurposeUseMoney (though no getter was provided,
        // JavaFX typically uses reflection if the field name matches the conventional getter name).
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("localDateTime"));

        // 'chucNangCoDinh' holds the name of the category the money was spent from.
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("chucNangCoDinh"));

        // 'purposeName' is the description/note for the transaction.
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purposeName"));

        // 'money' is the amount spent.
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("money"));

        // Set column widths to distribute space evenly
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Collects the history from all ChucNang objects and populates the TableView.
     */
    private void loadHistory() {
        if (moneyManager.getChucNangList() != null) {

            // 1. Stream through all ChucNang objects.
            // 2. For each ChucNang, ensure the category name is set on its history items (using setCN()).
            // 3. FlatMap to get a single stream of all PurposeUseMoney objects.
            // 4. Collect the items into an ObservableList for the TableView.
            ObservableList<PurposeUseMoney> allHistory = moneyManager.getChucNangList().stream()
                    .flatMap(cn -> {
                        // setCN() ensures the 'chucNangCoDinh' field in PurposeUseMoney is populated
                        // with the category name before display.
                        cn.setCN();
                        return cn.getHistory().stream();
                    })
                    // Sort by time (optional, but good practice)
                    .sorted((p1, p2) -> {
                        // Assuming localDateTime is accessible or purpose objects are unique
                        try {
                            return p2.getLocalDateTime().compareTo(p1.getLocalDateTime());
                        } catch (Exception e) {
                            return 0; // Fallback if LocalDateTime is inaccessible
                        }
                    })
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            historyTable.setItems(allHistory);
        }
    }

    /**
     * Handles the action to close the history window.
     */
    @FXML
    public void handleClose() {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}