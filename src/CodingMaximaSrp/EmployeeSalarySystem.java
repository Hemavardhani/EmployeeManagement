package CodingMaximaSrp;

   import com.opencsv.exceptions.CsvValidationException;
   import java.time.LocalDate;
   import java.util.List;
   import java.util.Map;
   import java.io.IOException;
   import java.nio.file.Files;
   import java.nio.file.Paths;

   public class EmployeeSalarySystem {
       public static void main(String[] args) {
           try {
               // Load employee data from CSV files
               List<Employee> fullTimeEmployees = EmployeeCsvLoader.loadFullTimeEmployees(loadFileData("full_time_employees.csv"));
               List<Employee> contractEmployees = EmployeeCsvLoader.loadContractEmployees(loadFileData("contract_employees.csv"));
               List<Employee> dailyWageEmployees = EmployeeCsvLoader.loadDailyWageEmployees(loadFileData("daily_wage_employees.csv"));

               // Create salary system and add employees
               SalarySystem system = new SalarySystem();
               system.addEmployees(fullTimeEmployees);
               system.addEmployees(contractEmployees);
               system.addEmployees(dailyWageEmployees);

               // Example operations
               // 1. Apply bulk salary adjustment for full-time employees
               system.bulkSalaryAdjustment(FullTimeEmployee.class, 5000, 1000, 500);
               System.out.println("Applied bulk salary adjustment for full-time employees");

               // 2. Filter employees by salary range
               System.out.println("\nEmployees with salary between $30,000 and $50,000:");
               List<Employee> filteredBySalary = system.filterEmployeesBySalary(30000, 50000);
               filteredBySalary.forEach(System.out::println);

               // 3. Filter employees by type (Contract)
               System.out.println("\nContract Employees:");
               List<Employee> contractEmps = system.filterEmployeesByType(ContractEmployee.class);
               contractEmps.forEach(System.out::println);

               // 4. Filter employees by hire date
               System.out.println("\nEmployees hired in 2023:");
               List<Employee> hiredIn2023 = system.filterEmployeesByHireDate(
                   LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));
               hiredIn2023.forEach(System.out::println);

               // 5. Sort employees by salary (descending)
               System.out.println("\nEmployees sorted by salary (descending):");
               List<Employee> sortedBySalary = system.sortEmployeesBySalary(false);
               sortedBySalary.forEach(System.out::println);

               // 6. Generate and print payroll report
               System.out.println("\nPayroll Report:");
               Map<String, Object> report = system.generatePayrollReport();
               System.out.println("Total Employees: " + report.get("employeeCount"));
               System.out.println("Full-Time Employees: " + report.get("fullTimeCount"));
               System.out.println("Contract Employees: " + report.get("contractCount"));
               System.out.println("Daily Wage Employees: " + report.get("dailyWageCount"));
               System.out.println("Total Salary: $" + String.format("%.2f", report.get("totalSalary")));
               System.out.println("Average Salary: $" + String.format("%.2f", report.get("averageSalary")));

               // 7. Export payroll to CSV
               system.exportPayrollToCsv("payroll_report.csv");
               System.out.println("\nPayroll exported to payroll_report.csv");

           } catch (CsvValidationException | IOException e) {
               System.err.println("Error loading CSV data: " + e.getMessage());
           }
       }

       private static String loadFileData(String filename) {
           try {
               String path = "C:\\\\Users\\\\hemav\\\\eclipse-workspace\\\\CodingMaximaSrpOcpExample\\\\" + filename;
               return Files.readString(Paths.get(path));
           } catch (IOException e) {
               System.err.println("Error reading file " + filename + ": " + e.getMessage());
               return "";
           }
       }
   }