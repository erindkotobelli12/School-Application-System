package adminfunc.adminedit;

import Course.Department;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;

public class Admin_Department_Edit {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtCode;
    @FXML private TextField txtLocation;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        //text qe bon qe kur ndryshon id-ja te terhiqen te dhenat e departamentit
        txtId.textProperty().addListener((observable, oldValue, newValue) -> {
            fetchDepartmentDetails(newValue);
        });
    }

    private void fetchDepartmentDetails(String id) {
        if (id == null || id.isEmpty()) {
            clearOtherFields();
            return;
        }

        // Search for department in admin's list
        for (Department dept : admin.getAdminDepartments()) {
            if (dept.getDepartmentId().equals(id)) {
                txtName.setText(dept.getDepartmentName());
                txtCode.setText(dept.getDepartmentCode());
                txtLocation.setText(dept.getDepartmentLocation());
                return;
            }
        }
        // If no match is found, clear the fields (optional)
        clearOtherFields();
    }

    @FXML
    private void handleUpdateDepartment() throws IOException {
        String id = txtId.getText();

        Department targetDept = null;
        for (Department dept : admin.getAdminDepartments()) {
            if (dept.getDepartmentId().equals(id)) {
                targetDept = dept;
                break;
            }
        }

        if (targetDept != null) {
            // Update the object details
            targetDept.setDepartmentName(txtName.getText());
            targetDept.setDepartmentCode(txtCode.getText());
            targetDept.setDepartmentLocation(txtLocation.getText());

            loginController.saveStudentUser();
            loginController.saveAdminProfessors();
            loginController.saveAdminUser();
            showAlert("Success", "Department updated successfully!");
        } else {
            showAlert("Error", "No department found with ID: " + id);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_edit_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearOtherFields() {
        txtName.clear();
        txtCode.clear();
        txtLocation.clear();
    }
}