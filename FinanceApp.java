import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Transaction {
    String type;
    double amount;
    String description;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public String toString() {
        return type + ": $" + amount + " - " + description;
    }
}

public class FinanceApp {
    private JFrame frame;
    private JTextField amountField, descField;
    private JComboBox<String> typeBox;
    private JTextArea historyArea;
    private JLabel balanceLabel;
    private double balance = 0.0;
    private final String FILE_NAME = "transactions.txt";
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public FinanceApp() {
        frame = new JFrame("Personal Finance Tracker");
        amountField = new JTextField();
        descField = new JTextField();
        typeBox = new JComboBox<>(new String[]{"Income", "Expense"});
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        balanceLabel = new JLabel("Balance: $0.00");

        JButton addButton = new JButton("Add Transaction");
        addButton.addActionListener(e -> addTransaction());

        loadTransactions();
        updateHistory();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descField);
        inputPanel.add(addButton);
        inputPanel.add(balanceLabel);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addTransaction() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String description = descField.getText();
            String type = (String) typeBox.getSelectedItem();

            if (type.equals("Expense")) amount = -amount;

            Transaction t = new Transaction(type, Math.abs(amount), description);
            transactions.add(t);
            balance += amount;
            saveTransactions();
            updateHistory();

            amountField.setText("");
            descField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Enter a valid number.");
        }
    }

    private void updateHistory() {
        historyArea.setText("");
        for (Transaction t : transactions) {
            historyArea.append(t.toString() + "\n");
        }
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }

    private void saveTransactions() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Transaction t : transactions) {
                out.println(t.type + "," + t.amount + "," + t.description);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String type = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String desc = parts[2];
                    Transaction t = new Transaction(type, amount, desc);
                    transactions.add(t);
                    if (type.equals("Expense")) balance -= amount;
                    else balance += amount;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinanceApp::new);
    }
}