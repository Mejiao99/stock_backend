package stockhelper.main;


public interface Market {

    Currency getStockValue(String ticket);

    default double exchangeRate(String from, String to) {
        return 1.0;
    }

}
