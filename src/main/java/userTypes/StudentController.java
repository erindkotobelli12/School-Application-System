package userTypes;
import Login.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import java.io.IOException;

public class StudentController {
    @FXML private BorderPane mainLayout;
    @FXML private VBox sideBar;
    private boolean isCollapsed = false;
    @FXML private Label userNameLabel;
    @FXML private Label welcomeNameLabel;

    Student s = LoginController.studentUser;

    @FXML
    public void initialize() {
        if (s != null) {
            // Top bar gets full name
            userNameLabel.setText(s.getFirstName() + " " + s.getLastName());
            // Welcome message gets the last name
            if(s.getGender().equals("Male")){
                welcomeNameLabel.setText("Mr. " + s.getLastName());
            } else {
                welcomeNameLabel.setText("Ms. " + s.getLastName());

            }
        }
    }

    @FXML
    private void toggleSidebar() {
        if (isCollapsed) {
            mainLayout.setLeft(sideBar); // Put sidebar back
        } else {
            mainLayout.setLeft(null); // Remove sidebar
        }
        isCollapsed = !isCollapsed;
    }

    @FXML
    private void Profile_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_profile_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Student Profile");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_profile_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Courses_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_courses_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Courses");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_profile_view.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void Transportation_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_transportation_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Transportation");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_profile_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Finance_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_finance_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Finance");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_finance_view.fxml");
            e.printStackTrace();
        }
    }

    @FXML
    private void Grades_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_grades_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Grades");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_grades_view.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void Transcript_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/studentfunction/student_transcript_view.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Transcript");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error: Could not find student_grades_view.fxml");
            e.printStackTrace();
        }
    }
    @FXML
    private void logout(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Could not find or load login_view.fxml. check the file path!");
        }
    }


}
