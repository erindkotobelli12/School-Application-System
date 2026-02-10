package Course;
import userTypes.Student;

import java.io.Serializable;
import java.util.ArrayList;

public class Degree implements Serializable {

    private String degreeId;
    private String degreeName;
    private String degreeLevel;
    private int durationYears;
    private int totalCreditsRequired;
    private Department degreeDepartment;
    private ArrayList<Student> degreeStudents = new ArrayList<>();
    private ArrayList<Course> requiredCourses = new ArrayList<>();
    private ArrayList<Course> electiveCourses = new ArrayList<>();
    private double degreeCost;

    public Degree(String degreeId, String degreeName, String degreeLevel, int durationYears, int totalECTSRequired,Department degreeDepartment, double degreeCost) {
        this.degreeId = degreeId;
        this.degreeName = degreeName;
        this.degreeLevel = degreeLevel;
        this.durationYears = durationYears;
        this.totalCreditsRequired = totalECTSRequired;
        this.degreeDepartment=degreeDepartment;
        this.degreeCost=degreeCost;
    }

    public String getDegreeId() {
        return degreeId;
    }

    public void setDegreeId(String degreeId) {
        this.degreeId = degreeId;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public int getDurationYears() {
        return durationYears;
    }

    public void setDurationYears(int durationYears) {
        this.durationYears = durationYears;
    }

    public int getTotalCreditsRequired() {
        return totalCreditsRequired;
    }

    public void setTotalCreditsRequired(int totalCreditsRequired) {
        this.totalCreditsRequired = totalCreditsRequired;
    }

    public double getDegreeCost() {return degreeCost;}

    public void setDegreeCost(double degreeCost) {this.degreeCost = degreeCost;}

    public ArrayList<Course> getRequiredCourses() {
        return requiredCourses;
    }

    public void setRequiredCourses(ArrayList<Course> requiredCourses) {
        this.requiredCourses = requiredCourses;
    }

    public ArrayList<Course> getElectiveCourses() {
        return electiveCourses;
    }

    public void setElectiveCourses(ArrayList<Course> electiveCourses) {
        this.electiveCourses = electiveCourses;
    }

    public void addStudentDegree(Student student) {
        degreeStudents.add(student);
    }
    public void removeStudentDegree(Student student) {
        degreeStudents.remove(student);
    }

    public Department getDegreeDepartment() {
        return degreeDepartment;
    }

    public void setDegreeDepartment(Department degreeDepartment) {
        this.degreeDepartment = degreeDepartment;
    }

    public ArrayList<Student> getDegreeStudents() {
        return degreeStudents;
    }
    public void addCourseRequired(Course course) {
        requiredCourses.add(course);
    }
    public void addCourseElective(Course course) {
        electiveCourses.add(course);
    }
    public void removeCourseRequired(Course course) {
        requiredCourses.remove(course);
    }
    public void removeCourseElective(Course course) {
        electiveCourses.remove(course);
    }

    public double getCost() {
        return degreeCost;
    }
    public void setCost(double degreeCost) {
        this.degreeCost = degreeCost;
    }

}
