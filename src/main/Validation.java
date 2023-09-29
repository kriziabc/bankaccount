import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Validation {
    public static String validateTxn(String inputDate, String requiredInput,
                                String type,String amountInput) {
        String errCode = "";
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT);
            LocalDate.parse(inputDate, formatter);
        }catch(Exception e){
            return Constants.DATE_ERR;
        }
        if(requiredInput.trim().isEmpty()){
            return Constants.ACCT_EMPTY_ERR;
        }

        if(type.trim().isEmpty() || !(type.equalsIgnoreCase("d") ||  type.equalsIgnoreCase("w"))) {
            return Constants.TYPE_ERR;
        }
        try{
            BigDecimal amt =  new BigDecimal(amountInput);
            if(amt.compareTo(new BigDecimal("0")) <= 0 ){
                return Constants.AMOUNT_ZERO_ERR;
            }
            if(amt.scale() != Constants.PRECISION) return Constants.DECIMAL_ERR;
        }catch(Exception e){
            return Constants.AMOUNT_ERR;
        }
        return errCode;
    }
    public static String validateRule(String inputDate, String requiredInput,
                              String rateStr) {
        String errCode ="";
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATEFORMAT);
            LocalDate.parse(inputDate, formatter);
        }catch(Exception e){
            return Constants.DATE_ERR;
        }
        if(requiredInput.trim().isEmpty()){
            return Constants.RULE_EMPTY_ERR;
        }

        try{
            BigDecimal rate =  new BigDecimal(rateStr);
            if(rate.compareTo(new BigDecimal("0")) <= 0 ||
                    rate.compareTo(new BigDecimal("100")) > 0){
                return Constants.RATE_ZERO_ERR;
            }
            if(rate.scale() != Constants.PRECISION) return Constants.DECIMAL_ERR;
        }catch(Exception e){
            return Constants.RATE_FORMAT_ERR;
        }
        return errCode;
    }
    public static String handleErrors(String errorCode){
        if(errorCode.equals(Constants.ZEROBAL_ERR)){
            return "Invalid transaction. Cannot do a withdrawal.";
        }
        if(errorCode.equals(Constants.DATE_ERR)){
            return "Date format is invalid.";
        }
        if(errorCode.equals(Constants.ACCT_EMPTY_ERR)){
            return "Account number cannot be empty.";
        }
        if(errorCode.equals(Constants.RULE_EMPTY_ERR)){
            return "Rule Id cannot be empty.";
        }
        if(errorCode.equals(Constants.TYPE_ERR)){
            return "Type is invalid.";
        }
        if(errorCode.equals(Constants.AMOUNT_ZERO_ERR)){
            return "Amount should be greater than zero.";
        }
        if(errorCode.equals(Constants.AMOUNT_ERR)){
            return "Invalid amount.";
        }
        if(errorCode.equals(Constants.RATE_ZERO_ERR)){
            return "Rate should be greater than zero and less than 100.";
        }
        if(errorCode.equals(Constants.RATE_FORMAT_ERR)){
            return "Invalid rate.";
        }
        if(errorCode.equals(Constants.DECIMAL_ERR)){
            return "Decimals are allowed to "+Constants.PRECISION+" decimal places only.";
        }
        if(errorCode.equals(Constants.EXCEPTION_ERR)){
            return "System error. Please contact your admin.";
        }
        if(errorCode.equals(Constants.RULES_SIZE_ERR)){
            return "Interest rules should be at least two.";
        }
        if(errorCode.equals(Constants.EARLY_DATE_ERR)){
            return "Cannot create a transaction with earlier date.";
        }
        return "";
    }
}
