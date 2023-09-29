import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
public class Bank {
    public Map<String, List<Transaction>> accountMap = new HashMap<>();
    public Map<String, Rules> rulesMap = new HashMap<>();
    public List<Rules> sortedRules = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT);
    PrintStatement printStatement = new PrintStatement();
    MathContext mc = new MathContext(Constants.PRECISION);

    public String createTxn(String accountInput){
        try{
            String[] accountArr = accountInput.split(" ");
            String inputDate = accountArr[0];
            String accountNo = accountArr[1];
            String type = accountArr[2];
            String amountInput = accountArr[3];
            String endBalance = "";
            if(!amountInput.contains(".")){
                amountInput = amountInput+".00";
            }
            String errorCode = Validation.validateTxn(inputDate, accountNo, type, amountInput);
            if(!errorCode.isEmpty()) return errorCode;
            List<Transaction> transactionList = new ArrayList<>();
            long totalLen = 0;
            if(accountMap.containsKey(accountNo)){
                transactionList = accountMap.get(accountNo);
                Transaction lastTxn = transactionList.get(transactionList.size()-1);
                LocalDate lastTxnDate = LocalDate.parse(lastTxn.getDate(), formatter);
                LocalDate currentTxnDate = LocalDate.parse(inputDate, formatter);

                if(currentTxnDate.isBefore(lastTxnDate)) return Constants.EARLY_DATE_ERR;

                totalLen = transactionList.stream().filter(dt -> inputDate.equals(dt.getDate())).count();
                endBalance = computeEndBalance(transactionList, type, amountInput);

                if(endBalance.equals("-1"))  return Constants.ZEROBAL_ERR;
                totalLen++;
            }else{
                if(type.equalsIgnoreCase("w"))  return Constants.ZEROBAL_ERR;
                endBalance = amountInput;
                totalLen = 1;
            }
            String txnId = inputDate+"-"+totalLen;
            transactionList.add(new Transaction(inputDate, accountNo,type, amountInput, txnId, endBalance));
            accountMap.put(accountNo, transactionList);
            printStatement.printTxn(transactionList);
        }catch(Exception e){
            return Constants.EXCEPTION_ERR;
        }
        return "";
    }

    public String computeEndBalance( List<Transaction> txns, String type,
                                        String enteredAmount){
        BigDecimal amountInput = new BigDecimal(enteredAmount);
        BigDecimal prevBalance = txns.get(txns.size()-1).getBalance();
        String output = "";
        if(type.equalsIgnoreCase("w")){
            if(amountInput.compareTo(prevBalance) > 0){
                output = "-1";
            }else{
                BigDecimal remainingBal = prevBalance.subtract(amountInput,mc);
                output = remainingBal.toPlainString();
            }
        }
        if(type.equalsIgnoreCase("d")){
            BigDecimal remainingBal = prevBalance.add(amountInput, mc);
            output = remainingBal.toPlainString();
        }
        return output;
    }
    public String createRule(String ruleInput){
        try{
            String[] ruleArr = ruleInput.split(" ");
            String inputDate = ruleArr[0];
            String ruleId = ruleArr[1];
            String rate = ruleArr[2];

            String errorCode = Validation.validateRule(inputDate, ruleId,rate);
            if(!errorCode.isEmpty()) return errorCode;
            Rules rule = new Rules(inputDate, ruleArr[1], ruleArr[2]);
            rulesMap.put(inputDate, rule);
            sortedRules = rulesMap.values().stream().sorted((o1, o2) -> {
                LocalDate date1 = LocalDate.parse(o1.getDate(), formatter);
                LocalDate date2 = LocalDate.parse(o2.getDate(), formatter);
                return date1.compareTo(date2);
            }).toList();
            System.out.println("Interest rules: ");
            printStatement.printRules(sortedRules);
        }catch(Exception e){
            return Constants.EXCEPTION_ERR;
        }

        return "";
    }
    public String createStatement(String input) {
        try{
            String[] inputArr = input.split(" ");
            String accountNo = inputArr[0];
            String inputDate = inputArr[1];

            if(sortedRules.size() < 2){
                return Constants.RULES_SIZE_ERR;
            }
            List<InterestTable>  ruleDateRange = getRuleDateTable(inputDate);
            List<Transaction> transactionList = new ArrayList<>(accountMap.get(accountNo).stream()
                    .filter(dt -> {
                        return dt.getDate().contains(inputDate);
                    }).toList());
            List<InterestTable>  txnDateRange = getTxnDateTable(transactionList, inputDate);

            String interestStr = defineInterestTable(txnDateRange, ruleDateRange);
            BigDecimal interest = new BigDecimal(interestStr);

            BigDecimal balanceWithInterest = transactionList.get(transactionList.size() -1)
                    .getBalance().add(interest);
            YearMonth date = YearMonth.parse(inputDate, DateTimeFormatter.ofPattern("yyyyMM"));
            String lastDayOfMonth = formatter.format(date.atEndOfMonth());
            transactionList.add(addEODInterest(interest,lastDayOfMonth, balanceWithInterest));
            printStatement.printStatement(transactionList);
        }catch(Exception e){
            return Constants.EXCEPTION_ERR;
        }
        return "";
    }

    public Transaction addEODInterest(BigDecimal interest,
                                      String lastDayOfMonth, BigDecimal balanceWithInterest){
        Transaction eodInterestTxn = new Transaction();
        eodInterestTxn.setTxnId("");
        eodInterestTxn.setType("I");
        eodInterestTxn.setAmount(interest);
        eodInterestTxn.setDate(lastDayOfMonth);
        eodInterestTxn.setBalance(balanceWithInterest);
        return eodInterestTxn;
    }
    public  List<InterestTable> getTxnDateTable(List<Transaction> filteredTxns, String inputDate){
        YearMonth date = YearMonth.parse(inputDate, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate lastDayOfMonth = LocalDate.parse(formatter.format(date.atEndOfMonth()), formatter);

        List<InterestTable> rateTable = new ArrayList<>();
        for(int i = 0; i < filteredTxns.size(); i++){
            Transaction currentTxn = filteredTxns.get(i);
            if((i+1) < filteredTxns.size()){
                Transaction nextTxn = filteredTxns.get(i+1);
                if(nextTxn.getDate().equals(currentTxn.getDate())){
                    continue;
                }
            }
            LocalDate currentTxnDate = LocalDate.parse(currentTxn.getDate(), formatter);
            InterestTable iTable = new InterestTable();
            iTable.setDateFrom(currentTxnDate);
            iTable.setEODBalance(currentTxn.getBalance());
            if((i+1) < filteredTxns.size() ){
                LocalDate nextRuleDate = LocalDate.parse(filteredTxns.get(i+1).getDate(), formatter);
                iTable.setDateTo(nextRuleDate.minusDays(1));
                rateTable.add(iTable);
            }

            if(i == filteredTxns.size() - 1){
                iTable.setDateFrom(currentTxnDate);
                iTable.setDateTo(lastDayOfMonth);
                rateTable.add(iTable);
            }
        }
        return rateTable;
    }
    public  List<InterestTable> getRuleDateTable(String inputDate){
        YearMonth date = YearMonth.parse(inputDate, DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDate lastDayOfMonth = LocalDate.parse(formatter.format(date.atEndOfMonth()), formatter);

        List<Rules> rules = sortedRules.stream().filter(r -> r.getDate().contains(inputDate)).toList();
        List<InterestTable> rateTable = new ArrayList<>();
        LocalDate firstDate = LocalDate.parse(inputDate+"01",formatter);

        for(int i = 0; i < rules.size(); i++){
            Rules currentRule = rules.get(i);
            LocalDate currentRuleDate = LocalDate.parse(currentRule.getDate(), formatter);
            InterestTable iTable = new InterestTable();
            InterestTable iTable2 = new InterestTable();
            if(i == 0){
                if(currentRuleDate.compareTo(firstDate) != 0){ //first ruledate is not 06-01
                    Rules previousRule = new Rules();
                    if(!sortedRules.isEmpty()){
                        Rules firstFiltered = rules.get(0);
                        int firstIndex = getIndexFromSortedList(firstFiltered);
                        previousRule = sortedRules.get( firstIndex- 1);
                    }
                    iTable.setDateFrom(firstDate);
                    iTable.setDateTo(currentRuleDate.minusDays(1));
                    iTable.setRate(previousRule.getRate());
                    rateTable.add(iTable);

                }else{
                    if((i+1) < rules.size() ){
                        LocalDate nextRuleDate = LocalDate.parse(rules.get(i+1).getDate(), formatter);;
                        iTable.setDateTo(nextRuleDate.minusDays(1));
                        iTable.setDateFrom(currentRuleDate);
                        iTable.setRate(currentRule.getRate());
                        rateTable.add(iTable);
                    }
                }

            }else{
                if((i+1) < rules.size() ){
                    LocalDate nextRuleDate = LocalDate.parse(rules.get(i+1).getDate(), formatter);
                    iTable2.setDateTo(nextRuleDate.minusDays(1));
                    iTable2.setDateFrom(currentRuleDate);
                    iTable2.setRate(currentRule.getRate());
                    rateTable.add(iTable2);
                }
            }
            if(i == rules.size() - 1){ // handles if only one  record
                iTable2.setDateFrom(currentRuleDate);
                iTable2.setDateTo(lastDayOfMonth);
                iTable2.setRate(currentRule.getRate());
                rateTable.add(iTable2);
            }
        }
        return rateTable;
    }

    public String defineInterestTable(List<InterestTable> txnDateRange, List<InterestTable> ruleDateRange) {
        InterestTable txn;
        InterestTable rule;
        List<InterestTable> statement = new ArrayList<>();
        int ruleIdx = 0;
        for (int c = 0; c < txnDateRange.size(); c++) {

            txn = txnDateRange.get(c);
            rule = ruleDateRange.get(ruleIdx);
            if (txn.getDateFrom().compareTo(rule.getDateFrom()) >= 0 &&
                    txn.getDateFrom().compareTo(rule.getDateTo()) <= 0) {
                if (txn.getDateTo().compareTo(rule.getDateTo()) <= 0) {
                    InterestTable iTable = new InterestTable();
                    iTable.setDateFrom(txn.getDateFrom());
                    iTable.setDateTo(txn.getDateTo());
                    iTable.setRate(rule.getRate());
                    iTable.setEODBalance(txn.getEODBalance());
                    statement.add(iTable);
                } else {
                    InterestTable iTable = new InterestTable();
                    iTable.setDateFrom(txn.getDateFrom());
                    iTable.setDateTo(rule.getDateTo());
                    iTable.setRate(rule.getRate());
                    iTable.setEODBalance(txn.getEODBalance());
                    statement.add(iTable);

                    if((ruleIdx+1) < ruleDateRange.size()){
                        InterestTable additional = new InterestTable();
                        additional.setDateFrom(rule.getDateTo().plusDays(1));
                        additional.setDateTo(txn.getDateTo());
                        additional.setRate(ruleDateRange.get(ruleIdx+1).getRate());
                        additional.setEODBalance(txn.getEODBalance());
                        statement.add(additional);
                        ruleIdx++;
                    }

                }
            }else{
                if(ruleIdx+1 < ruleDateRange.size()){
                    ruleIdx++;
                    c--;
                }
            }
        }

        BigDecimal eodI = computeInterest(statement);
        return eodI.toPlainString();

    }

    public BigDecimal computeInterest(List<InterestTable> iTable){
        BigDecimal total = new BigDecimal("0");
        for(InterestTable txnInterest : iTable){
            long numOfDays =  ChronoUnit.DAYS.between(txnInterest.getDateFrom()
                    ,txnInterest.getDateTo()) + 1;
            BigDecimal eodPercentage = txnInterest.getRate().divide(new BigDecimal("100"), mc);
            BigDecimal eodBalRate = txnInterest.getEODBalance().multiply(eodPercentage);
            BigDecimal eodBalTotal  = eodBalRate.multiply(new BigDecimal(numOfDays));
            total = total.add(eodBalTotal);
        }
        total = total.divide(new BigDecimal("365"), mc);
        return total;
    }
    public int getIndexFromSortedList(Rules firstFiltered){
        for(int j = 0; j < sortedRules.size(); j++){
            if(sortedRules.get(j).getRuleId().equals(firstFiltered.getRuleId())){
                return j;
            }
        }
        return 0;
    }
    public void run() {
        String menuInput = " ";
        String q1 = "Welcome to AwesomeGIC Bank! What would you like to do? \n";
        String q2 = new StringBuilder().append("[T] Input transactions \n").append("[I] Define interest rules\n").append("[P] Print statement\n").append("[Q] Quit").toString();
        String err = "";
        do{
            System.out.println(q1 + q2);
            Scanner menuQ = new Scanner(System.in);
            menuInput = menuQ.nextLine();
            if(menuInput.equalsIgnoreCase("t")){
                System.out.println("Please enter transaction details in <Date> <Account> <Type> <Amount> format" +
                        "\n" +
                        "(or enter blank to go back to main menu):");
                Scanner txnQ = new Scanner(System.in);
                String txnInput = txnQ.nextLine();
                if(txnInput.trim().isEmpty()){
                    menuInput = " ";
                }else{
                    err = createTxn(txnInput);
                    System.out.println(Validation.handleErrors(err));
                    q1 =  "Is there anything else you'd like to do? \n";

                }
            }else if(menuInput.equalsIgnoreCase("i")){
                System.out.println("Please enter interest rules details in <Date> <RuleId> <Rate in %> format \n" +
                        "(or enter blank to go back to main menu):");
                Scanner ruleQ = new Scanner(System.in);
                String ruleInput = ruleQ.nextLine();
                if(ruleInput.trim().isEmpty()){
                    menuInput = " ";
                }else{
                    err = createRule(ruleInput);
                    System.out.println(Validation.handleErrors(err));
                    q1 =  "Is there anything else you'd like to do? \n";

                }
            }else if(menuInput.equalsIgnoreCase("p")){
                System.out.println("Please enter account and month to generate the statement <Account> <Year><Month>\n" +
                        "(or enter blank to go back to main menu):");
                Scanner statementQ = new Scanner(System.in);
                String statementInput = statementQ.nextLine();
                if(statementInput.trim().isEmpty()){
                    menuInput = " ";
                }else{
                    err = createStatement(statementInput);
                    System.out.println(Validation.handleErrors(err));
                    q1 =  "Is there anything else you'd like to do? \n";
                }
            }

        }while(!menuInput.equalsIgnoreCase("q"));
        System.out.println("Thank you for banking with AwesomeGIC Bank.\n" +
                "Have a nice day!");
    }
}