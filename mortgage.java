package company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;

public class Mortgage {
    private static float loanAmount;
    private static float couponValue;
    private static float termmonths;
    private JTextField couponValueTextField;
    private JTextField termYearsTextField;
    private JTextField loanAmountTextField;
    private JButton submitValuesButton;
    private JTextArea textArea1;

    public Mortgage(float loanAmount, float couponValue, float termmonths){
        this.loanAmount = loanAmount;
        this.couponValue = couponValue;
        this.termmonths = termmonths;
    }

    public static void updateRate(float rate){
        Mortgage.couponValue = rate;
    }
    
    /*

    This function takes some input and calculates the monthly mortgage payment with interest

    Psuedo: Take annual interest in decimal form. Divide by 12 to get monthly interest rate
    add 1 to the monthly rate.
    multiply numYearsInTerm by 12 to get months in the term
    take monthlyrate+1 and raise to -(numberofmonthly payments) subtract result from 1
    divide the monthly rate by the result and then multiply all of that by amount borrowed to get monthly payment
    inputs: loanAmount, couponvalue(annual interest rate), Term(in months)
     */

    public static double getMonthlyMortgagePayment(){
        // Vars: Loan Amount, CouponValue, Termmonths
        double denominator;
        double rate = couponValue/12;

        if(rate > 0 ){
            denominator=1/rate-1/(rate * Math.pow(1 + rate, termmonths));
            if (denominator != 0)
                return Math.max(0.0,loanAmount/denominator);
            else
                return 0.0;
        } else 
            return 0.0;
        }

        public static double getMonthlyMortgagePayment(float loanAmount, float couponValue, int term){
            // Vars: Loan Amount, CouponValue, Termmonths
            double denominator;
            double rate = couponValue/12;

            if(rate > 0 ){
                denominator=1/rate-1/(rate * Math.pow(1 + rate, term));
                if (denominator != 0)
                    return Math.max(0.0,loanAmount/denominator);
                else
                    return 0.0;
            } else
                return 0.0;
        }
        
        /*
        float monthlyInterest = (couponValue/12);
        float temp = (float)(1-(Math.pow(monthlyInterest+1,(-1* termmonths))));
        float temp2 = loanAmount * monthlyInterest / temp;
        // temp = (monthlyInterest-1)/temp;
        //float monthlyPayment = loanAmount*temp;
        return temp2;
        */

        // This is the method just for calculating mortgage payments

    public static ArrayList<ArrayList<Float>> showValues(Mortgage mortgage){
        float loanAmount = mortgage.loanAmount;
        float termMonths = mortgage.termmonths;
        float couponValue = mortgage.couponValue;

        double monthlyPay = mortgage.getMonthlyMortgagePayment();

        ArrayList<Float> Month = new ArrayList<>();
        ArrayList<Float> LoanAfterMonths = new ArrayList<>();
        ArrayList<Float> interestPayments = new ArrayList<>();
        ArrayList<Float> principlePayments = new ArrayList<>();
        ArrayList<Float> TotalCashFlow = new ArrayList<>();
        float newLoanAmt = loanAmount;
        int x = 0;
        while(x <=termMonths) {

            Month.add((float)x);
            LoanAfterMonths.add(newLoanAmt);
            float IntPayment = newLoanAmt*couponValue/12;
            interestPayments.add(IntPayment);

            float principle = (float)monthlyPay- IntPayment;
            principlePayments.add(principle);
            int a = 0;
            while(a < interestPayments.size()){
                float cashAtMonth = interestPayments.get(a) + principlePayments.get(a);
                TotalCashFlow.add(cashAtMonth);
                a++;
            }
            newLoanAmt = newLoanAmt - principle;
            x++;
        }

        //System.out.println(Month.size() + "  " + LoanAfterMonths.size());

        ArrayList<ArrayList<Float>> arr = new ArrayList<>();
        arr.add(Month);
        arr.add(LoanAfterMonths);
        arr.add(TotalCashFlow);
        arr.add(principlePayments);
        arr.add(interestPayments);

        return arr;
    }
    
    // this is the overloaded method to calculate the student loan values.
    public static ArrayList<ArrayList<Float>> showValues(Mortgage mortgage, String[] underwriteRate){
        float loanAmount = mortgage.loanAmount;
        float termMonths = mortgage.termmonths;
        float updatedBaseRate = (float)mortgage.calculateIR(underwriteRate[0], underwriteRate[1], underwriteRate[2]);
        float couponValue = updatedBaseRate;
        //System.out.println(" Starting rate: " + couponValue);
        double variableRate = mortgage.calculateVariableRate(couponValue);
        //System.out.println(" Variable Rate " + variableRate);
        double monthlyPay = mortgage.getMonthlyMortgagePayment(loanAmount,couponValue, (int)termmonths);
        //System.out.println(" TERM MONTHS " + termmonths);
        ArrayList<Float> Month = new ArrayList<>();
        ArrayList<Float> LoanAfterMonths = new ArrayList<>();
        ArrayList<Float> interestPayments = new ArrayList<>();
        ArrayList<Float> principlePayments = new ArrayList<>();
        ArrayList<Float> TotalCashFlow = new ArrayList<>();
        ArrayList<Float> updatedCoupon = new ArrayList<>();
        float newLoanAmt = loanAmount;
        int x = 0;
        while(x <=termMonths) {
            if(x%12 == 0 & x!= 0){
                couponValue = (float)variateRate(couponValue, variableRate);
                updatedCoupon.add(couponValue);
                monthlyPay = mortgage.getMonthlyMortgagePayment(newLoanAmt,couponValue, (int)termmonths-x);

            }
            Month.add((float)x);
            LoanAfterMonths.add(newLoanAmt);
            float IntPayment = newLoanAmt*couponValue/12;
            interestPayments.add(IntPayment);
            float principle = (float)monthlyPay- IntPayment;
            principlePayments.add(principle);
            int a = 0;
            while(a < interestPayments.size()){
                float cashAtMonth = interestPayments.get(a) + principlePayments.get(a);
                TotalCashFlow.add(cashAtMonth);
                a++;
            }
            newLoanAmt = newLoanAmt - principle;
            x++;
        }

        //System.out.println(Month.size() + "  " + LoanAfterMonths.size());

        ArrayList<ArrayList<Float>> arr = new ArrayList<>();
        arr.add(Month);
        arr.add(LoanAfterMonths);
        arr.add(TotalCashFlow);
        arr.add(principlePayments);
        arr.add(interestPayments);

        return arr;
    }

    public double getPV(double discountRate, Mortgage mortgage) {
        double pv=0;
        ArrayList<ArrayList<Float>> mortgageValues = showValues(mortgage);
        ArrayList<Float> TotalCash = mortgageValues.get(2);
        // Use periodic discountRate (no multiply by frequency)
        discountRate = discountRate/12;
       // discountRate = (Math.pow(1.0+discountRate/12, 2.0/12)-1);
        float cashAtMoment = 0;
        int period;
        for (period=0; period<termmonths; period++){
            cashAtMoment = TotalCash.get(period);
            pv += cashAtMoment / Math.pow(1.0+discountRate, (double)period);}

        return pv;
    }

    public double calculateIR(String schoolRank, String schoolLocation, String courseStudy){
        double schoolRate = 0;
        double locationRate = 0;
        double courseRate = 0;

        if(schoolRank.equals("top50")){
            schoolRate = 0.02;
        }
        else if(schoolRank.equals("middle")){
            schoolRate = 0.04;
        }
        else schoolRate = 0.05; // for lower tier schools

        if(schoolLocation.equals("ne")){
            locationRate = .04;
        }
        else if(schoolLocation.equals("midwest")){
            locationRate = .02;
        }
        else locationRate = .03; /// for west coast

        if(courseStudy.equals("stem")){
            courseRate = .02;
        }
        else if(courseStudy.equals("arts")){
            courseRate = .04;
        }
        else courseRate = .03; // for other majors ie business
        // taking the current treasury rate to be 1.73%
        double finalRate = (courseRate+locationRate+schoolRate+.0173)/4;

        return finalRate;

    }
    
    public static double calculateVariableRate(double startRate){
        // 10.5 taken as absolute ceiling of interest rates as per Sallie Mae
        // this is how much the interest rate is going to increase per year up to the cap of 10.5
        // assumes linear growth in interest rate
        double variableRate = (.105 - startRate)/12;
        return variableRate;
    }
    
    public static double variateRate(double currentRate, double variableRate){
        // function do increment
        double newRate = currentRate+variableRate;
        return newRate;
    }

    public static Float[] inputs(){
        Scanner myobj = new Scanner(System.in);
        System.out.println(" What is your loan amount" + "\n");
        float loanAmount = myobj.nextFloat();
        System.out.println("What is the number of months in the term?  " + "\n");
        float termmonths = myobj.nextFloat();
        System.out.println("What is your base/fixed interest rate?  " + "\n");
        float couponvalue = myobj.nextFloat();

        Float[] myarr = {loanAmount, termmonths, couponvalue};
        myobj.close();
        return myarr;
    }

    public static String[] inputRanking(){
        Scanner myobj = new Scanner(System.in);
        System.out.println("What is your schools approximate ranking " + "\n");
        String schoolRank = myobj.nextLine();

        System.out.println("Where is your school roughly located? " + "\n");
        String schoolLocation = myobj.nextLine();

        System.out.println("What is you major of studies? " + "\n");
        String majorStudies = myobj.nextLine();

        String[] results = {schoolRank, schoolLocation, majorStudies};
        return results;
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.

       /* Scanner myobj = new Scanner(System.in);
        System.out.println("Would you like to test a mortgage(fixed rate) or student loan(variable rate)");

        String mortOrStu = myobj.nextLine();
        if(mortOrStu.equals("mortgage")){
            Float[] myarr = inputs();
            float loanAmount = 0;
            float couponValue = 0;
            float termmonths = 0;


            loanAmount = myarr[0];
            couponValue = myarr[1];
            termmonths = myarr[2];*/
        /*float loanAmount = 1000000;
        double couponValue = 0.05;
        float termmonths = 120;
        Mortgage mortgage = new Mortgage(loanAmount, (float)couponValue, termmonths);
        ArrayList<ArrayList<Float>> mortgageValues = showValues(mortgage);
        ArrayList<Float> months = mortgageValues.get(0);
        ArrayList<Float> updatedLoan = mortgageValues.get(1);
        ArrayList<Float> TotalCash = mortgageValues.get(2);
        ArrayList<Float> Principle = mortgageValues.get(3);
        ArrayList<Float> Interest = mortgageValues.get(4);
        System.out.println("Months  " + months + "\n");
        System.out.println(" Updated balance    " +updatedLoan+ "\n ");
        System.out.println(" Total Cash " + TotalCash+ "\n");
        System.out.println(" Principle over time    " + Principle+ "\n");
        System.out.println(" Interest " + Interest+ "\n");
        System.out.println(" PRESENT VALUE:  " + mortgage.getPV(.03, mortgage)); //for mortgage analysis only
*/

/*
        else{
            Float[] myarr = inputs();
            float loanAmount = 0;
            float couponValue = 0;
            float termmonths = 0;

            loanAmount = myarr[0];
            couponValue = myarr[1];
            termmonths = myarr[2];

            String schoolRank = "";
            String schoolLocation = "";
            String major = "";
            String[] myarr2 = inputRanking();
            for (int i = 0; i < 3; i++) {
                schoolRank = myarr2[0];
                schoolLocation = myarr2[1];
                major = myarr2[2];
            //}*/

        String schoolRank = "top50";
        String schoolLocation = "ne";
        String major = "stem";
        float loanAmount = 1000000;
        double couponValue = 0.05;
        float termmonths = 120;
        String[] underwrite = {schoolRank, schoolLocation, major}; // for student loans analysis only
        Mortgage mortgage = new Mortgage(loanAmount, (float)couponValue, termmonths);

        ArrayList<ArrayList<Float>> mortgageValues = showValues(mortgage, underwrite);
        ArrayList<Float> months = mortgageValues.get(0);
        ArrayList<Float> updatedLoan = mortgageValues.get(1);
        ArrayList<Float> TotalCash = mortgageValues.get(2);
        ArrayList<Float> Principle = mortgageValues.get(3);
        ArrayList<Float> Interest = mortgageValues.get(4);

        System.out.println("Months  " + months + "\n");
        System.out.println(" Updated balance    " +updatedLoan+ "\n ");
        System.out.println(" Total Cash " + TotalCash+ "\n");
        System.out.println(" Principle over time    " + Principle+ "\n");
        System.out.println(" Interest " + Interest+ "\n");
    }
        //myobj.close();
}

    /*
    if(schoolRank.equals("top50")){
            schoolRate = 0.02;
        }
        else if(schoolRank.equals("middle")){
            schoolRate = 0.04;
        }
        else schoolRate = 0.05; // for lower tier schools

        if(schoolLocation.equals("ne")){
            locationRate = .04;
        }
        else if(schoolLocation.equals("midwest")){
            locationRate = .02;
        }
        else locationRate = .03; /// for west coast

        if(courseStudy.equals("stem")){
            courseRate = .02;
        }
        else if(courseStudy.equals("arts")){
            courseRate = .04;
        }
        else courseRate = .03; // for other majors ie business
        // taking the current treasury rate to be 1.73%
     */
