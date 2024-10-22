import sun.applet.Main;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

 class TaskMain extends JFrame {

    private JTable taskTable;
    private DefaultTableModel taskTableModel;
    private JTextField taskInput;
    private JTextField descriptionInput;
    private JComboBox<String> categoryInput;
    private JSpinner startDateInput;
    private JSpinner deadlineInput;
    private JComboBox<String> statusInput;
    private Connection connection;

    public TaskMain() {
        initializeDatabase();
        initializeUI();
        updateTable();
    }

    private void initializeDatabase() {
        // Initialize the database connection
        try {
            System.out.println("Connection in");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/taskmdb?user=root");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initializeUI() {
        // Initialize the UI components
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        taskTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Name", "Description", "Category", "Start Date", "Deadline", "Status"});
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));

        inputPanel.add(new JLabel("Task:"));
        taskInput = new JTextField();
        inputPanel.add(taskInput);

        inputPanel.add(new JLabel("Description:"));
        descriptionInput = new JTextField();
        inputPanel.add(descriptionInput);

        inputPanel.add(new JLabel("Category:"));
        categoryInput = new JComboBox<>(new String[]{"Personal", "Work", "School", "General"});
        inputPanel.add(categoryInput);

        inputPanel.add(new JLabel("Start Date:"));
        startDateInput = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateInput, "yyyy-MM-dd");
        startDateEditor.getTextField().setEditable(false);
        startDateInput.setEditor(startDateEditor);
        inputPanel.add(startDateInput);

        inputPanel.add(new JLabel("Deadline:"));
        deadlineInput = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deadlineEditor = new JSpinner.DateEditor(deadlineInput, "yyyy-MM-dd");
        deadlineEditor.getTextField().setEditable(false);
        deadlineInput.setEditor(deadlineEditor);
        inputPanel.add(deadlineInput);

        inputPanel.add(new JLabel("Status:"));
        statusInput = new JComboBox<>(new String[]{"Pending", "Done"});
        inputPanel.add(statusInput);

        getContentPane().add(inputPanel, BorderLayout.SOUTH);

        JButton addTaskButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update");
        JButton deleteTaskButton = new JButton("Delete Task");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTaskButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteTaskButton);

        getContentPane().add(buttonPanel, BorderLayout.NORTH);

        addTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTask();
            }
        });

        deleteTaskButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTask();
            }
        });

        taskTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int rowIndex = taskTable.getSelectedRow();
                if (rowIndex != -1) {
                    String taskName = taskTableModel.getValueAt(rowIndex, 0).toString();
                    String description = taskTableModel.getValueAt(rowIndex, 1).toString();
                    String category = taskTableModel.getValueAt(rowIndex, 2).toString();
                    Date startDate = (Date) taskTableModel.getValueAt(rowIndex, 3);
                    Date deadline = (Date) taskTableModel.getValueAt(rowIndex, 4);
                    String status = taskTableModel.getValueAt(rowIndex, 5).toString();

                    taskInput.setText(taskName);
                    descriptionInput.setText(description);
                    categoryInput.setSelectedItem(category);
                    startDateInput.setValue(startDate);
                    deadlineInput.setValue(deadline);
                    statusInput.setSelectedItem(status);
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateTable() {
        String sql = "SELECT * FROM tasktable";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            taskTableModel.setRowCount(0);
            while (resultSet.next()) {
                Object[] rowData = new Object[]{
                        resultSet.getString("taskName"),
                        resultSet.getString("description"),
                        resultSet.getString("category"),
                        resultSet.getDate("dateStarted"),
                        resultSet.getDate("deadline"),
                        resultSet.getString("status")
                };
                taskTableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addTask() {
        String sql = "INSERT INTO tasktable (taskName, description, category, dateStarted, dateFinished, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, taskInput.getText());
            statement.setString(2, descriptionInput.getText());
            statement.setString(3, categoryInput.getSelectedItem().toString());

            java.util.Date startDate = (java.util.Date) startDateInput.getValue();
            java.util.Date deadline = (java.util.Date) deadlineInput.getValue();

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlDeadline = new java.sql.Date(deadline.getTime());

            statement.setDate(4, sqlStartDate);
            statement.setNull(5, Types.DATE); // Set dateFinished to null
            statement.setDate(6, sqlDeadline);
            statement.setString(7, statusInput.getSelectedItem().toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        clearFields();
        updateTable();
    }

    private void updateTask() {
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex != -1) {
            String sql = "UPDATE tasktable SET taskName=?, description=?, category=?, dateStarted=?, deadline=?, status=?, dateFinished=? WHERE taskName=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, taskInput.getText());
                statement.setString(2, descriptionInput.getText());
                statement.setString(3, categoryInput.getSelectedItem().toString());

                java.util.Date startDate = (java.util.Date) startDateInput.getValue();
                java.util.Date deadline = (java.util.Date) deadlineInput.getValue();

                // Convert java.util.Date to java.sql.Date
                java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
                java.sql.Date sqlDeadline = new java.sql.Date(deadline.getTime());

                statement.setDate(4, sqlStartDate);
                statement.setDate(5, sqlDeadline);
                statement.setString(6, statusInput.getSelectedItem().toString());

                // Check if status is "Done" and update the dateFinished column accordingly
                if (statusInput.getSelectedItem().toString().equals("Done")) {
                    statement.setDate(7, new java.sql.Date(System.currentTimeMillis()));
                } else {
                    statement.setNull(7, Types.DATE);
                }

                String taskName = taskTableModel.getValueAt(rowIndex, 0).toString();
                statement.setString(8, taskName);

                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            clearFields();
            updateTable();
        }
    }

    private void deleteTask() {
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex != -1) {
            String taskName = taskTableModel.getValueAt(rowIndex, 0).toString();
            String sql = "DELETE FROM tasktable WHERE taskName=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, taskName);
                statement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            clearFields();
            updateTable();
        }
    }

    private void clearFields() {
        taskInput.setText("");
        descriptionInput.setText("");
        categoryInput.setSelectedIndex(0);
        startDateInput.setValue(new java.util.Date());
        deadlineInput.setValue(new java.util.Date());
        statusInput.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}
