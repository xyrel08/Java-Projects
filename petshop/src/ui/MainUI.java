package ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import database.DatabaseManager;
import model.Task;

public class MainUI extends JFrame {

    private JTable taskTable;
    private DefaultTableModel taskTableModel;
    private JTextField taskInput;
    private JTextField descriptionInput;
    private JComboBox<String> categoryInput;
    private JSpinner startDateInput;
    private JSpinner deadlineInput;
    public JComboBox<String> statusInput;
    private DatabaseManager databaseManager;

    public MainUI() {
        databaseManager = new DatabaseManager();
        initializeUI();
        updateTable();
    }

    private void initializeUI() {
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        taskTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Name", "Description", "Category", "Start Date", "Deadline", "Status"});
        taskTable = new JTable(taskTableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        getContentPane().add(scrollPane, BorderLayout.SOUTH);

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

        getContentPane().add(inputPanel, BorderLayout.NORTH);

        JButton addTaskButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update");
        JButton deleteTaskButton = new JButton("Delete Task");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTaskButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteTaskButton);

        getContentPane().add(buttonPanel, BorderLayout.CENTER);

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
                    String startDate = taskTableModel.getValueAt(rowIndex, 3).toString();
                    String deadline = taskTableModel.getValueAt(rowIndex, 4).toString();
                    String status = taskTableModel.getValueAt(rowIndex, 5).toString();

                    taskInput.setText(taskName);
                    descriptionInput.setText(description);
                    categoryInput.setSelectedItem(category);
                    startDateInput.setValue(java.sql.Date.valueOf(startDate));
                    deadlineInput.setValue(java.sql.Date.valueOf(deadline));
                    statusInput.setSelectedItem(status);
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void updateTable() {
        java.util.List<Task> tasks = databaseManager.getAllTasks();
        taskTableModel.setRowCount(0);
        for (Task task : tasks) {
            Object[] rowData = new Object[]{
                    task.getTaskName(),
                    task.getDescription(),
                    task.getCategory(),
                    task.getStartDate(),
                    task.getDeadline(),
                    task.getStatus()
            };
            taskTableModel.addRow(rowData);
        }
    }

    private void addTask() {
        String taskName = taskInput.getText();
        String description = descriptionInput.getText();
        String category = categoryInput.getSelectedItem().toString();
        String startDate = startDateInput.getValue().toString();
        String deadline = deadlineInput.getValue().toString();
        String status = statusInput.getSelectedItem().toString();

        Task task = new Task(taskName, description, category, startDate, deadline, status);
        databaseManager.addTask(task);

        clearFields();
        updateTable();
    }

    private void updateTask() {
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex != -1) {
            String taskName = taskTableModel.getValueAt(rowIndex, 0).toString();
            String newTaskName = taskInput.getText();
            String description = descriptionInput.getText();
            String category = categoryInput.getSelectedItem().toString();
            String startDate = startDateInput.getValue().toString();
            String deadline = deadlineInput.getValue().toString();
            String status = statusInput.getSelectedItem().toString();

            Task task = new Task(newTaskName, description, category, startDate, deadline, status);
            databaseManager.updateTask(task, taskName);

            clearFields();
            updateTable();
        }
    }

    private void deleteTask() {
        int rowIndex = taskTable.getSelectedRow();
        if (rowIndex != -1) {
            String taskName = taskTableModel.getValueAt(rowIndex, 0).toString();
            databaseManager.deleteTask(taskName);

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

    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainUI();
            }
        });
    }
}
