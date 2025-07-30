package CodingMaximaSrp;

   import com.opencsv.CSVReader;
   import com.opencsv.exceptions.CsvValidationException;
   import java.io.StringReader;
   import java.io.IOException;
   import java.time.LocalDate;
   import java.util.ArrayList;
   import java.util.List;

   public class EmployeeCsvLoader {
       public static List<Employee> loadFullTimeEmployees(String csvData) throws CsvValidationException, IOException {
           List<Employee> employees = new ArrayList<>();
           try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
               reader.readNext(); // Skip header
               String[] line;
               while ((line = reader.readNext()) != null) {
                   if (line.length < 7) continue; // Skip invalid rows
                   try {
                       String empId = line[0].trim();
                       String name = line[1].trim();
                       LocalDate hireDate = LocalDate.parse(line[2].trim());
                       double basicPay = Double.parseDouble(line[3].trim());
                       double hraPercentage = Double.parseDouble(line[4].trim()) / basicPay; // Convert hra to percentage
                       double annualBonus = Double.parseDouble(line[5].trim()) / basicPay; // Convert bonus to percentage
                       employees.add(new FullTimeEmployee(empId, name, hireDate, basicPay, hraPercentage, annualBonus));
                   } catch (Exception e) {
                       System.err.println("Error parsing full-time employee: " + e.getMessage());
                   }
               }
           }
           return employees;
       }

       public static List<Employee> loadContractEmployees(String csvData) throws CsvValidationException, IOException {
           List<Employee> employees = new ArrayList<>();
           try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
               reader.readNext(); // Skip header
               String[] line;
               while ((line = reader.readNext()) != null) {
                   if (line.length < 6) continue; // Skip invalid rows
                   try {
                       String empId = line[0].trim();
                       String name = line[1].trim();
                       LocalDate hireDate = LocalDate.parse(line[2].trim());
                       double contractRate = Double.parseDouble(line[3].trim());
                       int contractDuration = Integer.parseInt(line[5].trim());
                       employees.add(new ContractEmployee(empId, name, hireDate, contractRate, contractDuration));
                   } catch (Exception e) {
                       System.err.println("Error parsing contract employee: " + e.getMessage());
                   }
               }
           }
           return employees;
       }

       public static List<Employee> loadDailyWageEmployees(String csvData) throws CsvValidationException, IOException {
           List<Employee> employees = new ArrayList<>();
           try (CSVReader reader = new CSVReader(new StringReader(csvData))) {
               reader.readNext(); // Skip header
               String[] line;
               while ((line = reader.readNext()) != null) {
                   if (line.length < 7) continue; // Skip invalid rows
                   try {
                       String empId = line[0].trim();
                       String name = line[1].trim();
                       LocalDate hireDate = LocalDate.parse(line[2].trim());
                       double dailyRate = Double.parseDouble(line[3].trim());
                       int workingDays = Integer.parseInt(line[4].trim());
                       employees.add(new DailyWageEmployee(empId, name, hireDate, dailyRate, workingDays));
                   } catch (Exception e) {
                       System.err.println("Error parsing daily wage employee: " + e.getMessage());
                   }
               }
           }
           return employees;
       }
   }