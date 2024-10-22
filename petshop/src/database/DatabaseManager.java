package database;

// Package: com.taskmanager.database

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import model.Task;
import ui.MainUI;

import javax.swing.*;


public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
//        connection = ConnectionDB.getConnection();
    }

    // private Connection connection;

    // public DatabaseManager() {
    // initializeDatabase();
    // }

    // private void initializeDatabase() {
    // // Initialize the database connection
    // try {
    // connection =
    // DriverManager.getConnection("jdbc:mysql://localhost/taskmdb?user=root");
    // } catch (SQLException e) {
    // System.out.println("Failed to connect to the database.");
    // e.printStackTrace();
    // System.exit(1);
    // }
    // }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tasktable");
            while (resultSet.next()) {
                String taskName = resultSet.getString("taskName");
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                String startDate = resultSet.getString("dateStarted");
                String deadline = resultSet.getString("deadline");
                String status = resultSet.getString("status");
                Task task = new Task(taskName, description, category, startDate, deadline, status);
                tasks.add(task);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void addTask(Task task) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO tasktable (taskName, description, category, dateStarted, deadline, status) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, task.getCategory());

            // Convert the start date format to 'YYYY-MM-DD'
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = inputFormat.parse(task.getStartDate());
            String formattedStartDate = outputFormat.format(startDate);
            // Convert the deadline format to 'YYYY-MM-DD'
            java.util.Date deadline = inputFormat.parse(task.getDeadline());
            String formattedDeadline = outputFormat.format(deadline);

            preparedStatement.setString(4, formattedStartDate);
            preparedStatement.setString(5, formattedDeadline);
            preparedStatement.setString(6, task.getStatus());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task, String oldTaskName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE tasktable SET taskName = ?, description = ?, category = ?, dateStarted = ?, deadline = ?, status = ? WHERE taskName = ?");
            preparedStatement.setString(1, task.getTaskName());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, task.getCategory());


            // Convert the start date format to 'YYYY-MM-DD'
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = inputFormat.parse(task.getStartDate());
            String formattedStartDate = outputFormat.format(startDate);
            // Convert the deadline format to 'YYYY-MM-DD'
            java.util.Date deadline = inputFormat.parse(task.getDeadline());
            String formattedDeadline = outputFormat.format(deadline);

            preparedStatement.setString(4, formattedStartDate);
            preparedStatement.setString(5, formattedDeadline);
            preparedStatement.setString(6, task.getStatus());
            preparedStatement.setString(7, oldTaskName);
            preparedStatement.executeUpdate();
            preparedStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteTask(String taskName) {
        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM tasktable WHERE taskName = ?");
            preparedStatement.setString(1, taskName);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
