package manager;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import models.ReceiptModel;
import singletons.ReceiptView;

public class ListReceipts extends JFrame{
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private static String[] columnNames = {
    		"SN", "REC_NO", "ITEMS", "TOTAL", "COUNTER", "DATE"
			};
	
	private DefaultTableModel itModel;
    
	JLabel tLbl;
	JTable itTable;
	ArrayList<ReceiptModel> receipts;

	String cId, cName, total, date, receiptsNo, Teller;

	public ListReceipts(String id, String name, String Total, String Date, String ReceiptsNo) {
		this.cId = id;
		this.cName = name;
		this.total = Total;
		this.date = Date;
		this.receiptsNo = ReceiptsNo;

		receipts = new ArrayList<>();
		
		setTitle("Customer Cash receipts");
		setBounds(250, 100, 550, 500);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		
		JLabel cNLbl = new JLabel("Cash Receipts for: " + cName);
        cNLbl.setBounds(150, 10, 200, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);
		
        tLbl = new JLabel("Customer Total: KSh " + total);
        tLbl.setBounds(320, 70, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);
        
        JLabel dLbl = new JLabel("Last Purchase: " + date);
        dLbl.setBounds(50, 70, 200, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);
        
        itTable = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
	    itTable.setAutoCreateColumnsFromModel(true);
	    itTable.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row    

	               new ReceiptView(receipts.get(row));
	            }
	         }	
	      });
		
		JScrollPane scrollPane= new JScrollPane(itTable);
		scrollPane.setBounds(20, 100, 500, 350);
		add(scrollPane);
		
		
		fetchCashItems(cId);
	}
	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)itTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		itModel = new DefaultTableModel(columnNames, 0);
    	itTable.setModel(itModel);
		TableColumn column = null;
		for (int i = 0; i < 6; i++) {
		    column = itTable.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(30);
		    } else if(i == 1) {
		    	column.setMaxWidth(60);
		    } else {
		    	column.setMaxWidth(100);
		    }
		    column.setCellRenderer(centerRenderer);
		}
	}
	
	
	private void fetchCashItems(String cId) {
		String allRecords = 
				"SELECT r.rId, c.cContact, count(i.itId) AS items, SUM(r.rTotal) AS bill, r.uId, r.rDate "
				+ "FROM hardware.receipts r "
				+ "LEFT JOIN hardware.customers c ON c.cId = r.cId "
				+ "JOIN hardware.receipt_items i ON i.rId = r.rId "
				+ "WHERE rType = 'cash' "
				+ "AND r.cId = '"+ cId +"' "
				+ "group by r.rId  ORDER BY r.rId DESC ";
		
		String receiptTotal = "SELECT SUM(rTotal) AS total FROM hardware.receipts "
        		+ "WHERE  rType = 'cash' AND cId = '"+ cId +"' ";
		
		drawTable();		
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			resultSet = statement
	                .executeQuery(allRecords);
			
			int SN = 0, counter, items;
			String code = "", total ="", teller = "", cContact = "", date ="";
	    	
	    	while (resultSet.next()) {
				SN = SN +1;
				code = resultSet.getString("r.rId");
				cContact = resultSet.getString("cContact");
				items = resultSet.getInt("items");
				total = resultSet.getString("bill");
				counter = resultSet.getInt("uId");
				date = resultSet.getString("rDate");
				
				preparedStatement = connect
	                    .prepareStatement( "SELECT uName FROM hardware.users WHERE uID = "+ counter);
				
				ResultSet rs = preparedStatement.executeQuery();
	    		while (rs.next()) {
	    			teller = rs.getString("uName");
				}
	    		rs.close();


	    		ReceiptModel r = new ReceiptModel(code, cName, cContact, total, teller, date);
				receipts.add(r );
				
				Object[] record = {SN, code, items, total, teller, date};
				
				itModel.addRow(record);
				itModel.fireTableDataChanged();
			}
			
			resultSet = statement
	                .executeQuery(receiptTotal);

			while (resultSet.next()) {
				int RctTotal = resultSet.getInt("total");
				tLbl.setText("Receipts Total: KSh " + RctTotal);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		
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
