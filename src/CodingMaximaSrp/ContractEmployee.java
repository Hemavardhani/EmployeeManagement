package CodingMaximaSrp;

   import java.time.LocalDate;

   public class ContractEmployee extends Employee {
       private int contractDurationMonths;

       public ContractEmployee(String empId, String name, LocalDate hireDate, double contractRate, int contractDurationMonths) {
           super(empId, name, hireDate);
           setBasicPay(contractRate);
           setHra(contractRate * 0.2); // 20% HRA for contractors
           setBonus(0); // No bonus for contract employees
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