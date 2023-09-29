import java.math.RoundingMode;
import java.util.List;

public class PrintStatement {
    public void printTxn(List<Transaction> data){
        System.out.printf("| %-8s | %-10s | %-2s | %8s |%n","Date",
                "Txn Id", "Type", "Amount");
        data.forEach(
                (t) -> {
                    System.out.printf("| %-8s | %-10s | %-4s | %8s |%n",
                            t.getDate() , t.getTxnId() , t.getType(), t.getAmount());
                }
        );
    }
    public void printRules(List<Rules> data){
        System.out.printf("| %-8s | %-8s | %-8s |%n","Date",
                "RuleId", "Rate (%)");
        data.forEach(
                (t) -> {
                    System.out.printf("| %-8s | %-8s | %8s |%n",
                            t.getDate(), t.getRuleId() , t.getRate());
                }
        );
    }
    public void printStatement(List<Transaction> data){
        System.out.printf("| %-8s | %-10s | %-2s | %8s | %8s |%n","Date",
                "Txn Id", "Type", "Amount", "Balance");
        data.forEach(
                (t) -> {
                    System.out.printf("| %-8s | %-10s | %-4s | %8s | %8s |%n",
                            t.getDate() , t.getTxnId() , t.getType(),
                            t.getAmount(), t.getBalance().setScale(2, RoundingMode.HALF_UP));
                }
        );
    }
}
