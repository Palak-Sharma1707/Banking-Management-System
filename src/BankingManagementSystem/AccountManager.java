
package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

    public class AccountManager {
        private Connection connection;
        private Scanner scanner;

        AccountManager(Connection connection, Scanner scanner) {
            this.connection = connection;
            this.scanner = scanner;
        }

        public void creditMoney(long accountNumber) throws SQLException {
            System.out.print("Enter Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consume newline
            System.out.print("Enter Security Pin: ");
            String securityPin = scanner.nextLine();
            try {
                connection.setAutoCommit(false);
                if (accountNumber != 0) {
                    String selectSql = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
                    PreparedStatement selectPs = connection.prepareStatement(selectSql);
                    selectPs.setLong(1, accountNumber);
                    selectPs.setString(2, securityPin);
                    ResultSet rs = selectPs.executeQuery();
                    if (rs.next()) {
                        String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement creditPs = connection.prepareStatement(creditSql);
                        creditPs.setDouble(1, amount);
                        creditPs.setLong(2, accountNumber);
                        int rowsAffected = creditPs.executeUpdate();
                        if (rowsAffected > 0) {
                            connection.commit();
                            System.out.println("Rs. " + amount + " credited successfully");
                            return;
                        } else {
                            connection.rollback();
                            System.out.println("Transaction failed!");
                        }
                    } else {
                        connection.rollback();
                        System.out.println("Invalid security pin!");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        }

        public void debitMoney(long accountNumber) throws SQLException {
            System.out.print("Enter Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String securityPin = scanner.nextLine();
            try {
                connection.setAutoCommit(false);
                if (accountNumber != 0) {
                    String selectSql = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
                    PreparedStatement selectPs = connection.prepareStatement(selectSql);
                    selectPs.setLong(1, accountNumber);
                    selectPs.setString(2, securityPin);
                    ResultSet rs = selectPs.executeQuery();
                    if (rs.next()) {
                        double currentBalance = rs.getDouble("balance");
                        if (amount <= currentBalance) {
                            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                            PreparedStatement debitPs = connection.prepareStatement(debitSql);
                            debitPs.setDouble(1, amount);
                            debitPs.setLong(2, accountNumber);
                            int rowsAffected = debitPs.executeUpdate();
                            if (rowsAffected > 0) {
                                connection.commit();
                                System.out.println("Rs. " + amount + " debited successfully");
                                return;
                            } else {
                                connection.rollback();
                                System.out.println("Transaction failed!");
                            }
                        } else {
                            System.out.println("Insufficient balance!");
                        }
                    } else {
                        System.out.println("Invalid security pin!");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        }

        public void transferMoney(long senderAccountNumber) throws SQLException {
            System.out.print("Enter Receiver Account Number: ");
            long receiverAccountNumber = scanner.nextLong();
            System.out.print("Enter Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Security Pin: ");
            String securityPin = scanner.nextLine();
            try {
                connection.setAutoCommit(false);
                if (senderAccountNumber != 0 && receiverAccountNumber != 0) {
                    String senderSql = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
                    PreparedStatement senderPs = connection.prepareStatement(senderSql);
                    senderPs.setLong(1, senderAccountNumber);
                    senderPs.setString(2, securityPin);
                    ResultSet rs = senderPs.executeQuery();
                    if (rs.next()) {
                        double senderBalance = rs.getDouble("balance");
                        if (amount <= senderBalance) {
                            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                            PreparedStatement debitPs = connection.prepareStatement(debitSql);
                            PreparedStatement creditPs = connection.prepareStatement(creditSql);
                            debitPs.setDouble(1, amount);
                            debitPs.setLong(2, senderAccountNumber);
                            creditPs.setDouble(1, amount);
                            creditPs.setLong(2, receiverAccountNumber);
                            int debitRows = debitPs.executeUpdate();
                            int creditRows = creditPs.executeUpdate();
                            if (debitRows > 0 && creditRows > 0) {
                                connection.commit();
                                System.out.println("Rs. " + amount + " transferred successfully");
                                return;
                            } else {
                                connection.rollback();
                                System.out.println("Transaction failed!");
                            }
                        } else {
                            System.out.println("Insufficient balance!");
                        }
                    } else {
                        System.out.println("Invalid security pin!");
                    }
                } else {
                    System.out.println("Invalid account number!");
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        }

        public void getBalance(long accountNumber) {
            System.out.print("Enter Security Pin: ");
            String securityPin = scanner.nextLine();
            try {
                String sql = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, accountNumber);
                ps.setString(2, securityPin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("Balance: " + rs.getDouble("balance"));
                } else {
                    System.out.println("Invalid security pin!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

