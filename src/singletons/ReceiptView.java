package singletons;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import document.GenerateInvoice;
import document.GenerateReceipt;
import models.ReceiptModel;
import models.SaleItem;

public class ReceiptView extends JFrame implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String[] itemColumns= {
			"SN", "ITEM", "QUANTITY", "UNIT", "PRICE", "TOTAL"
			};
    DefaultTableModel itModel;
	JLabel tLbl;
	JTable itTable;
	private JButton printBtn, backBtn;
	ArrayList<SaleItem> items;
	String rId, cName, cContact, total, date, Teller;

	public ReceiptView(ReceiptModel receipt) {
		rId = receipt.getRId();
		cName = receipt.getCName();
		cContact = receipt.getCContact();
		Teller = receipt.getTeller();
		total = receipt.getTotal();
		date = receipt.getDate();
		
		items = new ArrayList<>();
		
		setTitle("View Cash Buys");
		setBounds(300, 100, 550, 500);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		
		JLabel cNLbl = new JLabel("Cash Receipt for: " + cName);
        cNLbl.setBounds(100, 10, 250, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);
		        
        JLabel dLbl = new JLabel("Purchase Date: " + date);
        dLbl.setBounds(30, 50, 200, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);

        tLbl = new JLabel("Receipt Total: KSh " + total);
        tLbl.setBounds(320, 50, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);
        
        itTable = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
	    itTable.setAutoCreateColumnsFromModel(true);
		
		
		JScrollPane scrollPane= new JScrollPane(itTable);
		scrollPane.setBounds(20, 80, 500, 300);
		add(scrollPane);
		
		printBtn = new JButton("Print");
		printBtn.setBounds(350, 420, 100, 30);
		printBtn.addActionListener(this);
		add(printBtn);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(50, 420, 100, 30);
		backBtn.addActionListener(this);
		add(backBtn);
		
		fetchCashItems(rId);
		
		float t= 0;
		for(int j =0; j < items.size(); j++) {
			t = t + Float.valueOf(items.get(j).getTotal());
		}
		total = String.valueOf(t);
		tLbl.setText("Receipt Total: Ksh " + total);
        
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == printBtn) {
			printReceipt();
		} else if (e.getSource() == backBtn) {
			ReceiptView.this.dispose();
		}
		
	}
	
	private void printReceipt() {
		new GenerateReceipt(items, cName, cContact, Teller, date, rId, total, "Receipt");
		
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");

//		if(JOptionPane.showConfirmDialog(null, "Pdf File geneated successfully "
//				+ "\n \n" + "Do you wish to continue to Print Out the document? \n ") == 0) {
//			
//		} else {
//			ReceiptView.this.dispose();
//		}
		
		ReceiptView.this.dispose();

	}

	private void fetchCashItems(String rId) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			resultSet = statement
	                .executeQuery("SELECT p.pId, p.pName, pDescription, itDimens, i.itQuantity, unit, itSellP, i.itTotal, r.rDate, r.cId "
	                		+ "FROM hardware.receipts r "
	                		+ "JOIN hardware.receipt_items i ON r.rId = i.rId "
	                		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
	                		+ "JOIN hardware.products p ON sh.pId = p.pId "
	                		+ "WHERE r.rType = 'cash' "
	                		+ "AND r.rId = '"+ rId +"'");
			drawTable();
			
			String code, pName = null, unit = null,
	    			cId = null, Desc = null, dimens = "", particulars = ""; 
			String quantity, total, price;
			int SN = 0;
	    	
	    	
			while (resultSet.next()) {
				SN = SN +1;
				pName = resultSet.getString("pName");
				code = resultSet.getString("pId");
				dimens = resultSet.getString("itDimens");
				Desc = resultSet.getString("pDescription");
				quantity = resultSet.getString("itQuantity");
				unit = resultSet.getString("unit");
				total = resultSet.getString("itTotal");
				cId = resultSet.getString("cId");
				price = resultSet.getString("itSellP");
				
				if(dimens!=null) {
					particulars = pName + " "+ dimens;					
				} else {
					particulars = pName;
				}
				
				SaleItem item = new SaleItem(
						code, pName, Desc, unit, quantity, dimens, price, total);
				
				items.add(item);

				Object[] record = {SN, particulars, quantity, unit, price, total};
				
				itModel.addRow(record);
				itModel.fireTableDataChanged();
			}
			resultSet = statement
	                .executeQuery("SELECT SUM(rTotal) AS total FROM hardware.receipts "
	                		+ "WHERE rType = 'cash' AND cId = '"+ cId +"'"); 
			while (resultSet.next()) {
				int total1 = resultSet.getInt("total");
				tLbl.setText("Receipts Total: KSh " + total1);
			}
			
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		
	}
	

	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)itTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		
		itModel = new DefaultTableModel(itemColumns, 0);
    	itTable.setModel(itModel);
    	TableColumn col = null;
		for (int i = 0; i < 6; i++) {
		    col = itTable.getColumnModel().getColumn(i);    
		   
		    if(i == 0 ) {
		    	col.setMaxWidth(50);
		    }else if(i == 1) {
		    	col.setMaxWidth(180);
		    } else {
		    	col.setMaxWidth(80);
		    }
		    if(i !=1) {
		    	col.setCellRenderer(centerRenderer);  	
		    }
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
        	e.printStackTrace();
        }
    }
	
}
