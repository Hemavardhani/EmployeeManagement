package CodingMaximaSrp;

import java.time.LocalDate;

public abstract class Employee {
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

    public String getEmpId() {
        return empId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public double getBasicPay() {
        return basicPay;
    }

    public void setBasicPay(double basicPay) {
        if (basicPay < 0) {
            throw new IllegalArgumentException("Basic pay cannot be negative");
        }
        this.basicPay = basicPay;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        if (hra < 0) {
            throw new IllegalArgumentException("HRA cannot be negative");
        }
        this.hra = hra;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        if (bonus < 0) {
            throw new IllegalArgumentException("Bonus cannot be negative");
        }
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
