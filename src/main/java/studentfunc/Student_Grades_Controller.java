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

public class Student_Grades_Controller {

    @FXML private Label studentNameLabel;
    @FXML private VBox coursesContainer;
    @FXML private ScrollPane scrollPane;

    private Student s = LoginController.getStudentUser();

    @FXML
    public void initialize() {
        if (s != null) {
            studentNameLabel.setText(s.getFirstName() + " " + s.getLastName());
            loadGrades();
        }
    }

    private void loadGrades() {
        coursesContainer.getChildren().clear();

        ArrayList<Course> currentCourses = s.getStudentCurrentCourses();

        if (currentCourses == null || currentCourses.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setStyle("-fx-padding: 100;");

            Label emptyIcon = new Label("📚");
            emptyIcon.setStyle("-fx-font-size: 60px;");

            Label emptyMessage = new Label("No Current Courses");
            emptyMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #666;");

            Label emptySubtext = new Label("Enroll in courses to see your grades here");
            emptySubtext.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");

            emptyState.getChildren().addAll(emptyIcon, emptyMessage, emptySubtext);
            coursesContainer.getChildren().add(emptyState);
        } else {
            for (int i = 0; i < currentCourses.size(); i++) {
                Course course = currentCourses.get(i);
                VBox courseCard = createCourseCard(course, i);
                coursesContainer.getChildren().add(courseCard);
            }
        }
    }

    private VBox createCourseCard(Course course, int courseIndex) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 5; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        VBox.setMargin(card, new javafx.geometry.Insets(0, 0, 20, 0));

        // Course header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label courseName = new Label(course.getCourseName());
        courseName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label courseInfo = new Label("Course ID: " + course.getCourseID() + " | Credits: " + course.getCourseCredit());
        courseInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        titleBox.getChildren().addAll(courseName, courseInfo);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        header.getChildren().add(titleBox);

        // Grades section
        HBox gradesSection = createGradesSection(courseIndex);

        // Attendance section
        HBox attendanceSection = createAttendanceSection(course, courseIndex);

        card.getChildren().addAll(header, new Separator(), gradesSection, new Separator(), attendanceSection);

        return card;
    }

    private HBox createGradesSection(int courseIndex) {
        HBox section = new HBox(30);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setStyle("-fx-padding: 10;");

        Label sectionTitle = new Label("Grades:");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-min-width: 100;");

        // Get grades
        double midtermGrade = 0.0;
        double finalGrade = 0.0;

        if (courseIndex < s.getCurrentCourseGrades().size()) {
            ArrayList<Double> grades = s.getCurrentCourseGrades().get(courseIndex);
            if (grades.size() >= 2) {
                midtermGrade = grades.get(0);
                finalGrade = grades.get(1);
            }
        }

        // Midterm
        VBox midtermBox = createGradeBox("Midterm Exam", midtermGrade, 40);

        // Final
        VBox finalBox = createGradeBox("Final Exam", finalGrade, 60);

        // Final calculated grade
        double calculatedFinal = (midtermGrade * 0.4) + (finalGrade * 0.6);
        VBox finalCalcBox = createFinalGradeBox(calculatedFinal);

        section.getChildren().addAll(sectionTitle, midtermBox, finalBox, finalCalcBox);

        return section;
    }

    private VBox createGradeBox(String name, double grade, int weight) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label weightLabel = new Label(weight + "%");
        weightLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");

        // Determine color based on grade
        String color;
        if (grade >= 90) {
            color = "#4CAF50"; // Green
        } else if (grade >= 80) {
            color = "#8BC34A"; // Light green
        } else if (grade >= 70) {
            color = "#FFC107"; // Yellow
        } else if (grade >= 60) {
            color = "#FF9800"; // Orange
        } else if (grade > 0) {
            color = "#F44336"; // Red
        } else {
            color = "#999"; // Gray for no grade
        }

        Label gradeLabel = new Label(grade == 0.0 ? "-" : String.format("%.0f/100", grade));
        gradeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        box.getChildren().addAll(nameLabel, weightLabel, gradeLabel);

        return box;
    }

    private VBox createFinalGradeBox(double grade) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10; -fx-background-color: #E3F2FD; -fx-background-radius: 5; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 5;");

        Label nameLabel = new Label("Final Grade");
        nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1976D2; -fx-font-weight: bold;");

        // Determine color based on grade
        String color;
        if (grade >= 90) {
            color = "#4CAF50";
        } else if (grade >= 80) {
            color = "#8BC34A";
        } else if (grade >= 70) {
            color = "#FFC107";
        } else if (grade >= 60) {
            color = "#FF9800";
        } else if (grade > 0) {
            color = "#F44336";
        } else {
            color = "#999";
        }

        Label gradeLabel = new Label(grade == 0.0 ? "-" : String.format("%.2f/100", grade));
        gradeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        box.getChildren().addAll(nameLabel, gradeLabel);

        return box;
    }

    private HBox createAttendanceSection(Course course, int courseIndex) {
        HBox section = new HBox(30);
        section.setAlignment(Pos.CENTER_LEFT);
        section.setStyle("-fx-padding: 10;");

        Label sectionTitle = new Label("Attendance:");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-min-width: 100;");

        // Theory attendance
        VBox theoryBox = createAttendanceBox("Theory", course, courseIndex, true);

        // Lab attendance (only if course has lab)
        VBox labBox = null;
        if (course.isCourseHasLabHours()) {
            labBox = createAttendanceBox("Laboratory", course, courseIndex, false);
        }

        section.getChildren().add(sectionTitle);
        section.getChildren().add(theoryBox);
        if (labBox != null) {
            section.getChildren().add(labBox);
        }

        return section;
    }

    private VBox createAttendanceBox(String type, Course course, int courseIndex, boolean isTheory) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");

        Label typeLabel = new Label(type);
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Calculate attendance
        int attended = 0;
        int total = 0;

        if (isTheory) {
            // Theory attendance
            total = course.getCourseTheoHours();
            if (courseIndex < s.getCurrentTheoryHours().size()) {
                ArrayList<Integer> theoryHours = s.getCurrentTheoryHours().get(courseIndex);
                for (int hour : theoryHours) {
                    if (hour == 1) attended++;
                }
            }
        } else {
            // Lab attendance
            total = course.getCourseLabHours();
            if (courseIndex < s.getCurrentlabHours().size()) {
                ArrayList<Integer> labHours = s.getCurrentlabHours().get(courseIndex);
                for (int hour : labHours) {
                    if (hour == 1) attended++;
                }
            }
        }

        double percentage = (total > 0) ? (attended * 100.0 / total) : 0.0;

        // Determine color based on threshold
        String color;
        boolean isBelowThreshold;

        if (isTheory) {
            // Theory: red if below 60%
            isBelowThreshold = percentage < 60;
            color = isBelowThreshold ? "#F44336" : "#4CAF50";
        } else {
            // Lab: red if below 80%
            isBelowThreshold = percentage < 80;
            color = isBelowThreshold ? "#F44336" : "#4CAF50";
        }

        Label attendedLabel = new Label(attended + " / " + total);
        attendedLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");

        Label percentageLabel = new Label(String.format("%.1f%%", percentage));
        percentageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        // Warning icon if below threshold
        if (isBelowThreshold && percentage > 0) {
            Label warningIcon = new Label("⚠");
            warningIcon.setStyle("-fx-font-size: 14px;");
            box.getChildren().add(warningIcon);
        }

        box.getChildren().addAll(typeLabel, attendedLabel, percentageLabel);

        return box;
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