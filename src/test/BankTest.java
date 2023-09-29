import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTest {
    Bank bank = new Bank();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT);
    @Test
    public void testValidateInputInvalidDate() {
        String inputDate = "12242023";
        String accountNo = "123";
        String type = "D";
        String amountInput = "100";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("DateErr", result);
    }

    @Test
    public void testValidateInputAccount() {
        String inputDate = "20231223";
        String accountNo = " ";
        String type = "D";
        String amountInput = "100";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("AcctEmpty", result);
    }

    @Test
    public void testValidateInputType() {
        String inputDate = "20231223";
        String accountNo = "Acc001";
        String type = " ";
        String amountInput = "100";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals(Constants.TYPE_ERR, result);
    }

    @Test
    public void testValidateInputAmountFormat() {
        String inputDate = "20231223";
        String accountNo = "123";
        String type = "W";
        String amountInput = "w";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("AmtErr", result);
    }

    @Test
    public void testValidateInputAmountScale() {
        String inputDate = "20231223";
        String accountNo = "123";
        String type = "W";
        String amountInput = "12.1323";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("DecimalErr", result);
    }

    @Test
    public void testValidateInputAmount() {
        String inputDate = "20231223";
        String accountNo = "123";
        String type = "W";
        String amountInput = "0";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("AmtZero", result);
    }

    @Test
    public void testValidateInputRuleDate() {
        String inputDate = "12232023";
        String ruleId = "123";
        String rate = "W";

        String result = Validation.validateRule(inputDate, ruleId, rate);
        assertEquals("DateErr", result);
    }

    @Test
    public void testValidateInputRuleId() {
        String inputDate = "20231223";
        String ruleId = " ";
        String rate = "W";

        String result = Validation.validateRule(inputDate, ruleId, rate);
        assertEquals("RuleEmpty", result);
    }

    @Test
    public void testValidateInputRuleRate() {
        String inputDate = "20231223";
        String ruleId = "Rule1";
        String rate = "101";

        String result = Validation.validateRule(inputDate, ruleId, rate);
        assertEquals("RateZero", result);
    }

    @Test
    public void testValidateInputRateFormat() {
        String inputDate = "20231223";
        String ruleId = "Rule1";
        String rate = "wer";

        String result = Validation.validateRule(inputDate, ruleId, rate);
        assertEquals("RateErr", result);
    }

    @Test
    public void testValidateInputRateScale() {
        String inputDate = "20231223";
        String accountNo = "123";
        String type = "W";
        String amountInput = "12.1323";

        String result = Validation.validateTxn(inputDate, accountNo, type, amountInput);
        assertEquals("DecimalErr", result);
    }

    @Test
    public void testValidateZeroBal(){
        String accountInput = "20230626 AC001 w 500.00";

        String result = bank.createTxn(accountInput);
        assertEquals("0bal", result);
    }

    @Test
    public void testValidateDecimalErr(){
        String errorCode = Constants.DECIMAL_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Decimals are allowed to "+Constants.PRECISION+" decimal places only.", result);
    }

    @Test
    public void testValidateRateZeroErr(){
        String errorCode = Constants.RATE_ZERO_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Rate should be greater than zero and less than 100.", result);
    }


    @Test
    public void testValidateAmountErr(){
        String errorCode = Constants.AMOUNT_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Invalid amount.", result);
    }

    @Test
    public void testValidateAmountZeroErr(){
        String errorCode = Constants.AMOUNT_ZERO_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Amount should be greater than zero.", result);
    }

    @Test
    public void testValidateTypeErr(){
        String errorCode = Constants.TYPE_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Type is invalid.", result);
    }

    @Test
    public void testValidateRuleEmptyErr(){
        String errorCode = Constants.RULE_EMPTY_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Rule Id cannot be empty.", result);
    }

    @Test
    public void testValidateAcctEmptyErr(){
        String errorCode = Constants.ACCT_EMPTY_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Account number cannot be empty.", result);
    }

    @Test
    public void testValidateDateErr(){
        String errorCode = Constants.DATE_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Date format is invalid.", result);
    }

    @Test
    public void testValidateZeroBalErr(){
        String errorCode = Constants.ZEROBAL_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Invalid transaction. Cannot do a withdrawal.", result);
    }

    @Test
    public void testValidateExceptionErr(){
        String errorCode = Constants.EXCEPTION_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("System error. Please contact your admin.", result);
    }

    @Test
    public void testValidateRuleSize(){
        String errorCode = Constants.RULES_SIZE_ERR;
        String result = Validation.handleErrors(errorCode);
        assertEquals("Interest rules should be at least two.", result);
    }

    @Test
    public void testInterestComputation(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT);
        List<InterestTable> txnDateRange = new ArrayList<>();
        InterestTable txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230601",formatter));
        txn.setDateTo(LocalDate.parse("20230625",formatter));
        txn.setEODBalance(new BigDecimal("250"));
        txnDateRange.add(txn);

        txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230626",formatter));
        txn.setDateTo(LocalDate.parse("20230630",formatter));
        txn.setEODBalance(new BigDecimal("130"));
        txnDateRange.add(txn);

        List<InterestTable> ruleDateRange = new ArrayList<>();
        InterestTable rule1 = new InterestTable();
        rule1.setRate(new BigDecimal("1.95"));
        rule1.setDateFrom(LocalDate.parse("20230101",formatter));
        rule1.setDateTo(LocalDate.parse("20230519",formatter));
        ruleDateRange.add(rule1);

        InterestTable rule2 = new InterestTable();
        rule2.setRate(new BigDecimal("1.90"));
        rule2.setDateFrom(LocalDate.parse("20230520",formatter));
        rule2.setDateTo(LocalDate.parse("20230614",formatter));
        ruleDateRange.add(rule2);

        InterestTable rule3 = new InterestTable();
        rule3.setRate(new BigDecimal("2.20"));
        rule3.setDateFrom(LocalDate.parse("20230615",formatter));
        rule3.setDateTo(LocalDate.parse("20230630",formatter));
        ruleDateRange.add(rule3);
        System.out.println("TXNS");
        for(InterestTable txnT: txnDateRange){
            System.out.println(txnT);
        }

        System.out.println("RULES");
        for(InterestTable ruleT: ruleDateRange){
            System.out.println(ruleT);
        }
        String result = bank.defineInterestTable(txnDateRange, ruleDateRange);
        System.out.println(result);
        assertEquals("0.39", result);
    }

    @Test
    public void testInterestComputation2(){
        List<InterestTable> txnDateRange = new ArrayList<>();
        InterestTable txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230601",formatter));
        txn.setDateTo(LocalDate.parse("20230613",formatter));
        txn.setEODBalance(new BigDecimal("500"));
        txnDateRange.add(txn);

        txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230614",formatter));
        txn.setDateTo(LocalDate.parse("20230619",formatter));
        txn.setEODBalance(new BigDecimal("650"));
        txnDateRange.add(txn);

        txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230620",formatter));
        txn.setDateTo(LocalDate.parse("20230624",formatter));
        txn.setEODBalance(new BigDecimal("450"));
        txnDateRange.add(txn);

        txn = new InterestTable();
        txn.setDateFrom(LocalDate.parse("20230625",formatter));
        txn.setDateTo(LocalDate.parse("20230630",formatter));
        txn.setEODBalance(new BigDecimal("550"));
        txnDateRange.add(txn);

        List<InterestTable> ruleDateRange = new ArrayList<>();
        InterestTable rule2 = new InterestTable();
        rule2.setRate(new BigDecimal("1.5"));
        rule2.setDateFrom(LocalDate.parse("20230601",formatter));
        rule2.setDateTo(LocalDate.parse("20230620",formatter));
        ruleDateRange.add(rule2);

        InterestTable rule3 = new InterestTable();
        rule3.setRate(new BigDecimal("2.5"));
        rule3.setDateFrom(LocalDate.parse("20230621",formatter));
        rule3.setDateTo(LocalDate.parse("20230630",formatter));
        ruleDateRange.add(rule3);

        String result = bank.defineInterestTable(txnDateRange, ruleDateRange);
        System.out.println(result);
        assertEquals("0.80", result);
    }

    @Test
    public void testGetTxnDateRange(){
        List<Transaction> filteredTxns = new ArrayList<>();
        Transaction tx = new Transaction();
        tx.setDate("20230601");
        tx.setBalance(new BigDecimal("250"));
        filteredTxns.add(tx);


        tx = new Transaction();
        tx.setDate("20230626");
        tx.setBalance(new BigDecimal("230"));
        filteredTxns.add(tx);

        tx = new Transaction();
        tx.setDate("20230626");
        tx.setBalance(new BigDecimal("530"));
        filteredTxns.add(tx);

        tx = new Transaction();
        tx.setDate("20230626");
        tx.setBalance(new BigDecimal("130"));
        filteredTxns.add(tx);

        String inputDate = "202306";
        List<InterestTable> result = bank.getTxnDateTable(filteredTxns,  inputDate);
    }

    @Test
    public void testComputeEOD(){
        //20230626 AC001 D 500.00
        String inputDate = "20230626";
        List<Transaction> filteredTxns = new ArrayList<>();
        Transaction tx = new Transaction();
        tx.setDate("20230505");
        tx.setType("D");
        tx.setBalance(new BigDecimal("100"));
        filteredTxns.add(tx);


        tx = new Transaction();
        tx.setDate("20230601");
        tx.setType("D");
        tx.setBalance(new BigDecimal("250"));
        filteredTxns.add(tx);

        tx = new Transaction();
        tx.setDate("20230626");
        tx.setType("W");
        tx.setBalance(new BigDecimal("230.00"));
        filteredTxns.add(tx);
        String type = "W";
        String enteredAmount = "100.00";
        String result = bank.computeEndBalance(filteredTxns,type,
                enteredAmount);

        assertEquals("130", result);
    }
}
