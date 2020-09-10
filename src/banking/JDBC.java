package banking;

import banking.CommandPattern.Account;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class JDBC {
    private final SQLiteDataSource dataSource;

    public JDBC(String newUrl) {
        String url = Objects.requireNonNullElse("jdbc:sqlite:" + newUrl, "jdbc:sqlite:bank.db");

        dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS card(" +
                                "id INTEGER PRIMARY KEY," +
                                "number TEXT NOT NULL," +
                                "pin TEXT NOT NULL," +
                                "balance INTEGER NOT NULL DEFAULT 0" +
                                ");"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBalance(Account account) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet accounts = statement.executeQuery("SELECT balance FROM card " +
                        "WHERE number = " + account.getCardNumber() + ";")) {
                    return accounts.getInt("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void insertAccount(String number, String pin) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        "INSERT INTO card (number, pin) VALUES(" + number + ", " + pin + ");"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(Account account) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet accounts = statement.executeQuery("SELECT * FROM card")) {
                    while (accounts.next()) {
                        String number = accounts.getString("number");
                        String pin = accounts.getString("pin");

                        if (number.equals(account.getCardNumber()) && pin.equals(account.getCardPIN())) {
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void addIncome(Account account, int income) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("UPDATE card SET balance = balance + " + income + " " +
                        "WHERE number = " + account.getCardNumber());
                System.out.println("Income was added\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transfer(Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter card number:");
        String cardNumber = scanner.next();

        if (!checkCard(cardNumber)) {
            System.out.println("Probably you made mistake in the card number. Please try again!\n");
            return;
        }
        if (!checkCardExistance(cardNumber)) {
            System.out.println("Such a card does not exist.\n");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        int money = scanner.nextInt();

        if (getBalance(account) < money) {
            System.out.println("Not enough money!\n");
            return;
        }
        if (account.getCardNumber().equals(cardNumber)) {
            System.out.println("You can't transfer money to the same account!\n");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("UPDATE card SET balance = balance - " + money +
                        " WHERE number = " + account.getCardNumber() + ";");
                statement.execute("UPDATE card SET balance = balance + " + money + "" +
                        " WHERE number = " + cardNumber + ";");
                System.out.println("Success!\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCard(String cardNumber) {
        int checkSum = 0;

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

        return checkSum % 10 == 0;
    }

    public boolean checkCardExistance(String cardNumber) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet accounts = statement.executeQuery("SELECT COUNT(*) AS total FROM card " +
                        "WHERE number = " + cardNumber + ";")) {
                    return accounts.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeAccount(Account account) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM card WHERE number = " + account.getCardNumber());
                System.out.println("\nThe account has been closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
