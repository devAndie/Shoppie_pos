package manager;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class StockPanel extends JPanel implements ActionListener {

	private static String[] columnNames = {
			"CODE", "PRODUCT NAME", "DESCRIPTION", "UNIT", "STOCK OUT",
			"STOCK IN"
	};
	private static String[] quantBundle = {
			"all", "1 - 3", "3 - 10", "More than 10"
	};
	
	String selector = "SELECT s.prId, p.pName, p.pDescription, s.Unit "
    		+ "FROM hardware.products p "
    		+ "RIGHT JOIN hardware.pricing s ON s.pId = p.pId ";
	
	String group = "GROUP BY s.prId ";
	
	String allRecords = selector + group;
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn;



	public StockPanel() {
		setBounds(0, 0, 7500, 750);
		setLayout(null);
		
		JLabel tableLbl = new JLabel("STOCK DETAILS");
		tableLbl.setBounds(250, 5, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tableLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(150, 35, 200, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
	        			
        				String searchSql = selector + "WHERE p.pName LIKE '%"+txt+"%' "
        			    		+ group;
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
		scrollPane.setBounds(20, 75, 700, 550);
		add(scrollPane);
		
		fetchRecords(allRecords);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) {
				String searchSql = selector + "WHERE p.pName LIKE '%"+txt+"%' "
			    		+ group;
				fetchRecords(searchSql);
			}
		} else if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();
		}
		
	}
		
	private void fetchRecords(String query) {
		drawTable();
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	
	    	String pName = "", pDesc= "", pUnit = "", stockOut = "", stockIn = "", Id; 
	    	int SN = 0, prId = 0, itemsNo = 0 , teller = 0;
	    	
	    	
	    	while (resultSet.next()) {
				SN = SN+1;
				Id = resultSet.getString("prId");
				pName = resultSet.getString("pName");
				pDesc = resultSet.getString("pDescription");
				pUnit = resultSet.getString("Unit");
				
				preparedStatement = connect
	                    .prepareStatement("select IFNULL(SUM(ditQuantity), 0) AS stockIn "
	                    		+ "FROM hardware.delivery_items "
	                    		+ "WHERE prId = "+ Id );
				ResultSet rs = preparedStatement.executeQuery();
	    		while (rs.next()) {
	    			stockIn = rs.getString("stockIn");
				}
	    		
	    		preparedStatement = connect
	                    .prepareStatement( "SELECT IFNULL(SUM(itQuantity), 0) AS stockOut "
	                    		+ "FROM hardware.receipt_items WHERE prId = "+ Id );
	    		ResultSet rst = preparedStatement.executeQuery();
	    		while (rst.next()) {
	    			stockOut = rst.getString("stockOut");
	    		}
	    		
				Object[] record = {Id, pName, pDesc, pUnit, stockOut, stockIn};
				
				model.addRow(record);
				model.fireTableDataChanged();
			}
	    		    	
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
            close();
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
		for (int i = 0; i < 6; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(50);
		    	column.setCellRenderer(centerRenderer);  	

		    }else if(i == 1 |i == 2 ) {
		    	column.setMaxWidth(180);
		    } else {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer);  	
		    }
		    
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