package store;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class SuppliesPanel extends JPanel implements ActionListener{
	
	private static String[] columnNames = {
			"SN", "SUPPLIER NAME", "DATE LAST DELIVERED", "ITEMS", "RECIEVED BY"
	};
	private static String[] quantBundle = {
			"all", "1 - 3", "3 - 10", "More than 10"
	};
	String allRecords = "SELECT s.sName, d.dDate, COUNT(di.ditID) AS products, u.uName "
    		+ "FROM hardware.suppliers s "
    		+ "LEFT JOIN hardware.deliveries d ON s.sID = d.sID "
    		+ "JOIN hardware.users u ON d.uId = u.uId "
    		+ "LEFT JOIN hardware.delivery_items di ON d.dID = di.dID "
    		+ "GROUP BY s.sID ";
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

	private JLabel searchLbl;
	private JPanel tablePanel;
	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn;

	
	
	public SuppliesPanel() {
		setBounds(10, 5, 800, 750);
		setLayout(null);
		
		JLabel tableLbl = new JLabel("SUPPLIES RECIEVED");
		tableLbl.setBounds(250, 5, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tableLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(200, 35, 150, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
	                	String searchSql = "SELECT s.sName, d.dDate, COUNT(di.ditID) AS products, u.uName "
	    			    		+ "FROM hardware.suppliers s "
	    			    		+ "LEFT JOIN hardware.deliveries d ON s.sID = d.sID "
	    			    		+ "JOIN hardware.users u ON d.uId = u.uId "
	    			    		+ "LEFT JOIN hardware.delivery_items di ON d.dID = di.dID "
	    			    		+ "WHERE s.sName LIKE '%"+txt+"%' "
	    			    		+ "GROUP BY s.sID ";
	    				fetchRecords(searchSql);
	                }
	            });
	        }
		});
		add(searchTxt);
		
		searchBtn = new JButton("SEARCH");
		searchBtn.setBounds(400, 35, 100, 30);
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
		
		JScrollPane scrollPane= new JScrollPane(table);
		scrollPane.setBounds(30, 75, 600, 550);
		add(scrollPane);
		
		fetchRecords(allRecords);
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) {
				String searchSql = "SELECT s.sName, d.dDate, COUNT(di.ditID) AS products, u.uName "
			    		+ "FROM hardware.suppliers s "
			    		+ "LEFT JOIN hardware.deliveries d ON s.sID = d.sID "
			    		+ "JOIN hardware.users u ON d.uId = u.uId "
			    		+ "LEFT JOIN hardware.delivery_items di ON d.dID = di.dID "
			    		+ "WHERE s.sName LIKE '%"+txt+"%' "
			    		+ "GROUP BY s.sID ";
				fetchRecords(searchSql);
			}
		} else if(e.getSource() == refreshBtn) {
			StoreDashboard.refresh();
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
	    	
	    	String sName = "", products= "", teller = null; 
	    	int SN = 0;
//	    	float stockOut = 0.0f, Stock = 0f;
	    	Date date = null;
			model = new DefaultTableModel(columnNames, 0);
			table.setModel(model);
			
			TableColumn column = null;
			for (int i = 0; i < 5; i++) {
			    column = table.getColumnModel().getColumn(i);
			    
			    if(i == 0 ) {
			    	column.setMaxWidth(50);
			    }else if(i == 1 | i == 2) {
			    	column.setMaxWidth(180);
			    }else if(i == 5) {
			    	column.setMaxWidth(70);
			    } else {
			    	column.setMaxWidth(100);
			    }
			}

	    	while (resultSet.next()) {
	    		SN = SN+1;
	    		
	    		sName = resultSet.getString("sName");
	    		date = resultSet.getDate("dDate");
	    		products = resultSet.getString("products");
	    		teller = resultSet.getString("uName");
	    		
	    		Object[] record = {SN, sName, date, products, teller };
	    		
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
        } catch (Exception e) {

        }
    }

}
