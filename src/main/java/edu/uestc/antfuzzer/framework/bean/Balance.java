package edu.uestc.antfuzzer.framework.bean;

import lombok.Getter;
import lombok.Setter;

public class Balance implements Comparable<Balance> {
    @Getter
    @Setter
    double quantity;
    @Getter
    @Setter
    String symbol;

    public Balance(double quantity, String symbol) {
        this.quantity = quantity;
        this.symbol = symbol;
    }

    @Override
    public int compareTo(Balance o) {
        if (quantity == o.quantity) {
            return symbol.compareTo(o.getSymbol());
        }
        return (int) (quantity - o.quantity);
    }
}
