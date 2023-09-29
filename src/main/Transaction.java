import java.math.BigDecimal;
public class Transaction {

    public Transaction(String date, String accountNo, String type, String amount,
                       String txnId, String endingBalance)
    {
        this.date = date;
        this.accountNo = accountNo;
        this.type = type;
        this.amount = new BigDecimal(amount);
        this.txnId = txnId;
        this.balance = new BigDecimal(endingBalance);
    }
    public Transaction() {
    }
    private String txnId;
    private String accountNo;
    private String date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;
    private Rules ruleId;

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Rules getRuleId() {
        return ruleId;
    }

    public void setRuleId(Rules ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String toString(){
        return getDate() + getTxnId() + getType() + getAmount();
    }


}
