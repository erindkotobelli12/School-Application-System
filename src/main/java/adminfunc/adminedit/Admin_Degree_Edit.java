package adminfunc.adminedit;

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

public class Admin_Degree_Edit {

    @FXML private ComboBox<String> deptComboBox;
    @FXML private ComboBox<String> comboDegreeLevel; // Changed to ComboBox
    @FXML private TextField txtDegreeId;
    @FXML private TextField txtDegreeName;
    @FXML private TextField txtDuration;
    @FXML private TextField txtCredits,txtCost;

    private Admin admin;
    private LoginController loginController;

    @FXML
    public void initialize() {
        loginController = new LoginController();
        admin = loginController.getAdminUser();

        for (Department d : admin.getAdminDepartments()) {
            deptComboBox.getItems().add(d.getDepartmentName());
        }

        comboDegreeLevel.getItems().addAll("Bachelor", "Master");

        txtDegreeId.textProperty().addListener((obs, oldVal, newVal) -> {
            autoPopulate(newVal);
        });
    }

    private void autoPopulate(String id) {
        if (id == null || id.isEmpty()) {
            clearOtherFields();
            return;
        }

        for (Degree deg : admin.getAdminDegrees()) {
            if (deg.getDegreeId().equals(id)) {
                txtDegreeName.setText(deg.getDegreeName());
                txtDuration.setText(String.valueOf(deg.getDurationYears()));
                txtCredits.setText(String.valueOf(deg.getTotalCreditsRequired()));
                comboDegreeLevel.setValue(deg.getDegreeLevel());
                txtCost.setText(String.valueOf(deg.getDegreeCost()));

                if (deg.getDegreeDepartment() != null) {
                    deptComboBox.setValue(deg.getDegreeDepartment().getDepartmentName());
                }
                return;
            }
        }
        clearOtherFields();
    }

    @FXML
    private void handleUpdateDegree() {
        try {
            String id = txtDegreeId.getText();
            Degree target = null;

            for (Degree deg : admin.getAdminDegrees()) {
                if (deg.getDegreeId().equals(id)) {
                    target = deg;
                    break;
                }
            }

            if (target != null) {
                target.setDegreeName(txtDegreeName.getText());
                target.setDegreeLevel(comboDegreeLevel.getValue());
                target.setDurationYears(Integer.parseInt(txtDuration.getText()));
                target.setTotalCreditsRequired(Integer.parseInt(txtCredits.getText()));
                target.setDegreeCost(Double.parseDouble(txtCost.getText()));

                // Department Logic
                String selDept = deptComboBox.getValue();
                if (selDept != null && !selDept.equals(target.getDegreeDepartment().getDepartmentName())) {
                    for (Department d : admin.getAdminDepartments()) {
                        if (d.getDepartmentName().equals(selDept)) {
                            target.getDegreeDepartment().getDepartmentDegrees().remove(target);
                            target.setDegreeDepartment(d);
                            d.getDepartmentDegrees().add(target);
                            break;
                        }
                    }
                }
                loginController.saveAdminProfessors();
                loginController.saveStudentUser();
                loginController.saveAdminUser();
                showAlert("Success", "Degree updated!");
            }
        } catch (Exception e) {
            showAlert("Error", "Check numeric fields.");
        }
    }

    private void clearOtherFields() {
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
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_edit_edit_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}