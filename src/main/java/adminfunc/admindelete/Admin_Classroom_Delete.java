package adminfunc.admindelete;

import Course.Classroom;
import Course.Course;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import userTypes.Admin;
import userTypes.Professor;
import java.io.IOException;

public class Admin_Classroom_Delete {

    @FXML private TextField txtClassroomId;

    @FXML
    private void handleDeleteClassroom() throws IOException {
        String idStr = txtClassroomId.getText().trim();
        if (idStr.isEmpty()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Classroom " + idStr + "?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().get() == ButtonType.YES) {
            LoginController lc = new LoginController();
            Admin admin = lc.getAdminUser();
            int targetId = Integer.parseInt(idStr);

            boolean removed = admin.getAdminClassrooms().removeIf(c -> c.getClassroomID() == targetId);

            if (removed) {
                for (Professor p : admin.getAdminProfessors()) {
                    p.getProfessorGivesLecture().removeIf(c -> c.getClassroomID() == targetId);
                }
                for(Course course: admin.getAdminCourses()) {
                    course.getClassrooms().removeIf(c -> c.getClassroomID() == targetId);
                }
                lc.saveAdminUser();
                lc.saveAdminProfessors();
                lc.saveStudentUser();
                txtClassroomId.clear();
                new Alert(Alert.AlertType.INFORMATION, "Deleted Successfully").show();
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminfunction/admin_delete_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) { e.printStackTrace(); }
    }
}