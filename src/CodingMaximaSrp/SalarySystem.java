package CodingMaximaSrp;

   import java.time.LocalDate;
   import java.util.*;
   import java.io.FileWriter;
   import java.io.IOException;

   public class SalarySystem {
       private Map<String, Employee> employees;

       public SalarySystem() {
           this.employees = new HashMap<>();
       }

       public void addEmployees(List<Employee> employeeList) {
           for (Employee emp : employeeList) {
               employees.put(emp.getEmpId(), emp);
           }
       }

       public String removeEmployee(String empId) {
           if (employees.containsKey(empId)) {
               Employee emp = employees.remove(empId);
               return "Employee " + emp.getName() + " removed";
           }
           return "Employee " + empId + " not found";
       }

       public Double getEmployeeSalary(String empId) {
           Employee employee = employees.get(empId);
           return employee != null ? employee.calculateSalary() : null;
       }

       public String applyIncrement(String empId, double incrementPercentage) {
           Employee employee = employees.get(empId);
           if (employee instanceof FullTimeEmployee) {
               ((FullTimeEmployee) employee).applyAnnualIncrement(incrementPercentage);
               return "Increment applied to " + employee.getName();
           }
           return "Increment only applicable to full-time employees";
       }

       public void bulkSalaryAdjustment(Class<? extends Employee> employeeType, double basicChange, double hraChange, double bonusChange) {
           for (Employee emp : employees.values()) {
               if (employeeType.isInstance(emp)) {
                   emp.adjustSalary(basicChange, hraChange, bonusChange);
               }
           }
       }

       public List<Employee> filterEmployeesBySalary(double minSalary, double maxSalary) {
           List<Employee> filtered = new ArrayList<>();
           for (Employee emp : employees.values()) {
               double salary = emp.calculateSalary();
               if (salary >= minSalary && salary <= maxSalary) {
                   filtered.add(emp);
               }
           }
           return filtered;
       }

       public List<Employee> filterEmployeesByType(Class<? extends Employee> employeeType) {
           List<Employee> filtered = new ArrayList<>();
           for (Employee emp : employees.values()) {
               if (employeeType.isInstance(emp)) {
                   filtered.add(emp);
               }
           }
           return filtered;
       }

       public List<Employee> filterEmployeesByHireDate(LocalDate startDate, LocalDate endDate) {
           List<Employee> filtered = new ArrayList<>();
           for (Employee emp : employees.values()) {
               LocalDate hireDate = emp.getHireDate();
               if (!hireDate.isBefore(startDate) && !hireDate.isAfter(endDate)) {
                   filtered.add(emp);
               }
           }
           return filtered;
       }

       public List<Employee> sortEmployeesBySalary(boolean ascending) {
           List<Employee> sorted = new ArrayList<>(employees.values());
           sorted.sort((e1, e2) -> {
               double diff = e1.calculateSalary() - e2.calculateSalary();
               return ascending ? (int) (diff * 100) : (int) (-diff * 100);
           });
           return sorted;
       }

       public List<Employee> sortEmployeesByName() {
           List<Employee> sorted = new ArrayList<>(employees.values());
           sorted.sort(Comparator.comparing(Employee::getName));
           return sorted;
       }

       public Map<String, Object> generatePayrollReport() {
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

       public void exportPayrollToCsv(String filename) {
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
   }