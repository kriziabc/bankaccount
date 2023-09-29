import java.math.BigDecimal;
import java.time.LocalDate;

public class InterestTable {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private BigDecimal eodBalance;
    private BigDecimal rate;

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public BigDecimal getEODBalance() {
        if(eodBalance == null){
            return new BigDecimal("0");
        }
        return eodBalance;
    }

    public void setEODBalance(BigDecimal eodBalance) {
        this.eodBalance = eodBalance;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public String toString(){
        return this.getDateFrom()+" "+this.getDateTo()+": Rate: "+this.getRate()
                +" EODBalance:"+this.getEODBalance();
    }
}
