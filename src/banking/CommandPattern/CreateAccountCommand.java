package banking.CommandPattern;

import banking.JDBC;

import java.util.Arrays;
import java.util.Random;

public class CreateAccountCommand implements Command {
    private final JDBC jdbc;

    public CreateAccountCommand(JDBC jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean execute() {
        String cardNumber = "400000";
        cardNumber += initializeRandomNumbers(9);
        cardNumber += generateCheckSum(cardNumber);

        String cardPIN = initializeRandomNumbers(4);

        jdbc.insertAccount(cardNumber, cardPIN);

        System.out.printf(
                "\nYour card has been created" +
                "\nYour card number:\n%s" +
                "\nYour card PIN:\n%s\n",
                cardNumber, cardPIN);
        return true;
    }

    public String initializeRandomNumbers(int length) {
        Random random = new Random();
        StringBuilder randomNumber = new StringBuilder();

        for (int i = 0; i < length; i++) {
            randomNumber.append(random.nextInt(10));
        }

        return randomNumber.toString();
    }

    public Integer generateCheckSum(String cardNumber) {
        int checkSum = 0, result = 0;

        int[] numbers = Arrays.stream(cardNumber.split("")).mapToInt(Integer::parseInt).toArray();
        for (int i = 0; i < numbers.length; i++) {
            if (i % 2 == 0) {
                numbers[i] = numbers[i] * 2;
            }
            if (numbers[i] > 9) {
                numbers[i] = numbers[i] - 9;
            }

            checkSum += numbers[i];
        }

        while (checkSum % 10 != 0) {
            checkSum++;
            result++;
        }

        return result;
    }
}