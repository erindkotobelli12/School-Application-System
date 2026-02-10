package adminfunc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class Admin_Edit_Controller {

    @FXML
    private void handleAdd(ActionEvent event) {
        try {
            // Adjust this path to match your project's Admin Dashboard location
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_add_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setTitle("Add");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error: Could not return to the dashboard.");
            e.printStackTrace();
        }

    }

    @FXML
    private void handleModify(ActionEvent event) {
        try {
            // Adjust this path to match your project's Admin Dashboard location
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfunction/admin_edit_edit_view.fxml"));
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
}