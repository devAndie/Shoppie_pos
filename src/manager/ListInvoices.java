package manager;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import api.CustomerData;
import api.GetData;
import api.GetRecords;
import document.GenerateInvoice;
import models.CheckBoxRenderer;
import models.Customer;
import models.InvoiceModel;
import singletons.InvoiceView;
import singletons.InvoiceViewManager;
import models.SaleItem;
import teller.CheckOut;

public class ListInvoices extends JFrame implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String[] columns = {
    		"INV_NO", "ITEMS", "TOTAL", "BALANCE", "TELLER", "DATE"
    };
	private DefaultTableModel itModel;
	ArrayList<InvoiceModel> invoices;
	JLabel tLbl;
	JButton mgPrintBtn;
	JTable itTable;
	String cId, cName, total, balance, date, invoicesNo;

	public ListInvoices(String id, String name, String Total, String Bal, String Date, String InvoicesNo) {
		cId = id;			cName = name;
		total = Total;		balance = Bal;
		date = Date;		invoicesNo = InvoicesNo;
		
		invoices = new ArrayList<>();
		
		setTitle("Customer invoices");
		setBounds(250, 100, 550, 500);
		setResizable(false);
		setVisible(true);	setLayout(null);
				
		JLabel cNLbl = new JLabel("Invoices for: " + cName);
        cNLbl.setBounds(150, 10, 150, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);
		
        tLbl = new JLabel("Invoices Total: KSh " + Total);
        tLbl.setBounds(320, 30, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);
        
        JLabel balLbl = new JLabel("Balance Due: KSh " + balance);
        balLbl.setBounds(320, 60, 200, 20);
        balLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(balLbl);
        
        JLabel dLbl = new JLabel("Last Visit Date: " + date);
        dLbl.setBounds(50, 60, 200, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);
        
		fetchInvList(cId);	
		
		JScrollPane scrollPane= new JScrollPane(itTable);
		scrollPane.setBounds(20, 85, 490, 300);
		add(scrollPane);
		
		mgPrintBtn = new JButton("Merge & Print");
		mgPrintBtn.setBounds(400, 410, 100, 30);
		mgPrintBtn.addActionListener(this);
		add(mgPrintBtn);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mgPrintBtn) {
			mergePrint();
		}

	}
	
	private void fetchInvList(String cId) {
		
		drawTable();

		String allRecords = "SELECT r.rId, count(i.rId) AS items, c.cContact, "
				+ "r.rTotal, r.rBal AS balance, r.uID, r.rDate "
	    		+ "FROM hardware.receipts r "
	    		+ "LEFT JOIN hardware.customers c ON c.cId = r.cId "
	    		+ "JOIN hardware.receipt_items i ON i.rId = r.rId "
	    		+ "WHERE rType = 'invoice' "
	    		+ "AND r.cId ='"+ cId +"' "
	    		+ "group by i.rId  ORDER BY r.rId DESC ";
		
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			resultSet = statement
	                .executeQuery(allRecords);
	    	
			String payment = "", status = "", 
					teller = "", cContact = "", invNo = "";
	    	int SN = 0, itemsNo = 0, counter = 0;
	    	String total = "", bal = "", date = null;
	    	
			while (resultSet.next()) {
	    		int i = 0;
	    		
	    		invNo = resultSet.getString("rId");
	    		cContact = resultSet.getString("cContact");
	    		itemsNo = resultSet.getInt("items");
	    		
	    		total = resultSet.getString("rTotal");
	    		counter = resultSet.getInt("uID");
	    		bal = resultSet.getString("balance");
	    		date = resultSet.getString("rDate");
	    		
	    		preparedStatement = connect
	                    .prepareStatement( "SELECT uName FROM hardware.users "
	                    		+ "WHERE uID = "+ counter);
				
				ResultSet rs = preparedStatement.executeQuery();
	    		while (rs.next()) {
	    			teller = rs.getString("uName");
				}
	    		rs.close();
	    		
	    		InvoiceModel inv = new InvoiceModel(invNo, cName, cContact, total, 
	    				bal, teller, date);
				invoices.add(inv );
	    		
				boolean print = false;
				
				Object[] record = {invNo, itemsNo, total, bal, teller, date,  Boolean.FALSE };
				
				
	    		itModel.addRow(record);
	    		itModel.fireTableDataChanged();
				
				i ++;
	    	}	
			
			resultSet = statement
	                .executeQuery("SELECT SUM(rTotal) AS total FROM hardware.receipts "
	                		+ "WHERE rType = 'invoice'  "
	                		+ "AND cId = '"+ cId +"'"); 
			while (resultSet.next()) {
				float invTotal = resultSet.getFloat("total");
				tLbl.setText("Invoices Total: KSh " + invTotal);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
        
	}

	private void drawTable() {
//		CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();

		itModel = new DefaultTableModel(columns, 0);
		
        itTable = new JTable(itModel){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {	
	        	 return false;
	             
	         } 
	    };
	    
//   	 itTable.getColumnModel().getColumn(6).setCellRenderer(checkBoxRenderer);

	    itTable.setRowSelectionAllowed(true);
	    itTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    itTable.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect doble click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               
	               new InvoiceViewManager(invoices.get(row));
	            }
	         }	
	       }
	    );
	    
	    
	    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)itTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
//		itTable.getColumnModel().getColumn(6).setCellRenderer(checkBoxRenderer);

//    	itTable.setModel(itModel);
    	
		TableColumn col = null;
		for (int i = 0; i < 6; i++) {
		    col = itTable.getColumnModel().getColumn(i);    
		   
		    if(i == 0 ) {
		    	col.setMaxWidth(50);
		    	
		    }  else {
		    	col.setMaxWidth(100);
		    }
		    col.setCellRenderer(centerRenderer);  	
		}
	}
	
	public void mergePrint() {

		String Teller = null, Date = null, InvNo = null, invIDs = "";
		
		float Total = 0, Balance = 0;
		
		int[] invs = itTable.getSelectedRows();
		
		if(invs.length >= 1) {
			
			for(int i =0; i < invs.length; i++) {
				int row = invs[i];
				
				String id = itModel.getValueAt(row, 0).toString();
				String tel = itModel.getValueAt(row, 4).toString();
				String d = itModel.getValueAt(row, 5).toString();
				float t = Float.valueOf(itModel.getValueAt(row, 2).toString());
				float b = Float.valueOf(itModel.getValueAt(row, 3).toString());
				
				if(i == 0) {
					InvNo = id;
					
					Total = t;
					Balance = b;
					
					Teller = tel;
					Date = d;
					
					invIDs = "rId = '"+ id +"' ";
					
				} else {
					InvNo = InvNo + ", "+id;
					
					Total = Total + t;
					Balance = Balance + b;
					
					invIDs  = invIDs + "OR rId = '" + id + "' ";					
					
				}
			}
			String query = "SELECT itId, p.pId, p.pName, p.pDescription, i.itQuantity, unit, "
	        		+ "itSellP, i.itTotal, i.itDimens "
	        		+ "FROM hardware.receipt_items i "
	        		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
	        		+ "JOIN hardware.products p ON sh.pId = p.pId "
	        		+ "WHERE " + invIDs ;

			
			ArrayList<SaleItem> items = GetRecords.fetchInvItems(query);
			Customer c = CustomerData.getCustomer(cId);
			String cContact = c.getContact();
			
			String InvBal = String.valueOf(Balance);
			String InvTotal = String.valueOf(Total);
			
			new GenerateInvoice(items, cName, cContact, Teller, Date, InvNo, 
					InvBal, InvTotal, "Invoice");
			
			JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");
			
		} else {
			JOptionPane.showMessageDialog(ListInvoices.this, "Select two or more Invoices to merge");	
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
