package banking.CommandPattern;

import banking.JDBC;

import java.util.Scanner;

public class LogInToAccountCommand implements Command {

    private final JDBC jdbc;
    private Account currentAccount;

    public LogInToAccountCommand(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nEnter your card number:");
        String cardNumber = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String cardPIN = scanner.nextLine();

        Account account = new Account(cardNumber, cardPIN);
        if (jdbc.exists(account)) {
            System.out.println("\nYou have successfully logged in!\n");
            currentAccount = account;
            return true;
        } else {
            System.out.println("\nWrong card number or PIN!");
            return false;
        }
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}