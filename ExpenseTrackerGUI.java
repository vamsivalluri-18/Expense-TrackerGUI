import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Expense {
    LocalDate date;
    String category;
    double amount;
    String description;

    public Expense(LocalDate date, String category, double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
        return date + " | " + category + " | " + amount + " | " + description;
    }
}

public class ExpenseTrackerGUI extends JFrame {
    private static final String FILE_NAME = "expenses.txt";
    private static final String[] CATEGORIES = {
        "Food", "Travel", "Bills", "Shopping", "Entertainment",
        "Education", "Healthcare", "Rent", "Savings", "Others"
    };

    private JTextField amountField, descriptionField, monthField, yearField;
    private JComboBox<String> categoryBox;
    private JTextArea resultArea;
    private JTable expenseTable;
    private DefaultTableModel tableModel;

    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        amountField = new JTextField();
        descriptionField = new JTextField();
        categoryBox = new JComboBox<>(CATEGORIES);
        JButton addButton = new JButton("Add Expense");

        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Expense"));
        inputPanel.add(new JLabel("Amount (₹):"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout());
        monthField = new JTextField(5);
        yearField = new JTextField(5);
        JButton filterButton = new JButton("View Monthly Expenses");

        filterPanel.setBorder(BorderFactory.createTitledBorder("Monthly View"));
        filterPanel.add(new JLabel("Month (1-12):"));
        filterPanel.add(monthField);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearField);
        filterPanel.add(filterButton);

        // Table
        String[] cols = {"Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(cols, 0);
        expenseTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(expenseTable);

        // Result Area
        resultArea = new JTextArea(3, 40);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Summary"));

        // Events
        addButton.addActionListener(e -> addExpense());
        filterButton.addActionListener(e -> showMonthlyExpenses());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(resultArea, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addExpense() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            String category = categoryBox.getSelectedItem().toString();
            String description = descriptionField.getText().trim();
            LocalDate date = LocalDate.now();

            Expense expense = new Expense(date, category, amount, description);
            try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
                fw.write(expense.toString() + "\n");
            }

            amountField.setText("");
            descriptionField.setText("");
            JOptionPane.showMessageDialog(this, "Expense added successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid amount and description.");
        }
    }

    private void showMonthlyExpenses() {
        int month, year;
        try {
            month = Integer.parseInt(monthField.getText().trim());
            year = Integer.parseInt(yearField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid month and year.");
            return;
        }

        tableModel.setRowCount(0);
        double total = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                if (parts.length == 4) {
                    LocalDate date = LocalDate.parse(parts[0].trim());
                    if (date.getMonthValue() == month && date.getYear() == year) {
                        String category = parts[1].trim();
                        double amount = Double.parseDouble(parts[2].trim());
                        String desc = parts[3].trim();
                        tableModel.addRow(new Object[]{date, category, amount, desc});
                        total += amount;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading expenses.");
        }

        resultArea.setText("Total Expenses in " + month + "/" + year + " = ₹" + String.format("%.2f", total));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerGUI());
    }
}


