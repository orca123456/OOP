import java.io.Serializable;

public class PartTime extends Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private double hourlyRate;

    public PartTime(int empId, String name, int age, double hourlyRate) {
        super(empId, name, age);
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String getType() {
        return "PartTime";
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * 160; // Assume 160 hours per month
    }
}
