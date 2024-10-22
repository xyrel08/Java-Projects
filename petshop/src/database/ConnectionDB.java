//package database;
//
//import ui.MainUI;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class ConnectionDB extends MainUI {
//    private static Connection connection;
//
//    public static Connection getConnection() {
//        if (connection == null) {
//            initializeDatabase();
//        }
//        return connection;
//    }
//
//    private static void initializeDatabase() {
//        try {
//
//            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/taskmdb?user=root");
//            System.out.println("Connection in");
//        } catch (SQLException e) {
//            System.out.println("Failed to connect to the database.");
//            e.printStackTrace();
//            System.exit(1);
//        }
//    }
//}
//
