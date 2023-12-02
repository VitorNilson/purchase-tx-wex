package wex.purchasetx.exchangerate.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException() {
        super("Value conversion for the provided country's currency is not supported.");
    }
}
