package adminfunc.adminadd;

import Course.Department;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;

public class Admin_Department_Add {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtCode;
    @FXML
    private TextField txtLocation;
    @FXML
    private Button btnAdd;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveDepartment() throws IOException {
        // 1. Capture data from UI
        String id = txtId.getText();
        String name = txtName.getText();
        String code = txtCode.getText();
        String location = txtLocation.getText();

        // 2. Validation (Simple check to ensure fields aren't empty)
        if (id.isEmpty() || name.isEmpty() || code.isEmpty() || location.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        LoginController a= new LoginController();
        Admin admin=a.getAdminUser();
        Department newDept = new Department(id, name, code, location);
        admin.addAdminDepartments(newDept);
        a.saveAdminUser();


        showAlert("Success", "Department '" + newDept.getDepartmentName() + "' added successfully!");

        // Optional: Clear fields after saving
        clearFields();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtCode.clear();
        txtLocation.clear();
    }
}