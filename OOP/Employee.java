import java.io.Serializable;

public abstract class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private int empId;
    private String name;
    private int age;

    public Employee(int empId, String name, int age) {
        this.empId = empId;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return empId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setId(int empId) {
        this.empId = empId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public abstract String getType();

    public abstract double calculateSalary();
}
