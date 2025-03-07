package view;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import database.DatabaseManager;

public class TransactionLogUI extends JFrame{
	 	private static final String DATABASE_NAME = "budget_app";
	    private static final String TABLE_NAME = "transactions";
	    private JTable table;
	    private DefaultTableModel model;

	    
	    //UI
	    public TransactionLogUI() {
	        setTitle("SQL Database Viewer - " + DATABASE_NAME);
	        setSize(1280,720);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLayout(new BorderLayout());

	        model = new DefaultTableModel();
	        table = new JTable(model);
	        JScrollPane scrollPane = new JScrollPane(table);
	        add(scrollPane, BorderLayout.CENTER);

	        loadData();
	        setVisible(true);
	    }

	    //Fetching Data from Database
	    private void loadData() {
	        try (Connection connection = getDatabaseConnection();
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME)) {

	            ResultSetMetaData metaData = resultSet.getMetaData();
	            int columnCount = metaData.getColumnCount();
	            String[] columnNames = new String[columnCount];

	            for (int i = 1; i <= columnCount; i++) {
	                columnNames[i - 1] = metaData.getColumnName(i);
	            }

	            model.setColumnIdentifiers(columnNames);

	            while (resultSet.next()) {
	                Object[] rowData = new Object[columnCount];
	                for (int i = 1; i <= columnCount; i++) {
	                    rowData[i - 1] = resultSet.getObject(i);
	                }
	                model.addRow(rowData);
	            }

	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
	                    "Database Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }

	    private Connection getDatabaseConnection() throws SQLException {
	    	 String URL = "jdbc:postgresql://localhost:5432/Projecttest"; // change if needed
	         String USER = "andychan";
	         String PASSWORD = "";

	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found!", "Driver Error", JOptionPane.ERROR_MESSAGE);
	            throw new SQLException("Driver not found", e);
	        }

	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(TransactionLogUI::new);
	    }
	}

