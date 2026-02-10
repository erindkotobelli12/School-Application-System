package Course;

import java.io.Serializable;
import java.util.ArrayList;

public class Department implements Serializable {

    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String departmentLocation;

    private ArrayList<Degree>departmentDegrees= new ArrayList<>();

    public Department(String departmentId, String departmentName, String departmentCode, String departmentLocation) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.departmentLocation = departmentLocation;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentLocation() {
        return departmentLocation;
    }

    public void setDepartmentLocation(String departmentLocation) {
        this.departmentLocation = departmentLocation;
    }

    public ArrayList<Degree> getDepartmentDegrees() {
        return departmentDegrees;
    }

    public void addDepartmentDegrees(Degree degree){
        departmentDegrees.add(degree);
    }
    public void removeDepartmentDegrees(Degree degree){
        departmentDegrees.remove(degree);
    }
}
