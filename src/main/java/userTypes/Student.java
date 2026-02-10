package userTypes;

import Course.Course;
import Course.Degree;
import Course.Department;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
public class Student extends Person implements Serializable {
    // --- Identity and Status Fields ---
    private static int studentNextID = 10000;
    private int studentId;
    private String password;
    private int studentEnrollmentYear;
    private String studentEnrollmentStatus;
    private boolean studentIsActive;

    // --- Academic Progress Fields ---
    private double studentGPA = 0.0;
    private Degree studentDegree;
    private Department studentDepartment;
    private int studentNrOfCurrentCourses = 0;

    // --- finance---
    private double AMTpayed = 0.0;

    // --- Course Collections ---
    private ArrayList<Course> studentCurrentCourses = new ArrayList<>();
    private ArrayList<Course> studentCourseFinished = new ArrayList<>();

    // IMPORTANT: Final grades are kept permanently - for both current AND finished courses
    private ArrayList<Double> studentFinalGrades = new ArrayList<>();

    private ArrayList<ArrayList<Double>> CurrentCourseGrades = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> CurrentTheoryHours = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> CurrentlabHours = new ArrayList<>();

    // --- Constructor ---
    public Student(String firstName, String lastName, String dateOfBirth, String email, String phoneNumber, int age,
                   Department studentDepartment, Degree studentDegree, int studentEnrollmentYear,
                   String studentEnrollmentStatus, boolean studentIsActive, String gender, String password, String address) {

        super(firstName, lastName, dateOfBirth, email, phoneNumber, age, gender, address);
        this.studentId = studentNextID++;
        this.studentDepartment = studentDepartment;
        this.studentDegree = studentDegree;
        this.studentEnrollmentYear = studentEnrollmentYear;
        this.studentEnrollmentStatus = studentEnrollmentStatus;
        this.studentIsActive = studentIsActive;
        this.password = password;
    }

    // --- Course Management Methods ---

    public void studentAddCurrentCourse(Course course) {
        studentCurrentCourses.add(course);

        ArrayList<Double> newGrades = new ArrayList<>();
        ArrayList<Integer> newTheoryHours = new ArrayList<>();
        ArrayList<Integer> newLabHours = new ArrayList<>();

        for (int i = 0; i < course.getCourseGradeDistribution().size(); i++) {
            newGrades.add(0.0);
        }

        for (int i = 0; i < course.getCourseTheoHours(); i++) {
            newTheoryHours.add(0);
        }

        for (int i = 0; i < course.getCourseLabHours(); i++) {
            newLabHours.add(0);
        }

        CurrentCourseGrades.add(newGrades);
        CurrentTheoryHours.add(newTheoryHours);
        CurrentlabHours.add(newLabHours);

        // Add placeholder for final grade (will be calculated when course completes)
        studentFinalGrades.add(0.0);

        studentNrOfCurrentCourses++;

        System.out.println("DEBUG: Added course to student - Total courses: " + studentNrOfCurrentCourses);
        System.out.println("DEBUG: CurrentCourseGrades size: " + CurrentCourseGrades.size());
        System.out.println("DEBUG: Grades for this course: " + newGrades.size() + " slots");
    }

    public void clearCurrentCourses() {
        studentCurrentCourses.clear();
        CurrentCourseGrades.clear();
        CurrentTheoryHours.clear();
        CurrentlabHours.clear();
        studentNrOfCurrentCourses = 0;
    }


    /**
     * FIXED: Calculate GPA based on ALL courses - both current and finished
     */
    public void studentCalculateGPA() {
        double totalWeightedPoints = 0;
        double totalCredits = 0;

        // ArrayLists to track unique courses and their best grades
        ArrayList<Integer> processedCourseIDs = new ArrayList<>();
        ArrayList<Course> bestCourses = new ArrayList<>();
        ArrayList<Double> bestGrades = new ArrayList<>();

        // Calculate the offset: finished courses start after current courses in studentFinalGrades
        int finishedCoursesStartIndex = studentCurrentCourses.size();

        // Process finished courses
        for (int i = 0; i < studentCourseFinished.size(); i++) {
            Course course = studentCourseFinished.get(i);

            // The grade index for finished courses is offset by the number of current courses
            int gradeIndex = finishedCoursesStartIndex + i;

            if (gradeIndex < studentFinalGrades.size()) {
                double currentGrade = studentFinalGrades.get(gradeIndex);
                int courseID = course.getCourseID();

                // Check if we've seen this course before
                int existingIndex = processedCourseIDs.indexOf(courseID);

                if (existingIndex != -1) {
                    // Course already exists - check if current grade is higher
                    if (currentGrade > bestGrades.get(existingIndex)) {
                        bestCourses.set(existingIndex, course);
                        bestGrades.set(existingIndex, currentGrade);
                    }
                } else {
                    // First time seeing this course - add it
                    processedCourseIDs.add(courseID);
                    bestCourses.add(course);
                    bestGrades.add(currentGrade);
                }
            }
        }

        // Calculate GPA using ALL unique courses (including those with grade 0)
        for (int i = 0; i < bestCourses.size(); i++) {
            double credits = bestCourses.get(i).getCourseCredit();
            double grade = bestGrades.get(i);

            totalWeightedPoints += credits * grade;
            totalCredits += credits;
        }

        if (totalCredits > 0) {
            this.studentGPA = totalWeightedPoints / totalCredits;
        } else {
            this.studentGPA = 0.0;
        }
    }


    // --- Getters & Setters ---

    public static void setStudentNextID(int value) { studentNextID = value; }
    public int getStudentId() { return studentId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Department getStudentDepartment() { return studentDepartment; }
    public void setStudentDepartment(Department studentDepartment) { this.studentDepartment = studentDepartment; }

    public Degree getStudentDegree() { return studentDegree; }
    public void setStudentDegree(Degree studentDegree) { this.studentDegree = studentDegree; }

    public int getStudentEnrollmentYear() { return studentEnrollmentYear; }
    public void setStudentEnrollmentYear(int year) { this.studentEnrollmentYear = year; }

    public String getStudentEnrollmentStatus() { return studentEnrollmentStatus; }
    public void setStudentEnrollmentStatus(String status) { this.studentEnrollmentStatus = status; }

    public boolean isStudentIsActive() { return studentIsActive; }
    public void setStudentIsActive(boolean active) { this.studentIsActive = active; }

    public double getStudentGPA() { return studentGPA; }
    public void setStudentGPA(double gpa) { this.studentGPA = gpa; }

    public ArrayList<Course> getStudentCurrentCourses() { return studentCurrentCourses; }
    public void setStudentCurrentCourses(ArrayList<Course> list) { this.studentCurrentCourses = list; }

    public ArrayList<Course> getStudentCourseFinished() { return studentCourseFinished; }
    public void setStudentCourseFinished(ArrayList<Course> list) { this.studentCourseFinished = list; }

    public ArrayList<Double> getStudentFinalGrades() { return studentFinalGrades; }

    public ArrayList<ArrayList<Double>> getCurrentCourseGrades() {
        return CurrentCourseGrades;
    }

    public void setCurrentCourseGrades(ArrayList<ArrayList<Double>> currentCourseGrades) {
        CurrentCourseGrades = currentCourseGrades;
    }

    public int getStudentNrOfCurrentCourses() {
        return studentNrOfCurrentCourses;
    }

    public ArrayList<ArrayList<Integer>> getCurrentTheoryHours() {
        return CurrentTheoryHours;
    }

    public ArrayList<ArrayList<Integer>> getCurrentlabHours() {
        return CurrentlabHours;
    }

    public double getAMTpayed() {return AMTpayed;}
    public void setAMTpayed(double amtpayed) { this.AMTpayed = amtpayed; }
}