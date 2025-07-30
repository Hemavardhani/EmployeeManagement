package CodingMaximaSrp;

   import java.time.LocalDate;

   public class FullTimeEmployee extends Employee {
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