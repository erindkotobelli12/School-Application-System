package adminfunc;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class Admin_View_Controller {

    @FXML
    private void handleBack(Event event) {
        // Based on your image: admin_view_view.fxml is in the root of adminfunction
        switchScene(event, "/admin_view.fxml");
    }

    @FXML
    private void student(Event event) {
        // Image shows: adminfunction -> adminview -> view_student_view.fxml
        switchScene(event, "/adminfunction/adminview/view_student_view.fxml");
    }

    @FXML
    private void professor(Event event) {
        // Image shows a typo in your filename: "porfessor"
        switchScene(event, "/adminfunction/adminview/view_porfessor_view.fxml");
    }

    @FXML
    private void finance(Event event) {
        // Image shows: adminfunction -> adminview -> view_finance_view.fxml
        switchScene(event, "/adminfunction/adminview/view_finance_view.fxml");
    }

    private void switchScene(Event event, String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("FATAL ERROR: Path not found! Check spelling: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Using 1000x700 to match your FXML's preferred size
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading the FXML file.");
            e.printStackTrace();
        }
    }
}