import java.io.Serializable;

public class FullTime extends Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private double dailyRate;
    private String position;

    public FullTime(int empId, String name, int age, double dailyRate, String position) {
        super(empId, name, age);
        this.dailyRate = dailyRate;
        this.position = position;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String getType() {
        return "FullTime";
    }

    @Override
    public double calculateSalary() {
        return dailyRate * 30; // Assume 30 working days in a month
    }
}
