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

public class Admin_Degree_Delete {

    @FXML private TextField txtDegreeId;
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
    private void handleDeleteDegree() throws IOException {
        String idInput = txtDegreeId.getText().trim();

        if (idInput.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "ID Missing", "Please enter the ID of the degree you wish to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Degree Deletion");
        confirm.setHeaderText("Delete Degree ID: " + idInput);
        confirm.setContentText("Warning: This will remove the degree from the system permanently.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LoginController a= new LoginController();
            Admin admin = a.getAdminUser();
            for(Degree degree : admin.getAdminDegrees()) {
                if(idInput.equals(degree.getDegreeId())) {
                    admin.getAdminDegrees().remove(degree);
                    for(int i=0;i<admin.getAdminStudents().size();i++){
                        if(admin.getAdminStudents().get(i).getStudentDegree().getDegreeId().equals(degree.getDegreeId())) {;
                            admin.getAdminStudents().get(i).setStudentDegree(null);
                        }
                    }
                    for(Department department : admin.getAdminDepartments()) {
                        for(Degree deptDegree : department.getDepartmentDegrees()) {
                            if(deptDegree.getDegreeId().equals(degree.getDegreeId())) {
                                department.getDepartmentDegrees().remove(deptDegree);
                                break;
                            }
                        }
                    }

                    break;
                }


            }
            a.saveAdminUser();
            a.saveAdminProfessors();
            a.saveStudentUser();


            System.out.println("Deleting Degree with ID: " + idInput);

            txtDegreeId.clear();
            showAlert(Alert.AlertType.INFORMATION, "Action Complete", "Deletion processed for ID: " + idInput);
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