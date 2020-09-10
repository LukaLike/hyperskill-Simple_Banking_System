package banking;

import banking.CommandPattern.Account;
import banking.CommandPattern.Controller;
import banking.CommandPattern.CreateAccountCommand;
import banking.CommandPattern.LogInToAccountCommand;

import java.util.Scanner;

public class Main {

    private static Scanner scanner;
    private static Controller controller;
    private static Account currentAccount;

    private static JDBC jdbc;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        controller = new Controller();

        if (args.length == 0) {
            jdbc = new JDBC("bank.db");
        } else {
            jdbc = new JDBC(args[1]);
        }

        mainInput();
    }

    private static void mainInput() {
        int input = -1;
        while (input != 0) {
            System.out.println("\n1. Create an account\n2. Log into account\n0. Exit");
            input = scanner.nextInt();

            switch (input) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    if (logIntoAccount()) {
                        bankInput();
                    }
                    break;
                default:
                    break;
            }
        }
        System.out.println("Bye!");
    }

    private static void bankInput() {
        int input;
        while (true) {
            System.out.println(
                    "1. Balance\n2. Add Income\n3. Do transfer\n4. Close account\n5. Log out\n6. Exit");
            input = scanner.nextInt();

            switch (input) {
                case 1:
                    System.out.println("\n" + jdbc.getBalance(currentAccount) + "\n");
                    break;
                case 2:
                    System.out.println("\nEnter income:");
                    jdbc.addIncome(currentAccount, scanner.nextInt());
                    break;
                case 3:
                    System.out.println("\nTransfer");
                    jdbc.transfer(currentAccount);
                    break;
                case 4:
                    jdbc.closeAccount(currentAccount);
                    return;
                case 5:
                    System.out.println("\nYou have successfully logged out!");
                    return;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
                default:
                    break;
            }
        }
    }

    private static void createAccount() {
        CreateAccountCommand command = new CreateAccountCommand(jdbc);
        controller.setCommand(command);
        controller.executeCommand();
    }

    private static boolean logIntoAccount() {
        LogInToAccountCommand command = new LogInToAccountCommand(jdbc);
        controller.setCommand(command);

        if (controller.executeCommand()) {
            currentAccount = command.getCurrentAccount();
            return true;
        } else {
            return false;
        }
    }
}
