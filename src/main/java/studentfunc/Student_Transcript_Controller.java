// UPDATED Student.java - Key Changes in completeCourse() method:
// DO NOT remove final grade when moving course to finished list
// This preserves the grade so transcript can display it

package studentfunc;

import Course.Course;
import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import userTypes.Student;

import java.io.IOException;
import java.util.ArrayList;

public class Student_Transcript_Controller {

    @FXML private Label studentNameLabel;
    @FXML private Label gpaLabel;
    @FXML private VBox coursesContainer;
    @FXML private ScrollPane scrollPane;

    private Student s = LoginController.getStudentUser();

    @FXML
    public void initialize() {
        if (s != null) {
            studentNameLabel.setText(s.getFirstName() + " " + s.getLastName());
            calculateAndDisplayGPA();
            loadTranscript();
        }
    }

    private void calculateAndDisplayGPA() {
        // Use the Student's built-in GPA calculation which already considers finished courses
        double gpa = s.getStudentGPA();
        gpaLabel.setText(String.format("%.2f", gpa));
    }

    private void loadTranscript() {
        coursesContainer.getChildren().clear();

        ArrayList<Course> finishedCourses = s.getStudentCourseFinished();
        ArrayList<Double> finalGrades = s.getStudentFinalGrades();

        if (finishedCourses == null || finishedCourses.isEmpty()) {
            // Show empty state
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 100;");

            Label emptyIcon = new Label("📋");
            emptyIcon.setStyle("-fx-font-size: 60px;");

            Label emptyMessage = new Label("No Completed Courses");
            emptyMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #666;");

            Label emptySubtext = new Label("Your transcript will appear here once you complete courses");
            emptySubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");

            emptyState.getChildren().addAll(emptyIcon, emptyMessage, emptySubtext);
            coursesContainer.getChildren().add(emptyState);
        } else {
            // Display finished courses with their grades
            for (int i = 0; i < finishedCourses.size(); i++) {
                Course course = finishedCourses.get(i);
                // Get the corresponding final grade (if available)
                double finalGrade = (i < finalGrades.size()) ? finalGrades.get(i) : 0.0;

                HBox courseRow = createCourseRow(course, finalGrade);
                coursesContainer.getChildren().add(courseRow);
            }
        }
    }

    private HBox createCourseRow(Course course, double finalGrade) {
        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 15, 0));

        // Course name
        VBox nameBox = new VBox(5);
        Label courseName = new Label(course.getCourseName());
        courseName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label courseId = new Label("Course ID: " + course.getCourseID());
        courseId.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        nameBox.getChildren().addAll(courseName, courseId);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        // Credits
        VBox creditsBox = new VBox(5);
        creditsBox.setAlignment(Pos.CENTER);
        Label creditsLabel = new Label("Credits");
        creditsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label creditsValue = new Label(String.valueOf(course.getCourseCredit()));
        creditsValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        creditsBox.getChildren().addAll(creditsLabel, creditsValue);

        // Final Grade
        VBox gradeBox = new VBox(5);
        gradeBox.setAlignment(Pos.CENTER);
        Label gradeLabel = new Label("Final Grade");
        gradeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Determine color based on grade (0.0 - 4.0 scale)
        String color;
        if (finalGrade >= 3.5) {
            color = "#4CAF50"; // Green (A)
        } else if (finalGrade >= 3.0) {
            color = "#8BC34A"; // Light green (B+)
        } else if (finalGrade >= 2.5) {
            color = "#FFC107"; // Yellow (B)
        } else if (finalGrade >= 2.0) {
            color = "#FF9800"; // Orange (C+)
        } else if (finalGrade >= 1.0) {
            color = "#FF5722"; // Deep orange (C/D)
        } else {
            color = "#999"; // Gray (no grade)
        }

        Label gradeValue = new Label(finalGrade == 0.0 ? "-" : String.format("%.1f / 4.0", finalGrade));
        gradeValue.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        gradeBox.getChildren().addAll(gradeLabel, gradeValue);

        row.getChildren().addAll(nameBox, creditsBox, gradeBox);

        return row;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/student_view.fxml"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}