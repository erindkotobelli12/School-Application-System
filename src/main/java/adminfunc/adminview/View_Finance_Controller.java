package adminfunc.adminview;

import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;
import userTypes.Student;

import java.io.IOException;

public class View_Finance_Controller {

    @FXML private Label lblTotalIncome;
    @FXML private Label lblTotalExpenses;
    @FXML private Label lblNetProfit;
    @FXML private Label lblExpectedIncome;
    @FXML private Label lblStudentCount;
    @FXML private Label lblProfessorCount;
    @FXML private Label lblAveragePayment;
    @FXML private Label lblAverageSalary;

    @FXML private PieChart financePieChart;
    @FXML private BarChart<String, Number> incomeExpenseChart;

    private Admin admin;

    @FXML
    public void initialize() {
        admin = LoginController.getAdminUser();
        loadFinancialData();
    }

    private void loadFinancialData() {
        // Calculate income from students
        double totalIncome = 0.0;
        double expectedIncome = 0.0;
        int studentCount = 0;

        for (Student s : admin.getAdminStudents()) {
            totalIncome += s.getAMTpayed();
            if (s.getStudentDegree() != null) {
                expectedIncome += s.getStudentDegree().getDegreeCost();
            }
            studentCount++;
        }

        // Calculate expenses from professors
        double totalExpenses = 0.0;
        int professorCount = 0;

        for (Professor p : admin.getAdminProfessors()) {
            totalExpenses += p.getProfessorSalary();
            professorCount++;
        }

        // Calculate net profit
        double netProfit = totalIncome - totalExpenses;

        // Calculate averages
        double averagePayment = studentCount > 0 ? totalIncome / studentCount : 0.0;
        double averageSalary = professorCount > 0 ? totalExpenses / professorCount : 0.0;

        // Update labels
        lblTotalIncome.setText(String.format("€%.2f", totalIncome));
        lblTotalExpenses.setText(String.format("€%.2f", totalExpenses));
        lblNetProfit.setText(String.format("€%.2f", netProfit));
        lblExpectedIncome.setText(String.format("€%.2f", expectedIncome));
        lblStudentCount.setText(String.valueOf(studentCount));
        lblProfessorCount.setText(String.valueOf(professorCount));
        lblAveragePayment.setText(String.format("€%.2f", averagePayment));
        lblAverageSalary.setText(String.format("€%.2f", averageSalary));

        // Color code net profit
        if (netProfit >= 0) {
            lblNetProfit.setStyle("-fx-text-fill: #5cb85c; -fx-font-weight: bold; -fx-font-size: 24px;");
        } else {
            lblNetProfit.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold; -fx-font-size: 24px;");
        }

        // Load Pie Chart
        loadPieChart(totalIncome, totalExpenses);

        // Load Bar Chart
        loadBarChart(totalIncome, totalExpenses, expectedIncome);
    }

    private void loadPieChart(double income, double expenses) {
        financePieChart.getData().clear();

        PieChart.Data incomeData = new PieChart.Data("Income: €" + String.format("%.2f", income), income);
        PieChart.Data expenseData = new PieChart.Data("Expenses: €" + String.format("%.2f", expenses), expenses);

        financePieChart.getData().addAll(incomeData, expenseData);
        financePieChart.setLegendVisible(true);
    }

    private void loadBarChart(double actualIncome, double expenses, double expectedIncome) {
        incomeExpenseChart.getData().clear();

        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual");
        actualSeries.getData().add(new XYChart.Data<>("Income", actualIncome));
        actualSeries.getData().add(new XYChart.Data<>("Expenses", expenses));

        XYChart.Series<String, Number> expectedSeries = new XYChart.Series<>();
        expectedSeries.setName("Expected");
        expectedSeries.getData().add(new XYChart.Data<>("Income", expectedIncome));
        expectedSeries.getData().add(new XYChart.Data<>("Expenses", expenses));

        incomeExpenseChart.getData().addAll(actualSeries, expectedSeries);
    }


    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_view_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}