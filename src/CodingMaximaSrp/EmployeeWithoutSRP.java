package CodingMaximaSrp;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.time.LocalDate;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmployeeWithoutSRP {
	private static abstract class Employee {
        private final String empId;
        private final String name;
        private final LocalDate hireDate;
        private double basicPay;
        private double hra;
        private double bonus;

        public Employee(String empId, String name, LocalDate hireDate) {
            this.empId = empId;
            this.name = name;
            this.hireDate = hireDate;
            this.basicPay = 0.0;
            this.hra = 0.0;
            this.bonus = 0.0;
        }

        public abstract double calculateSalary();

        public String getEmpId() { return empId; }
        public String getName() { return name; }
        public LocalDate getHireDate() { return hireDate; }
        public double getBasicPay() { return basicPay; }
        public double getHra() { return hra; }
        public double getBonus() { return bonus; }

        public void setBasicPay(double basicPay) {
            if (basicPay < 0) throw new IllegalArgumentException("Basic pay cannot be negative");
            this.basicPay = basicPay;
        }

        public void setHra(double hra) {
            if (hra < 0) throw new IllegalArgumentException("HRA cannot be negative");
            this.hra = hra;
        }

        public void setBonus(double bonus) {
            if (bonus < 0) throw new IllegalArgumentException("Bonus cannot be negative");
            this.bonus = bonus;
        }

        public void adjustSalary(double basicChange, double hraChange, double bonusChange) {
            setBasicPay(this.basicPay + basicChange);
            setHra(this.hra + hraChange);
            setBonus(this.bonus + bonusChange);
        }

        @Override
        public String toString() {
            return "Employee: " + name + ", ID: " + empId + ", Type: " + this.getClass().getSimpleName() + 
                   ", Total Salary: " + String.format("%.2f", calculateSalary());
        }
    }

    // FullTimeEmployee class
    private static class FullTimeEmployee extends Employee {
        private double pfDeduction;

        public FullTimeEmployee(String empId, String name, LocalDate hireDate, double basicPay, double hraPercentage, double annualBonus) {
            super(empId, name, hireDate);
            setBasicPay(basicPay);
            setHra(basicPay * hraPercentage);
            setBonus(basicPay * annualBonus);
            this.pfDeduction = basicPay * 0.12;
        }

        @Override
        public double calculateSalary() {
            double monthlySalary = getBasicPay() + getHra() + (getBonus() / 12) - pfDeduction;
            return Math.round(monthlySalary * 100.0) / 100.0;
        }

        public void applyAnnualIncrement(double incrementPercentage) {
            double increment = getBasicPay() * (incrementPercentage / 100);
            adjustSalary(increment, increment * 0.3, 0);
        }
    }

    // ContractEmployee class
    private static class ContractEmployee extends Employee {
        private int contractDurationMonths;

        public ContractEmployee(String empId, String name, LocalDate hireDate, double contractRate, int contractDurationMonths) {
            super(empId, name, hireDate);
            setBasicPay(contractRate);
            setHra(contractRate * 0.2); // 20% HRA
            setBonus(0); // No bonus
            this.contractDurationMonths = contractDurationMonths;
        }

        @Override
        public double calculateSalary() {
            double monthlySalary = getBasicPay() + getHra();
            return Math.round(monthlySalary * 100.0) / 100.0;
        }

        public String extendContract(int additionalMonths) {
            this.contractDurationMonths += additionalMonths;
            return "Contract extended by " + additionalMonths + " months";
        }
    }

    // DailyWageEmployee class
    private static class DailyWageEmployee extends Employee {
        private double dailyRate;
        private int workingDays;

        public DailyWageEmployee(String empId, String name, LocalDate hireDate, double dailyRate, int workingDays) {
            super(empId, name, hireDate);
            this.dailyRate = dailyRate;
            this.workingDays = workingDays;
            setBasicPay(dailyRate * workingDays);
            setHra(0); // No HRA
            setBonus(dailyRate * workingDays * 0.05); // 5% bonus
        }

        @Override
        public double calculateSalary() {
            setBasicPay(dailyRate * workingDays);
            double monthlySalary = getBasicPay() + getBonus();
            return Math.round(monthlySalary * 100.0) / 100.0;
        }

        public void updateWorkingDays(int days) {
            if (days < 0) throw new IllegalArgumentException("Working days cannot be negative");
            this.workingDays = days;
            setBonus(dailyRate * days * 0.05);
        }
    }

    // Employee management and operations
    private static Map<String, Employee> employees = new HashMap<>();

    // Load CSV data
    private static void loadFullTimeEmployees(String csvData) throws CsvValidationException, IOException {
        try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
            reader.readNext(); // Skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 7) continue;
                try {
                    String empId = line[0].trim();
                    String name = line[1].trim();
                    LocalDate hireDate = LocalDate.parse(line[2].trim());
                    double basicPay = Double.parseDouble(line[3].trim());
                    double hraPercentage = Double.parseDouble(line[4].trim()) / basicPay;
                    double annualBonus = Double.parseDouble(line[5].trim()) / basicPay;
                    employees.put(empId, new FullTimeEmployee(empId, name, hireDate, basicPay, hraPercentage, annualBonus));
                } catch (Exception e) {
                    System.err.println("Error parsing full-time employee: " + e.getMessage());
                }
            }
        }
    }

    private static void loadContractEmployees(String csvData) throws CsvValidationException, IOException {
        try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
            reader.readNext(); // Skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 6) continue;
                try {
                    String empId = line[0].trim();
                    String name = line[1].trim();
                    LocalDate hireDate = LocalDate.parse(line[2].trim());
                    double contractRate = Double.parseDouble(line[3].trim());
                    int contractDuration = Integer.parseInt(line[5].trim());
                    employees.put(empId, new ContractEmployee(empId, name, hireDate, contractRate, contractDuration));
                } catch (Exception e) {
                    System.err.println("Error parsing contract employee: " + e.getMessage());
                }
            }
        }
    }

    private static void loadDailyWageEmployees(String csvData) throws CsvValidationException, IOException {
        try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
            reader.readNext(); // Skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length < 7) continue;
                try {
                    String empId = line[0].trim();
                    String name = line[1].trim();
                    LocalDate hireDate = LocalDate.parse(line[2].trim());
                    double dailyRate = Double.parseDouble(line[3].trim());
                    int workingDays = Integer.parseInt(line[4].trim());
                    employees.put(empId, new DailyWageEmployee(empId, name, hireDate, dailyRate, workingDays));
                } catch (Exception e) {
                    System.err.println("Error parsing daily wage employee: " + e.getMessage());
                }
            }
        }
    }

    // File reading
    private static String loadFileData(String filename) {
        try {
            String path = "C:\\Users\\hemav\\eclipse-workspace\\CodingMaximaSolidPriciplesExample\\src\\CodingMaximaSrpOcp\\" + filename;
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            return "";
        }
    }

    // Employee operations
    private static String removeEmployee(String empId) {
        if (employees.containsKey(empId)) {
            Employee emp = employees.remove(empId);
            return "Employee " + emp.getName() + " removed";
        }
        return "Employee " + empId + " not found";
    }

    private static Double getEmployeeSalary(String empId) {
        Employee employee = employees.get(empId);
        return employee != null ? employee.calculateSalary() : null;
    }

    private static String applyIncrement(String empId, double incrementPercentage) {
        Employee employee = employees.get(empId);
        if (employee instanceof FullTimeEmployee) {
            ((FullTimeEmployee) employee).applyAnnualIncrement(incrementPercentage);
            return "Increment applied to " + employee.getName();
        }
        return "Increment only applicable to full-time employees";
    }

    private static void bulkSalaryAdjustment(Class<? extends Employee> employeeType, double basicChange, double hraChange, double bonusChange) {
        for (Employee emp : employees.values()) {
            if (employeeType.isInstance(emp)) {
                emp.adjustSalary(basicChange, hraChange, bonusChange);
            }
        }
    }

    private static List<Employee> filterEmployeesBySalary(double minSalary, double maxSalary) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employees.values()) {
            double salary = emp.calculateSalary();
            if (salary >= minSalary && salary <= maxSalary) {
                filtered.add(emp);
            }
        }
        return filtered;
    }

    private static List<Employee> filterEmployeesByType(Class<? extends Employee> employeeType) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employees.values()) {
            if (employeeType.isInstance(emp)) {
                filtered.add(emp);
            }
        }
        return filtered;
    }

    private static List<Employee> filterEmployeesByHireDate(LocalDate startDate, LocalDate endDate) {
        List<Employee> filtered = new ArrayList<>();
        for (Employee emp : employees.values()) {
            LocalDate hireDate = emp.getHireDate();
            if (!hireDate.isBefore(startDate) && !hireDate.isAfter(endDate)) {
                filtered.add(emp);
            }
        }
        return filtered;
    }

    private static List<Employee> sortEmployeesBySalary(boolean ascending) {
        List<Employee> sorted = new ArrayList<>(employees.values());
        sorted.sort((e1, e2) -> {
            double diff = e1.calculateSalary() - e2.calculateSalary();
            return ascending ? (int) (diff * 100) : (int) (-diff * 100);
        });
        return sorted;
    }

    private static List<Employee> sortEmployeesByName() {
        List<Employee> sorted = new ArrayList<>(employees.values());
        sorted.sort(Comparator.comparing(Employee::getName));
        return sorted;
    }

    private static Map<String, Object> generatePayrollReport() {
        Map<String, Object> report = new HashMap<>();
        List<Map<String, Object>> payroll = new ArrayList<>();
        double totalSalary = 0.0;
        int fullTimeCount = 0, contractCount = 0, dailyWageCount = 0;

        for (Employee emp : employees.values()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", emp.getEmpId());
            entry.put("name", emp.getName());
            entry.put("salary", emp.calculateSalary());
            entry.put("type", emp.getClass().getSimpleName());
            entry.put("hireDate", emp.getHireDate().toString());
            payroll.add(entry);
            totalSalary += emp.calculateSalary();
            if (emp instanceof FullTimeEmployee) fullTimeCount++;
            else if (emp instanceof ContractEmployee) contractCount++;
            else if (emp instanceof DailyWageEmployee) dailyWageCount++;
        }

        report.put("payroll", payroll);
        report.put("totalSalary", Math.round(totalSalary * 100.0) / 100.0);
        report.put("employeeCount", employees.size());
        report.put("fullTimeCount", fullTimeCount);
        report.put("contractCount", contractCount);
        report.put("dailyWageCount", dailyWageCount);
        report.put("averageSalary", employees.isEmpty() ? 0 : Math.round((totalSalary / employees.size()) * 100.0) / 100.0);

        return report;
    }

    private static void exportPayrollToCsv(String filename) {
        Map<String, Object> report = generatePayrollReport();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> payroll = (List<Map<String, Object>>) report.get("payroll");

        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("ID,Name,Type,Salary,Hire Date\n");
            for (Map<String, Object> entry : payroll) {
                writer.append(String.format("%s,%s,%s,%.2f,%s\n",
                    entry.get("id"), entry.get("name"), entry.get("type"),
                    (Double) entry.get("salary"), entry.get("hireDate")));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {
        try {
            // Load employee data from CSV files
            loadFullTimeEmployees(loadFileData("full_time_employees.csv"));
            loadContractEmployees(loadFileData("contract_employees.csv"));
            loadDailyWageEmployees(loadFileData("daily_wage_employees.csv"));

            // Example operations
            // 1. Apply bulk salary adjustment for full-time employees
            bulkSalaryAdjustment(FullTimeEmployee.class, 5000, 1000, 500);
            System.out.println("Applied bulk salary adjustment for full-time employees");

            // 2. Filter employees by salary range
            System.out.println("\nEmployees with salary between $30,000 and $50,000:");
            List<Employee> filteredBySalary = filterEmployeesBySalary(30000, 50000);
            filteredBySalary.forEach(System.out::println);

            // 3. Filter employees by type (Contract)
            System.out.println("\nContract Employees:");
            List<Employee> contractEmps = filterEmployeesByType(ContractEmployee.class);
            contractEmps.forEach(System.out::println);

            // 4. Filter employees by hire date
            System.out.println("\nEmployees hired in 2023:");
            List<Employee> hiredIn2023 = filterEmployeesByHireDate(
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
            hiredIn2023.forEach(System.out::println);

            // 5. Sort employees by salary (descending)
            System.out.println("\nEmployees sorted by salary (descending):");
            List<Employee> sortedBySalary = sortEmployeesBySalary(false);
            sortedBySalary.forEach(System.out::println);

            // 6. Generate and print payroll report
            System.out.println("\nPayroll Report:");
            Map<String, Object> report = generatePayrollReport();
            System.out.println("Total Employees: " + report.get("employeeCount"));
            System.out.println("Full-Time Employees: " + report.get("fullTimeCount"));
            System.out.println("Contract Employees: " + report.get("contractCount"));
            System.out.println("Daily Wage Employees: " + report.get("dailyWageCount"));
            System.out.println("Total Salary: $" + String.format("%.2f", report.get("totalSalary")));
            System.out.println("Average Salary: $" + String.format("%.2f", report.get("averageSalary")));

            // 7. Export payroll to CSV
            exportPayrollToCsv("payroll_report.csv");
            System.out.println("\nPayroll exported to payroll_report.csv");

        } catch (CsvValidationException | IOException e) {
            System.err.println("Error loading CSV data: " + e.getMessage());
        }
    }

}
