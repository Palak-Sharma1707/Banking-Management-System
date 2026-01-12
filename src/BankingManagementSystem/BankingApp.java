package BankingManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    private static final String db_url = jdbc:mysql://localhost:3306/banking_system
    private static final String db_username = root
    private static final String db_password = your_password

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(db_url, db_username, db_password);
             Scanner scanner = new Scanner(System.in)) {
            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);
            String email;
            long accountNumber;
            while (true) {
                System.out.println("\n*** WELCOME TO BANKING SYSTEM ***");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int mainChoice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                switch (mainChoice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if (email == null) {
                            System.out.println("Incorrect email or password!");
                            break;
                        }
                        System.out.println("\nUser logged in successfully!");
                        if (!accounts.accountExists(email)) {
                            System.out.println("\n1. Open a new bank account");
                            System.out.println("2. Exit");
                            System.out.print("Enter choice: ");
                            int openChoice = scanner.nextInt();
                            scanner.nextLine();
                            if (openChoice == 1) {
                                accountNumber = accounts.openAccount(email);
                                System.out.println("Account created successfully!");
                                System.out.println("Your account number is: " + accountNumber);
                            } else {
                                break;
                            }
                        }
                        accountNumber = accounts.getAccountNumber(email);
                        int userChoice = 0;
                        while (userChoice != 5) {
                            System.out.println("\n1. Debit Money");
                            System.out.println("2. Credit Money");
                            System.out.println("3. Transfer Money");
                            System.out.println("4. Check Balance");
                            System.out.println("5. Log Out");
                            System.out.print("Enter your choice: ");
                            userChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (userChoice) {
                                case 1:
                                    accountManager.debitMoney(accountNumber);
                                    break;
                                case 2:
                                    accountManager.creditMoney(accountNumber);
                                    break;
                                case 3:
                                    accountManager.transferMoney(accountNumber);
                                    break;
                                case 4:
                                    accountManager.getBalance(accountNumber);
                                    break;
                                case 5:
                                    System.out.println("Logging out...");
                                    break;
                                default: System.out.println("Enter a valid choice!");
                            }
                        }
                        break;
                    case 3:
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM!");
                        System.out.println("Exiting system...");
                        return;
                    default: System.out.println("Enter a valid choice!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }
}
