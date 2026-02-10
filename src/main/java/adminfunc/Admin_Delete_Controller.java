package adminfunc;

import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import userTypes.Admin;

import java.io.IOException;
import userTypes.Admin;
public class Admin_Delete_Controller {

    @FXML private Label adminNameLabel;
    LoginController a=new LoginController();
    Admin admin=a.getAdminUser();

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Adjust this path to match your project's Admin Dashboard location
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin_view.fxml"));
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
    private void adminStudentsDelete(ActionEvent event) {
       if(!admin.getAdminStudents().isEmpty()) {
           try {
               // Adjust this path to match your project's Admin Dashboard location
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_student_delete.fxml"));
               Parent root = loader.load();

               Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
               Scene scene = new Scene(root, 900, 600);
               stage.setTitle("Student Deletion");
               stage.setScene(scene);
               stage.show();
           } catch (IOException e) {
               System.err.println("Error: Could not return to the dashboard.");
               e.printStackTrace();
           }
       }
       else{
           showAlert("Error","You don't have any Students assigned to you. Cannot delete students.");
       }
    }

    @FXML
    private void adminProfessorDelete(ActionEvent event) {
        if(!admin.getAdminProfessors().isEmpty()) {
            try {
                // Adjust this path to match your project's Admin Dashboard location
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_professor_delete.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Professor Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else{
            showAlert("Error","You don't have any Professors assigned to you. Cannot delete professors.");
        }
    }

    @FXML
    private void adminCoursesDelete(ActionEvent event) {
        if(!admin.getAdminCourses().isEmpty()) {
            try {
                // Adjust this path to match your project's Admin Dashboard location
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_course_delete.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Course Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else {
            showAlert("Error", "You don't have any Courses assigned to you. Cannot delete courses.");
        }
    }

    @FXML
    private void adminClassroomDelete(ActionEvent event) {
        if(!admin.getAdminClassrooms().isEmpty()) {
            try {
                // Adjust this path to match your project's Admin Dashboard location
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_classroom_delete.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Classroom Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else{
            showAlert("Error","You don't have any Classrooms assigned to you. Cannot delete classrooms.");
        }
    }

    @FXML
    private void adminDegreeDelete(ActionEvent event) {
        if(!admin.getAdminDegrees().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_degree_delete.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Degree Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else{
            showAlert("Error","You don't have any Degrees assigned to you. Cannot delete degrees.");
        }

    }

    @FXML
    private void adminDepartmentDelete(ActionEvent event) {
        if(!admin.getAdminDepartments().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_department_delete.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Department Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else{
            showAlert("Error","You don't have any Departments assigned to you. Cannot delete departments.");
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void adminDisconnect(ActionEvent event) {
        if(!admin.getAdminProfessors().isEmpty() && !admin.getAdminClassrooms().isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admindelete/admin_disconnect.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 900, 600);
                stage.setTitle("Department Deletion");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error: Could not return to the dashboard.");
                e.printStackTrace();
            }
        }
        else{
            showAlert("Error","You don't have any Teachers or Classrooms assigned to you. Cannot disconnect.");
        }

    }

}