package BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private final Connection connection;
    private final Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void register() {
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (userExists(email)) {
            System.out.println("User already exists for this email address!");
            return;
        }
        String insertSql = "INSERT INTO bank_user (full_name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Registration successful!");
            } else {
                System.out.println("Registration failed!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during user registration", e);
        }
    }

    public String login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        String loginSql = "SELECT 1 FROM bank_user WHERE email = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(loginSql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? email : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during login", e);
        }
    }

    public boolean userExists(String email) {
        String query = "SELECT 1 FROM bank_user WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
}
