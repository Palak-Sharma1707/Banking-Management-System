package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Accounts {
    private final Connection connection;
    private final Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long openAccount(String email) {
        if (accountExists(email)) {
            throw new RuntimeException("Account already exists!");
        }
        String insertSql = "INSERT INTO accounts (account_number, full_name, email, balance, security_pin) " + "VALUES (?, ?, ?, ?, ?)";
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter Initial Amount: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = scanner.nextLine();

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            long accountNumber = generateAccountNumber();
            ps.setLong(1, accountNumber);
            ps.setString(2, fullName);
            ps.setString(3, email);
            ps.setDouble(4, balance);
            ps.setString(5, securityPin);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return accountNumber;
            }
            throw new RuntimeException("Account creation failed!");
        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating account", e);
        }
    }

    public long getAccountNumber(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("account_number");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching account number", e);
        }
        throw new RuntimeException("Account number does not exist!");
    }

    private long generateAccountNumber() {
        String query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getLong("account_number") + 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating account number", e);
        }
        return 10000100L;
    }

    public boolean accountExists(String email) {
        String query = "SELECT 1 FROM accounts WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking account existence", e);
        }
    }
}
