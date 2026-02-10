package Course;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private String courseName;
    private int courseID;
    private Degree courseDegree;
    private int courseSemester;
    private int courseCredit;
    private int courseECTS;
    private String courseType;
    private int courseLabHours;
    private boolean courseHasLabHours;
    private int courseTheoHours;
    private ArrayList<Double>courseGradeDistribution = new ArrayList<>();
    private ArrayList<Classroom> classrooms= new ArrayList<>();



    public Course(String courseName, int courseID, Degree courseDegree, int courseSemester, int courseCredit, int courseECTS, String courseType, boolean courseHasLabHours , int courseLabHours, int courseTheoHours) {
        this.courseName = courseName;
        this.courseID = courseID;
        this.courseDegree = courseDegree;
        this.courseSemester = courseSemester;
        this.courseCredit = courseCredit;
        this.courseECTS = courseECTS;
        this.courseType = courseType;
        if(courseHasLabHours) {
            this.courseLabHours = courseLabHours;
        }
        this.courseHasLabHours = courseHasLabHours;
        this.courseTheoHours = courseTheoHours;
        courseGradeDistribution.add(0.4);
        courseGradeDistribution.add(0.6);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public Degree getCourseDegree() {
        return courseDegree;
    }

    public void setCourseDegree(Degree courseDegree) {
        this.courseDegree = courseDegree;
    }

    public int getCourseSemester() {
        return courseSemester;
    }

    public void setCourseSemester(int courseSemester) {
        this.courseSemester = courseSemester;
    }

    public int getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(int courseCredit) {
        this.courseCredit = courseCredit;
    }

    public int getCourseECTS() {
        return courseECTS;
    }

    public void setCourseECTS(int courseECTS) {
        this.courseECTS = courseECTS;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public boolean isCourseHasLabHours() {
        return courseHasLabHours;
    }

    public void setCourseHasLabHours(boolean courseHasLabHours) {
        this.courseHasLabHours = courseHasLabHours;
    }

    public int getCourseLabHours() {
        return courseLabHours;
    }

    public void setCourseLabHours(int courseLabHours) {
        this.courseLabHours = courseLabHours;
    }

    public int getCourseTheoHours() {
        return courseTheoHours;
    }

    public void setCourseTheoHours(int courseTheoHours) {
        this.courseTheoHours = courseTheoHours;
    }
    public ArrayList<Double> getCourseGradeDistribution() {
        return courseGradeDistribution;
    }

    public void setCourseGradeDistribution(ArrayList<Double> courseGradeDistribution) {
        this.courseGradeDistribution = courseGradeDistribution;
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }
    public void setClassrooms(ArrayList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }
    public void addClassroom(Classroom classroom){
        classrooms.add(classroom);
    }
    public void removeClassroom(Classroom classroom){
        classrooms.remove(classroom);
    }
}
