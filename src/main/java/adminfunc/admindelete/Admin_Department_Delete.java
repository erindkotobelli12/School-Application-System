package adminfunc.admindelete;

import Course.Degree;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;
import java.util.Optional;

public class Admin_Department_Delete {

    @FXML private TextField txtDepartmentId;
    @FXML private Button btnDelete;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Adjust this path to match your project's Admin Dashboard location
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_delete_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Admin");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteDepartment() throws IOException {
        String idInput = txtDepartmentId.getText().trim();

        if (idInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Department ID.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Department ID: " + idInput);
        confirm.setContentText("Are you sure? All associated degrees will be lost.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Attempting to delete Dept ID: " + idInput);
            LoginController a= new LoginController();
            Admin admin=a.getAdminUser();
            for(Department department : admin.getAdminDepartments()){
                if(idInput.equals(String.valueOf(department.getDepartmentId()))){
                    admin.adminDeleteDepartment(department);

                    for(int i = 0; i < admin.getAdminDegreesSize(); i++){
                        if(idInput.equals(String.valueOf(admin.getAdminDegrees().get(i).getDegreeDepartment().getDepartmentId()))){
                            admin.getAdminDegrees().get(i).getDegreeDepartment().setDepartmentId(null);
                            i--;
                        }
                    }
                    for(int i=0;i<admin.getAdminProfessorsSize(); i++){
                        if(idInput.equals(String.valueOf(admin.getAdminProfessors().get(i).getProfessorDepartment().getDepartmentId()))){
                            admin.getAdminProfessors().get(i).setProfessorDepartment(null);
                            i--;
                        }
                    }
                    for(int i=0;i<admin.getAdminStudentsSize(); i++){
                        if(idInput.equals(String.valueOf(admin.getAdminStudents().get(i).getStudentDepartment().getDepartmentId()))){
                            admin.getAdminStudents().get(i).setStudentDepartment(null);
                            i--;
                        }
                    }

                    break;
                }


            }


            a.saveAdminUser();
            a.saveAdminProfessors();
            a.saveStudentUser();

            txtDepartmentId.clear();
            showAlert(Alert.AlertType.INFORMATION, "Success", "If ID " + idInput + " existed, it has been removed.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}