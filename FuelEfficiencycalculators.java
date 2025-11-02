import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FuelEfficiencycalculators extends JFrame {
    private JTextField distanceField, fuelField, resultField;
    private JButton calcButton, viewButton, clearButton;
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private Connection conn;

    public FuelEfficiencycalculators() {
        setTitle("⛽ Fuel Efficiency Calculator");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 40));

        // --- Top Title ---
        JLabel titleLabel = new JLabel("Fuel Efficiency Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Calculator Panel (Inputs + Buttons) ---
        JPanel calcPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        calcPanel.setBackground(new Color(45, 45, 60));
        calcPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel distanceLabel = new JLabel("Distance (km):");
        JLabel fuelLabel = new JLabel("Fuel Used (litres):");
        JLabel resultLabel = new JLabel("Efficiency (km/l):");

        distanceLabel.setForeground(Color.WHITE);
        fuelLabel.setForeground(Color.WHITE);
        resultLabel.setForeground(Color.WHITE);

        distanceField = new JTextField();
        fuelField = new JTextField();
        resultField = new JTextField();
        resultField.setEditable(false);

        calcButton = new JButton("Calculate & Save");
        viewButton = new JButton("View Records");
        clearButton = new JButton("Clear");

        styleButton(calcButton);
        styleButton(viewButton);
        styleButton(clearButton);

        calcPanel.add(distanceLabel);
        calcPanel.add(distanceField);
        calcPanel.add(fuelLabel);
        calcPanel.add(fuelField);
        calcPanel.add(resultLabel);
        calcPanel.add(resultField);
        calcPanel.add(calcButton);
        calcPanel.add(clearButton);

        // --- Table Panel (Records + View Button) ---
        String[] columns = {"ID", "Distance (km)", "Fuel (L)", "Efficiency (km/l)", "Date & Time"};
        tableModel = new DefaultTableModel(columns, 0);
        recordTable = new JTable(tableModel);
        recordTable.setRowHeight(28);
        recordTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recordTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        recordTable.getTableHeader().setBackground(new Color(80, 100, 200));
        recordTable.getTableHeader().setForeground(Color.WHITE);
        recordTable.setBackground(new Color(245, 248, 255));

        JScrollPane tableScroll = new JScrollPane(recordTable);
        tableScroll.setBorder(new TitledBorder("Stored Records"));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(30, 30, 40));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 40));
        buttonPanel.add(viewButton);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- SplitPane (Top: Calculator, Bottom: Table) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, calcPanel, tablePanel);
        splitPane.setResizeWeight(0.4); // 40% calculator, 60% table
        splitPane.setDividerSize(6);
        add(splitPane, BorderLayout.CENTER);

        // --- Database Connection ---
        connectDatabase();

        // --- Action Listeners ---
        calcButton.addActionListener(e -> calculateAndSave());
        viewButton.addActionListener(e -> {
            fetchRecords();
            JOptionPane.showMessageDialog(this, "✅ Records Loaded Successfully!");
        });
        clearButton.addActionListener(e -> {
            distanceField.setText("");
            fuelField.setText("");
            resultField.setText("");
        });

        // Auto-load records on start
        fetchRecords();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(80, 100, 200));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    static class RoundedBorder extends LineBorder {
        public RoundedBorder(int radius) {
            super(Color.WHITE, 1, true);
        }
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/fuel_db", "root", "12345");
            System.out.println("✅ Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Database Connection Failed: " + e.getMessage());
        }
    }

    private void calculateAndSave() {
        try {
            double distance = Double.parseDouble(distanceField.getText());
            double fuel = Double.parseDouble(fuelField.getText());

            if (fuel <= 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Fuel must be greater than 0!");
                return;
            }

            double efficiency = distance / fuel;
            resultField.setText(String.format("%.2f", efficiency));

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO fuel_records (distance, fuel, efficiency) VALUES (?, ?, ?)");
            ps.setDouble(1, distance);
            ps.setDouble(2, fuel);
            ps.setDouble(3, efficiency);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Record Saved Successfully!");
            fetchRecords();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Please enter valid numbers!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Database Error: " + ex.getMessage());
        }
    }

    private void fetchRecords() {
        try {
            tableModel.setRowCount(0);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM fuel_records ORDER BY id DESC");

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getDouble("distance"),
                        rs.getDouble("fuel"),
                        rs.getDouble("efficiency"),
                        rs.getTimestamp("date_time")
                };
                tableModel.addRow(row);
            }
            System.out.println("✅ Records fetched successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error Fetching Records: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FuelEfficiencycalculators().setVisible(true));
    }
}
