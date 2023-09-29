import java.math.BigDecimal;

public class Rules {

    public Rules(String date, String ruleId, String rate){
        this.date = date;
        this.ruleId = ruleId;
        this.rate = new BigDecimal(rate);
    }
    private String date;
    private String ruleId;
    private BigDecimal rate;

    public Rules() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString(){
        return getDate()+" " +getRuleId() +" " +getRate() +" /n";
    }

    @Override
    public boolean equals(Object obj) {
        Rules r = (Rules) obj;
        return this.ruleId.equals(r.ruleId);
    }
}
