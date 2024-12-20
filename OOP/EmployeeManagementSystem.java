import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeManagementSystem extends JFrame {
    private boolean dataSaved = false;  // Flag to track if data has been saved
    private ArrayList<Employee> employees;
    private JTextArea displayArea;
    private int nextId = 1;

    // Constructor
    public EmployeeManagementSystem() {
        employees = new ArrayList<>();
        loadFromFile();
        setupGUI();

    }

    public Map<Integer, Employee> getEmployeeMap() {
        Map<Integer, Employee> employeeMap = new HashMap<>();
        for (Employee employee : employees) {
            employeeMap.put(employee.getId(), employee);
        }
        return employeeMap;
    }

    // Method to set up the GUI
    private void setupGUI() {
        setTitle("Employee Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display area for showing details
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2));

        // Add buttons with respective action listeners
        addButton(buttonPanel, "Add Employee", e -> addEmployee());
        addButton(buttonPanel, "Edit Employee", e -> editEmployee());
        addButton(buttonPanel, "Delete Employee", e -> deleteEmployee());
        addButton(buttonPanel, "Display Employees", e -> displayEmployees());
        addButton(buttonPanel, "Payroll", e -> processPayroll());
        addButton(buttonPanel, "Sort Employees", e -> sortEmployees());
        addButton(buttonPanel, "Save to File", e -> saveToFile());
        addButton(buttonPanel, "Exit", e -> exitSystem());

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Helper method to create and add buttons
    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }

    private void editEmployee() {
        String employeeId = JOptionPane.showInputDialog(this, "Enter Employee ID to edit:");
        try {
            int id = Integer.parseInt(employeeId); // Convert employeeId to int
            for (Employee employee : employees) {
                if (employee.getId() == id) { // Compare int with int
                    // Common fields for all employee types
                    String newName = JOptionPane.showInputDialog(this, "Enter new name:", employee.getName());
                    if (newName != null && !newName.trim().isEmpty()) {
                        employee.setName(newName);
                    }

                    String newAge = JOptionPane.showInputDialog(this, "Enter new age:", employee.getAge());
                    if (newAge != null && !newAge.trim().isEmpty()) {
                        try {
                            employee.setAge(Integer.parseInt(newAge));
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(this, "Invalid age format. Age not updated.");
                        }
                    }

                    // Additional fields based on employee type
                    if (employee instanceof FullTime) {
                        FullTime fullTimeEmployee = (FullTime) employee;

                        String newDailyRate = JOptionPane.showInputDialog(this, "Enter new daily rate:", fullTimeEmployee.getDailyRate());
                        if (newDailyRate != null && !newDailyRate.trim().isEmpty()) {
                            try {
                                fullTimeEmployee.setDailyRate(Double.parseDouble(newDailyRate));
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this, "Invalid daily rate format. Daily rate not updated.");
                            }
                        }

                        String newPosition = JOptionPane.showInputDialog(this, "Enter new position:", fullTimeEmployee.getPosition());
                        if (newPosition != null && !newPosition.trim().isEmpty()) {
                            fullTimeEmployee.setPosition(newPosition);
                        }
                    } else if (employee instanceof PartTime) {
                        PartTime partTimeEmployee = (PartTime) employee;

                        String newHourlyRate = JOptionPane.showInputDialog(this, "Enter new hourly rate:", partTimeEmployee.getHourlyRate());
                        if (newHourlyRate != null && !newHourlyRate.trim().isEmpty()) {
                            try {
                                partTimeEmployee.setHourlyRate(Double.parseDouble(newHourlyRate));
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this, "Invalid hourly rate format. Hourly rate not updated.");
                            }
                        }
                    } else if (employee instanceof Contract) {
                        Contract contractEmployee = (Contract) employee;

                        String newMonthlyRate = JOptionPane.showInputDialog(this, "Enter new monthly rate:", contractEmployee.getMonthlyRate());
                        if (newMonthlyRate != null && !newMonthlyRate.trim().isEmpty()) {
                            try {
                                contractEmployee.setMonthlyRate(Double.parseDouble(newMonthlyRate));
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(this, "Invalid monthly rate format. Monthly rate not updated.");
                            }
                        }

                        String newAgencyName = JOptionPane.showInputDialog(this, "Enter new agency name:", contractEmployee.getAgencyName());
                        if (newAgencyName != null && !newAgencyName.trim().isEmpty()) {
                            contractEmployee.setAgencyName(newAgencyName);
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Employee details updated successfully.");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Employee not found.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.");
        }
    }


    private void deleteEmployee() {
        String employeeId = JOptionPane.showInputDialog(this, "Enter Employee ID to delete:");
        try {
            int id = Integer.parseInt(employeeId); // Convert employeeId to int
            for (Employee employee : employees) {
                if (employee.getId() == id) { // Compare int with int
                    employees.remove(employee);
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Employee not found.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.");
        }
    }

    private void displayEmployees() {
        displayArea.setText("Employees:\n");
        for (Employee employee : employees) {
            displayArea.append("ID: " + employee.getId() + ", Name: " + employee.getName() + ", Daily Rate: " +
                    (employee instanceof FullTime ? ((FullTime) employee).getDailyRate() : "N/A") +
                    ", Hourly Rate: " +
                    (employee instanceof PartTime ? ((PartTime) employee).getHourlyRate() : "N/A") +
                    ", Monthly Rate: " +
                    (employee instanceof Contract ? ((Contract) employee).getMonthlyRate() : "N/A") +
                    "\n");
        }
    }

    private void sortEmployees() {
        // Create grouped lists for each contract type
        ArrayList<FullTime> fullTimeEmployees = new ArrayList<>();
        ArrayList<PartTime> partTimeEmployees = new ArrayList<>();
        ArrayList<Contract> contractEmployees = new ArrayList<>();

        // Separate employees by type
        for (Employee employee : employees) {
            if (employee instanceof FullTime) {
                fullTimeEmployees.add((FullTime) employee);
            } else if (employee instanceof PartTime) {
                partTimeEmployees.add((PartTime) employee);
            } else if (employee instanceof Contract) {
                contractEmployees.add((Contract) employee);
            }
        }

        // Sort each group by their respective rate
        fullTimeEmployees.sort((e1, e2) -> Double.compare(e1.getDailyRate(), e2.getDailyRate()));
        partTimeEmployees.sort((e1, e2) -> Double.compare(e1.getHourlyRate(), e2.getHourlyRate()));
        contractEmployees.sort((e1, e2) -> Double.compare(e1.getMonthlyRate(), e2.getMonthlyRate()));

        // Display the sorted employees grouped by contract type
        displayArea.setText("Employees (Sorted by Rate, Grouped by Type):\n");

        displayArea.append("\nFull-Time Employees (Daily Rate):\n");
        for (FullTime employee : fullTimeEmployees) {
            displayArea.append("ID: " + employee.getId() + ", Name: " + employee.getName() + ", Daily Rate: " + employee.getDailyRate() + "\n");
        }

        displayArea.append("\nPart-Time Employees (Hourly Rate):\n");
        for (PartTime employee : partTimeEmployees) {
            displayArea.append("ID: " + employee.getId() + ", Name: " + employee.getName() + ", Hourly Rate: " + employee.getHourlyRate() + "\n");
        }

        displayArea.append("\nContract Employees (Monthly Rate):\n");
        for (Contract employee : contractEmployees) {
            displayArea.append("ID: " + employee.getId() + ", Name: " + employee.getName() + ", Monthly Rate: " + employee.getMonthlyRate() + "\n");
        }

        JOptionPane.showMessageDialog(this, "Employees sorted and grouped by type and rate.");
    }

    private String serializeEmployee(Employee employee) {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(employee.getId())
                .append(" | Name: ").append(employee.getName())
                .append(" | Age: ").append(employee.getAge())
                .append(" | Employee Type: ").append(employee.getType()) // Add employee type
                .append(" | ");

        if (employee instanceof FullTime) {
            FullTime fullTime = (FullTime) employee;
            builder.append("Daily Rate: ").append(fullTime.getDailyRate())
                    .append(" | Position: ").append(fullTime.getPosition());
        } else if (employee instanceof PartTime) {
            PartTime partTime = (PartTime) employee;
            builder.append("Hourly Rate: ").append(partTime.getHourlyRate());
        } else if (employee instanceof Contract) {
            Contract contract = (Contract) employee;
            builder.append("Monthly Rate: ").append(contract.getMonthlyRate())
                    .append(" | Agency Name: ").append(contract.getAgencyName());
        }
        return builder.toString();
    }

    private void loadFromFile() {
        File file = new File("employee.txt");
        int maxId = 0; // Track the highest ID in the file
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                employees.clear(); // Clear the list before loading
                String line;
                while ((line = reader.readLine()) != null) {
                    Employee employee = deserializeEmployee(line);
                    if (employee != null) {
                        employees.add(employee);
                        maxId = Math.max(maxId, employee.getId()); // Update max ID
                    }
                }
                nextId = maxId + 1; // Set the next ID
                JOptionPane.showMessageDialog(this, "Employees loaded from employee.txt.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading employees from file.");
            }
        }
    }


    private Employee deserializeEmployee(String line) {
        String[] parts = line.split(" \\| ");
        int id = Integer.parseInt(parts[0].split(": ")[1]);
        String name = parts[1].split(": ")[1];
        int age = Integer.parseInt(parts[2].split(": ")[1]);
        String employeeType = parts[3].split(": ")[1]; // Read employee type

        if (employeeType.equals("FullTime")) { // Check employee type
            double dailyRate = Double.parseDouble(parts[4].split(": ")[1]);
            String position = parts[5].split(": ")[1];
            return new FullTime(id, name, age, dailyRate, position);
        } else if (employeeType.equals("PartTime")) { // Check employee type
            double hourlyRate = Double.parseDouble(parts[4].split(": ")[1]);
            return new PartTime(id, name, age, hourlyRate);
        } else if (employeeType.equals("Contract")) { // Check employee type
            double monthlyRate = Double.parseDouble(parts[4].split(": ")[1]);
            String agencyName = parts[5].split(": ")[1];
            return new Contract(id, name, age, monthlyRate, agencyName);
        }
        return null; // Invalid format
    }

    // Method to add a new employee
    private void addEmployee() {
        String[] options = {"Full-Time", "Part-Time", "Contract"};
        String employeeType = (String) JOptionPane.showInputDialog(this, "Select employee type:",
                "Add Employee", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (employeeType == null) return; // Cancel was pressed

        String name;
        do {
            name = JOptionPane.showInputDialog(this, "Enter name:");
            if (name == null) return; // Cancel was pressed
            if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty. Please enter a valid name.");
            }
        } while (name.trim().isEmpty());

        int age;
        while (true) {
            String ageStr = JOptionPane.showInputDialog(this, "Enter age:");
            if (ageStr == null) return; // Cancel was pressed
            try {
                age = Integer.parseInt(ageStr);
                if (age <= 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid age. Please enter a positive number.");
            }
        }

        if (employeeType.equals("Full-Time")) {
            double dailyRate;
            while (true) {
                String dailyRateStr = JOptionPane.showInputDialog(this, "Enter daily rate:");
                if (dailyRateStr == null) return; // Cancel was pressed
                try {
                    dailyRate = Double.parseDouble(dailyRateStr);
                    if (dailyRate <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid daily rate. Please enter a positive number.");
                }
            }

            String position;
            do {
                position = JOptionPane.showInputDialog(this, "Enter position:");
                if (position == null) return; // Cancel was pressed
                if (position.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Position cannot be empty. Please enter a valid position.");
                }
            } while (position.trim().isEmpty());

            employees.add(new FullTime(nextId++, name, age, dailyRate, position));

        } else if (employeeType.equals("Part-Time")) {
            double hourlyRate;
            while (true) {
                String hourlyRateStr = JOptionPane.showInputDialog(this, "Enter hourly rate:");
                if (hourlyRateStr == null) return; // Cancel was pressed
                try {
                    hourlyRate = Double.parseDouble(hourlyRateStr);
                    if (hourlyRate <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid hourly rate. Please enter a positive number.");
                }
            }

            employees.add(new PartTime(nextId++, name, age, hourlyRate));

        } else if (employeeType.equals("Contract")) {
            double monthlyRate;
            while (true) {
                String monthlyRateStr = JOptionPane.showInputDialog(this, "Enter monthly rate:");
                if (monthlyRateStr == null) return; // Cancel was pressed
                try {
                    monthlyRate = Double.parseDouble(monthlyRateStr);
                    if (monthlyRate <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid monthly rate. Please enter a positive number.");
                }
            }

            String agencyName;
            do {
                agencyName = JOptionPane.showInputDialog(this, "Enter agency name:");
                if (agencyName == null) return; // Cancel was pressed
                if (agencyName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Agency name cannot be empty. Please enter a valid agency name.");
                }
            } while (agencyName.trim().isEmpty());

            employees.add(new Contract(nextId++, name, age, monthlyRate, agencyName));
        }

        JOptionPane.showMessageDialog(this, "Employee added successfully.");
        dataSaved = false; // Set the dataSaved flag to false
    }



    // Payroll functionality
    private void processPayroll() {
        SwingUtilities.invokeLater(() -> {
            PayrollGUI payrollGUI = new PayrollGUI(this);
            payrollGUI.setVisible(true);
        });
    }


   /* public Employee findEmployeeById(int id) {
        for (Employee employee : employees) {
            if (employee.getId() == id) return employee;
        }
        return null;
    }*/



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeManagementSystem system = new EmployeeManagementSystem();
            system.setVisible(true);
        });
    }

    private void exitSystem() {
        // Ask the user if they want to exit
        int confirm = JOptionPane.showConfirmDialog(this,
                "Do you want to exit?",
                "Exit Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // If data has not been saved, ask if the user wants to save it
            if (!dataSaved) {
                int saveConfirm = JOptionPane.showConfirmDialog(this,
                        "You haven't saved employee data. Do you want to save before exiting?",
                        "Save Confirmation", JOptionPane.YES_NO_OPTION);

                if (saveConfirm == JOptionPane.YES_OPTION) {
                    // Save employee data to file
                    saveToFile();
                    JOptionPane.showMessageDialog(this, "Employee data saved.");
                } else {
                    JOptionPane.showMessageDialog(this, "Employee data not saved.");
                }
            }

            // Exit the application
            System.exit(0);
        } else {
            JOptionPane.showMessageDialog(this, "Returning to the menu.");
        }
    }

    // Set dataSaved to true when employee data is saved
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("employee.txt"))) {
            for (Employee employee : employees) {
                writer.write(serializeEmployee(employee));
                writer.newLine();
            }
            dataSaved = true;  // Set dataSaved to true after saving
            JOptionPane.showMessageDialog(this, "Employees saved to employee.txt.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving employees to file.");
        }
    }
}