package adminfunc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Admin_Edit_Edit_Controller {

    @FXML private Label adminNameLabel;
    @FXML private TilePane tilePane; // Matches fx:id in FXML
    @FXML private TextField searchField; // Matches fx:id in FXML

    // This list stores the original buttons so they don't disappear forever when filtered
    private ObservableList<Node> allButtons;

    @FXML
    public void initialize() {
        if (tilePane != null) {
            allButtons = FXCollections.observableArrayList(tilePane.getChildren());
        }
    }



    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
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
    private void adminStudentEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_student_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Student Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void adminProfessorEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_professor_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Professor Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void adminClassroomEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_classroom_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Classroom Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void adminCourseEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_course_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Course Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void adminDegreeEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_degree_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Degree Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void adminDepartmentEdit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/adminedit/admin_department_edit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Note: Keep the scene size consistent to avoid the jumping issue we discussed
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Dpartment Edit");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }
    }

}