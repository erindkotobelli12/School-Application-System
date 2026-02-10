package studentfunc;

import Course.Degree;
import Login.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.Event; // Using general Event
import javafx.stage.Stage;
import userTypes.Student;

import java.io.IOException;

public class Student_Finance_Controller {

    @FXML private Label userNameLabel;
    @FXML private Label totalTuitionLabel;
    @FXML private Label paidAmountLabel;
    @FXML private Label remainingAmountLabel;

    @FXML private TableView<BankInfo> bankTable;
    @FXML private TableColumn<BankInfo, String> bankNameCol;
    @FXML private TableColumn<BankInfo, String> accountNumCol;
    @FXML private TableColumn<BankInfo, String> currencyCol;
    @FXML private TableColumn<BankInfo, String> ibanCol;
    @FXML private TableColumn<BankInfo, String> swiftCol;
    @FXML private TableColumn<BankInfo, String> descCol;

    private Student student;

    @FXML
    public void initialize() {
        student = LoginController.getStudentUser();

        if (student != null) {
            userNameLabel.setText(student.getFirstName() + " " + student.getLastName());
            loadFinancialInfo();
        }

        // Table Mapping
        bankNameCol.setCellValueFactory(new PropertyValueFactory<>("bankName"));
        accountNumCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        currencyCol.setCellValueFactory(new PropertyValueFactory<>("currency"));
        ibanCol.setCellValueFactory(new PropertyValueFactory<>("iban"));
        swiftCol.setCellValueFactory(new PropertyValueFactory<>("swift"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load bank data
        ObservableList<BankInfo> bankList = FXCollections.observableArrayList(
                new BankInfo("Intesa Sanpaolo Bank", "0000000000", "EUR", "AL11 1111 1111 1111 1111 1111", "ALBANIA", "Tuition Payment"),
                new BankInfo("Raiffeisen Bank", "11111111111", "EUR", "AL22 2222 2222 2222 2222 2222", "QQQQQQ", "Alternative Payment")
        );

        bankTable.setItems(bankList);

        // Responsive Table height adjustment
        bankTable.setFixedCellSize(40);
        bankTable.prefHeightProperty().bind(bankTable.fixedCellSizeProperty().multiply(bankList.size()).add(35));
    }

    private void loadFinancialInfo() {
        // Safe check to ensure labels were injected by FXML
        if (totalTuitionLabel == null || paidAmountLabel == null) return;

        if (student.getStudentDegree() != null) {
            Degree degree = student.getStudentDegree();
            double totalCost = degree.getDegreeCost();
            double paidAmount = student.getAMTpayed();
            double remaining = totalCost - paidAmount;

            totalTuitionLabel.setText(String.format("€%.2f", totalCost));
            paidAmountLabel.setText(String.format("€%.2f", paidAmount));
            remainingAmountLabel.setText(String.format("€%.2f", remaining));

            if (remaining <= 0) {
                remainingAmountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #5cb85c; -fx-font-size: 16px;");
            } else {
                remainingAmountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-font-size: 16px;");
            }
        } else {
            totalTuitionLabel.setText("€0.00");
            paidAmountLabel.setText("€0.00");
            remainingAmountLabel.setText("€0.00");
        }
    }

    @FXML
    private void handleBack(Event event) {
        try {
            // Updated path to match your structure
            Parent root = FXMLLoader.load(getClass().getResource("/student_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            System.err.println("Navigation error: Could not find /student_view.fxml");
            e.printStackTrace();
        }
    }

    // Inner class for TableView
    public static class BankInfo {
        private String bankName, accountNumber, currency, iban, swift, description;
        public BankInfo(String bankName, String accountNumber, String currency, String iban, String swift, String description) {
            this.bankName = bankName; this.accountNumber = accountNumber; this.currency = currency;
            this.iban = iban; this.swift = swift; this.description = description;
        }
        public String getBankName() { return bankName; }
        public String getAccountNumber() { return accountNumber; }
        public String getCurrency() { return currency; }
        public String getIban() { return iban; }
        public String getSwift() { return swift; }
        public String getDescription() { return description; }
    }
}