package adminfunc.adminadd;

import Course.Degree;
import Course.Department;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;

public class Admin_Degree_Add {

    @FXML private ComboBox<String> deptComboBox;
    @FXML private ComboBox<String> comboDegreeLevel; // Changed to ComboBox
    @FXML private TextField txtDegreeId;
    @FXML private TextField txtDegreeName;
    @FXML private TextField txtDuration;
    @FXML private TextField txtCredits,txtCost;
    @FXML private Button btnSave;

    @FXML
    public void initialize() {
        LoginController a = new LoginController();
        Admin admin = a.getAdminUser();

        // Populate Departments
        if (admin != null) {
            for (Department d : admin.getAdminDepartments()) {
                deptComboBox.getItems().add(d.getDepartmentName());
            }
        }

        // Populate Degree Levels
        comboDegreeLevel.getItems().addAll("Bachelor", "Master");
    }

    @FXML
    private void handleSaveDegree() {
        try {
            LoginController a = new LoginController();
            Admin admin = a.getAdminUser();

            String selectedDept = deptComboBox.getValue();
            String level = comboDegreeLevel.getValue();
            String id = txtDegreeId.getText();
            String name = txtDegreeName.getText();
            Double degreeCost = Double.parseDouble(txtCost.getText());

            if (selectedDept == null || level == null || id.isEmpty() || name.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            Department deptSelected = null;
            for (Department d : admin.getAdminDepartments()) {
                if (selectedDept.equals(d.getDepartmentName())) {
                    deptSelected = d;
                    break;
                }
            }

            int duration = Integer.parseInt(txtDuration.getText());
            int credits = Integer.parseInt(txtCredits.getText());

            Degree newDegree = new Degree(id, name, level, duration, credits, deptSelected,degreeCost);
            admin.addAdminDegrees(newDegree);
            deptSelected.getDepartmentDegrees().add(newDegree);

            a.saveAdminUser();
            showAlert("Success", "Degree created successfully!");
            clearFields();

        } catch (NumberFormatException | IOException e) {
            showAlert("Error", "Invalid numeric input or save error.");
        }
    }

    private void clearFields() {
        txtDegreeId.clear();
        txtDegreeName.clear();
        txtDuration.clear();
        txtCredits.clear();
        deptComboBox.getSelectionModel().clearSelection();
        comboDegreeLevel.getSelectionModel().clearSelection();
        txtCost.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}