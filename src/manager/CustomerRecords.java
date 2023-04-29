package manager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import singletons.CustomerView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerRecords extends JPanel implements ActionListener {
	
	private static String[] columnNames = {
			"SN", "ID", "CUSTOMER NAME", "VISITS", "ITEMS", "TOTAL", "CASH BUYS",
			"INVOICES", "BALANCE", "LAST VISIT"
	};
	private String allRecords = "SELECT c.cId, c.cName, r.rDate, COUNT(r.rId) AS visits, SUM(r.rTotal) AS total "
    		+ "FROM hardware.customers c JOIN hardware.receipts r ON c.cId = r.cId "
    		+ "GROUP BY c.cId ORDER BY r.rDate DESC ";
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	
	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn;
	
	
	public CustomerRecords(){
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		JLabel tLbl = new JLabel("CUSTOMER RECORDS");
		tLbl.setBounds(250, 10, 200, 20);
		tLbl.setFont(new Font("Dialog", Font.BOLD, 16));
		add(tLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(200, 55, 270, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
        			
        				String searchSql = "SELECT c.cId, c.cName, r.rDate, COUNT(r.rId) AS visits, SUM(r.rTotal) AS total "
    	                		+ "FROM hardware.customers c JOIN hardware.receipts r ON c.cId = r.cId "
    	                		+ "WHERE c.cName LIKE '%"+ txt +"%'"
    	                		+ "GROUP BY c.cId ORDER BY r.rDate DESC ";
        				fetchRecords(searchSql);
	                }
	            });
	        }
		});
		add(searchTxt);
		
		searchBtn = new JButton("SEARCH");
		searchBtn.setBounds(300, 55, 100, 30);
		searchBtn.addActionListener(this);
		add(searchBtn);
		
		refreshBtn = new JButton("Refresh");
		refreshBtn.setBounds(680, 35, 70, 30);
		refreshBtn.addActionListener(this);
		add(refreshBtn);
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
		
		table.setAutoCreateColumnsFromModel(true);
		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect doble click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               int col = target.getSelectedColumn();
	               if(col ==6) {
	            	   showCashReceipts(row);
	               } else if (col == 7) {
	            	   listInvoice(row); 
	               } else {
	            	   showSummary(row);
	               }
	            }
	         }	
	      });
		
		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(20, 100, 750, 550);
		add(tablePane);
				
		fetchRecords(allRecords);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) { 
				String searchSql = "SELECT c.cId, c.cName, r.rDate, COUNT(r.rId) AS visits, SUM(r.rTotal) AS total "
                		+ "FROM hardware.customers c JOIN hardware.receipts r ON c.cId = r.cId "
                		+ "WHERE c.cName LIKE '%"+ txt +"%'"
                		+ "GROUP BY c.cId ORDER BY r.rDate DESC ";
				fetchRecords(searchSql);
			}
		} else if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();
		}
	}
	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
    	model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		TableColumn column = null;
		for (int i = 0; i < 10; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(30);
		    	column.setCellRenderer(centerRenderer);
		    } else if(i == 1) {
		    	column.setMaxWidth(50);
		    	column.setCellRenderer(centerRenderer);
		    } else if(i == 2) {
		    	column.setMaxWidth(200);
		    } else {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer);
		    }
		}
	}
	
	protected void showSummary(int row) {
		String cId, cName, visits, items, total, balance, date, cashs, invoices;
		
		cId = model.getValueAt(row, 1).toString();
		cName = model.getValueAt(row, 2).toString();
		visits = model.getValueAt(row, 3).toString();
		items = model.getValueAt(row, 4).toString();
		total = model.getValueAt(row, 5).toString();
		cashs = model.getValueAt(row, 6).toString(); 
		invoices = model.getValueAt(row, 7).toString();
		balance = model.getValueAt(row, 8).toString();
		date = model.getValueAt(row, 9).toString();
		
		new CustomerView(cId, cName, visits, items, total, balance, date, cashs, invoices);
			
	}
	JLabel tLbl;
	JTable itTable;
	protected void listInvoice(int row) {
		String cId, cName, total = "0", balance, date, invoices;
		cId = model.getValueAt(row, 1).toString();
		cName = model.getValueAt(row, 2).toString();
		total = model.getValueAt(row, 5).toString();
		invoices = model.getValueAt(row, 7).toString();
		balance = model.getValueAt(row, 8).toString();
		date = model.getValueAt(row, 9).toString();
		
		if(invoices.equalsIgnoreCase("0")) {
			total = "0";
			
		} else {
			new ListInvoices(cId, cName, total, balance, date, invoices);			
		}
		
	}

	protected void showCashReceipts(int row) {
		String cId, cName, total = "0", date, cashs;
		cId = model.getValueAt(row, 1).toString();
		cName = model.getValueAt(row, 2).toString();
		cashs = model.getValueAt(row, 6).toString(); 
		total = model.getValueAt(row, 5).toString();
		date = model.getValueAt(row, 9).toString();
		
		if(cashs.equalsIgnoreCase("0")) {
			total = "0";
		} else {
			new ListReceipts(cId, cName, total, date, cashs);
		}
		
		
	}
	
	private void fetchRecords(String query) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	
	    	String cName = "", c_code = "";
	    	int SN = 0, visits = 0, itemsNo = 0, cash = 0 , invoices = 0 ;
	    	float total = 0.0f, bal = 0;
	    	
	    	drawTable();
	    	while (resultSet.next()) {
	    		SN = SN+1;
	    		c_code = resultSet.getString("cId");
	    		cName = resultSet.getString("cName");
	    		visits = resultSet.getInt("visits");
	    		total = resultSet.getInt("total");
	    		Date date = resultSet.getDate("rDate");
	    		
	    		preparedStatement = connect
	                    .prepareStatement("SELECT COUNT(i.itId) AS items "
				        		+ "FROM hardware.receipt_items i "
				        		+ "WHERE rId = ( SELECT rId FROM hardware.receipts "
				        		+ "WHERE cId = "+ c_code +" group by cId)");
	    		ResultSet rs = preparedStatement.executeQuery();
	    		while (rs.next()) {
	    			itemsNo = rs.getInt("items");
				}
	    		preparedStatement = connect
	                    .prepareStatement("SELECT COUNT(rId) AS cash "
	                    		+ "FROM hardware.receipts "
	                    		+ "WHERE rType = 'cash' AND  cId = '"+ c_code +"'");
	    		ResultSet rs2 = preparedStatement.executeQuery();
	    		while (rs2.next()) {

		    		cash = rs2.getInt("cash");
	    		}
	    		preparedStatement = connect
	                    .prepareStatement("SELECT COUNT(rId) AS invoices, rBal "
	                    		+ "FROM hardware.receipts "
	                    		+ "WHERE rType = 'invoice'  AND  cId = '"+ c_code +"'");
	    		ResultSet rs3 = preparedStatement.executeQuery();
	    		while (rs3.next()) {

		    		invoices = rs3.getInt("invoices");
		    		bal = rs3.getInt("rBal");
	    		}
	    		
	    		Object[] record = {SN, c_code, cName, visits, itemsNo, total, cash, invoices, bal, date};
	    		
	    		model.addRow(record);
				model.fireTableDataChanged();
	    	}
	    	
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
	}
	
	void refresh() {
		fetchRecords(allRecords);
	}
	
	private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
            if (preparedStatement != null) {
            	preparedStatement.close();
            }
        } catch (Exception e) {

        }
    }

		
}
