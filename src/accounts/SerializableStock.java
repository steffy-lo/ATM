package accounts;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.io.Serializable;


public class SerializableStock implements Serializable {

    private static final long serialVersionUID = 1L;

    private Stock stock;

    SerializableStock(final Stock stock) {
        this.stock = stock;
    }

    public Stock getStock() {return this.stock;}

    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        stream.writeObject(stock.getSymbol());
        stream.writeObject(stock.getName());
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.stock = YahooFinance.get((String) stream.readObject());
        this.stock.setName((String) stream.readObject());
    }

}
