import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PayrollGUI extends JFrame {
    private JTextField empIdField;
    private JTextArea outputArea;
    private JButton calculateButton;
    private JButton saveToFileButton;
    private JButton showHistoryButton;
    private EmployeeManagementSystem employeeManagementSystem;

    public PayrollGUI(EmployeeManagementSystem employeeManagementSystem) {
        this.employeeManagementSystem = employeeManagementSystem;

        setTitle("Payroll Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Input field for Employee ID
        add(new JLabel("Enter Employee ID:"));
        empIdField = new JTextField(10);
        add(empIdField);

        // Output area
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea));

        // Calculate Button
        calculateButton = new JButton("Calculate Salary");
        add(calculateButton);

        // Save to File Button
        saveToFileButton = new JButton("Save to File");
        add(saveToFileButton);

        // Show Payroll History Button
        showHistoryButton = new JButton("Show Payroll History");
        add(showHistoryButton);

        // Action listener for Calculate button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateSalary();
            }
        });

        // Action listener for Save to File button
        saveToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });

        // Action listener for Show Payroll History button
        showHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPayrollHistory();
            }
        });

        setVisible(true);
    }

    private void calculateSalary() {
        String empIdText = empIdField.getText();
        if (empIdText.isEmpty()) {
            outputArea.setText("Please enter an Employee ID.");
            return;
        }

        int empId;
        try {
            empId = Integer.parseInt(empIdText);
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid Employee ID. Please enter a numeric value.");
            return;
        }

        Map<Integer, Employee> employeeMap = employeeManagementSystem.getEmployeeMap();
        Employee employee = employeeMap.get(empId);

        if (employee == null) {
            outputArea.setText("Employee not found.");
            return;
        }

        StringBuilder output = new StringBuilder();
        output.append("Employee ID: ").append(employee.getId()).append("\n");
        output.append("Name: ").append(employee.getName()).append("\n");
        output.append("Age: ").append(employee.getAge()).append("\n");
        output.append("Type: ").append(employee.getType()).append("\n");

        double salary = 0.0;
        try {
            if (employee instanceof FullTime) {
                FullTime fullTimeEmployee = (FullTime) employee;
                String daysPresentStr = JOptionPane.showInputDialog("Enter Days Present:");
                if (daysPresentStr == null || daysPresentStr.trim().isEmpty()) {
                    outputArea.setText("Days Present is required.");
                    return;
                }

                if (!daysPresentStr.matches("\\d+")) {
                    outputArea.setText("Invalid Days Present. Please enter a numeric value.");
                    return;
                }

                String absencesStr = JOptionPane.showInputDialog("Enter Days Absent:");
                if (absencesStr == null || absencesStr.trim().isEmpty()) {
                    outputArea.setText("Days Absent is required.");
                    return;
                }

                if (!absencesStr.matches("\\d+")) {
                    outputArea.setText("Invalid Days Absent. Please enter a numeric value.");
                    return;
                }

                int daysPresent = Integer.parseInt(daysPresentStr);
                int absences = Integer.parseInt(absencesStr);

                salary = fullTimeEmployee.getDailyRate() * (daysPresent - absences);
                output.append("Days Present: ").append(daysPresent).append("\n");
                output.append("Absences: ").append(absences).append("\n");
                output.append("Salary Calculation: ").append(fullTimeEmployee.getDailyRate())
                        .append(" * (").append(daysPresent).append(" - ").append(absences).append(") = ").append(salary).append("\n");

            } else if (employee instanceof PartTime) {
                PartTime partTimeEmployee = (PartTime) employee;
                String hoursWorkedStr = JOptionPane.showInputDialog("Enter Hours Worked:");
                if (hoursWorkedStr == null || hoursWorkedStr.trim().isEmpty()) {
                    outputArea.setText("Hours Worked is required.");
                    return;
                }

                if (!hoursWorkedStr.matches("\\d+")) {
                    outputArea.setText("Invalid Hours Worked. Please enter a numeric value.");
                    return;
                }

                int hoursWorked = Integer.parseInt(hoursWorkedStr);
                salary = partTimeEmployee.getHourlyRate() * hoursWorked;
                output.append("Hours Worked: ").append(hoursWorked).append("\n");
                output.append("Salary Calculation: ").append(partTimeEmployee.getHourlyRate())
                        .append(" * ").append(hoursWorked).append(" = ").append(salary).append("\n");

            } else if (employee instanceof Contract) {
                Contract contractEmployee = (Contract) employee;
                String absencesStr = JOptionPane.showInputDialog("Enter Absence Days:");
                if (absencesStr == null || absencesStr.trim().isEmpty()) {
                    outputArea.setText("Absence Days are required.");
                    return;
                }

                if (!absencesStr.matches("\\d+")) {
                    outputArea.setText("Invalid Absence Days. Please enter a numeric value.");
                    return;
                }

                int absences = Integer.parseInt(absencesStr);
                double dailyRate = contractEmployee.getMonthlyRate() / 30.0; // Assuming 30 days in a month
                salary = contractEmployee.getMonthlyRate() - (dailyRate * absences);

                output.append("Absence Days: ").append(absences).append("\n");
                output.append("Salary Calculation: ").append(contractEmployee.getMonthlyRate())
                        .append(" - (").append(dailyRate).append(" * ").append(absences).append(") = ").append(salary).append("\n");
            }

            output.append("Total Salary: ").append(salary).append("\n");
            outputArea.setText(output.toString());

        } catch (NumberFormatException e) {
            outputArea.setText("Invalid input. Please enter numeric values where required.");
        }
    }




    private void saveToFile() {
        String payrollData = outputArea.getText();
        if (payrollData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Append the data to payroll.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("payroll.txt", true))) {
            writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"); // Add a divider
            writer.write("Date and Time: " + formattedDateTime + "\n"); // Add date and time
            writer.write(payrollData);
            writer.write("\n"); // Add a newline after each entry
            JOptionPane.showMessageDialog(this, "Payroll data saved to payroll.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPayrollHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("payroll.txt"))) {
            StringBuilder history = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                history.append(line).append("\n");
            }
            outputArea.setText(history.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading payroll history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EmployeeManagementSystem employeeManagementSystem = new EmployeeManagementSystem();
        SwingUtilities.invokeLater(() -> new PayrollGUI(employeeManagementSystem));
    }
}