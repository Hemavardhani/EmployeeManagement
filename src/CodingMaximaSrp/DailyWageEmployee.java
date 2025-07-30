package CodingMaximaSrp;

   import java.time.LocalDate;

   public class DailyWageEmployee extends Employee {
       private double dailyRate;
       private int workingDays;

       public DailyWageEmployee(String empId, String name, LocalDate hireDate, double dailyRate, int workingDays) {
           super(empId, name, hireDate);
           this.dailyRate = dailyRate;
           this.workingDays = workingDays;
           setBasicPay(dailyRate * workingDays);
           setHra(0); // No HRA for daily wage employees
           setBonus(dailyRate * workingDays * 0.05); // 5% bonus
       }

       @Override
       public double calculateSalary() {
           setBasicPay(dailyRate * workingDays);
           double monthlySalary = getBasicPay() + getBonus();
           return Math.round(monthlySalary * 100.0) / 100.0;
       }

       public void updateWorkingDays(int days) {
           if (days < 0) {
               throw new IllegalArgumentException("Working days cannot be negative");
           }
           this.workingDays = days;
           setBonus(dailyRate * days * 0.05);
       }
   }