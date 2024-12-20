import java.io.Serializable;

public class Contract extends Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private double monthlyRate;
    private String agencyName;

    public Contract(int empId, String name, int age, double monthlyRate, String agencyName) {
        super(empId, name, age);
        this.monthlyRate = monthlyRate;
        this.agencyName = agencyName;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    @Override
    public String getType() {
        return "Contract";
    }

    @Override
    public double calculateSalary() {
        return monthlyRate;
    }
}
