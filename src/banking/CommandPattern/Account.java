package banking.CommandPattern;

import org.jetbrains.annotations.NotNull;

public class Account implements Comparable<Account> {

    private final String cardNumber;
    private final String cardPIN;
    private final int balance;

    public Account(String cardNumber, String cardPIN) {
        this.cardNumber = cardNumber;
        this.cardPIN = cardPIN;
        balance = 0;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardPIN() {
        return cardPIN;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public int compareTo(@NotNull Account o) {
        return this.cardNumber.equals(o.getCardNumber())
                && this.cardPIN.equals(o.getCardPIN()) ? 1 : 0;
    }
}