package singletons;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.GetRecords;
import api.UpdateData;
import document.GenerateDelivery;
import document.GenerateInvoice;
import models.InvoiceModel;
import models.SaleItem;
import teller.TellerDashboard;

public class InvoiceView extends JFrame implements ActionListener {
    
    private String[] itemColumns= {
			"SN", "ITEM", "QUANTITY", "UNIT", "PRICE", "TOTAL"
			};
    private JTable itTable;
    private JLabel balLbl, tLbl;
	private DefaultTableModel itModel;
	private JButton printBtn, backBtn, genBtn;
	
	
	private String query, cName, cContact, Teller, Date, InvTotal, InvNo, Balance, DocType = "Invoice";
	ArrayList<SaleItem> items;
	
	public InvoiceView(InvoiceModel invoice) {
		InvNo = invoice.getInvNo();
		cName = invoice.getcName();
		cContact = invoice.getcContact();
		Teller = invoice.getTeller();
		Date = invoice.getDate();
		InvTotal = invoice.getInvTotal();
		Balance = invoice.getBalance();
		
		query = "SELECT itId, p.pId, p.pName, p.pDescription, i.itQuantity, unit, "
        		+ "itSellP, i.itTotal, i.itDimens "
        		+ "FROM hardware.receipt_items i "
        		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
        		+ "JOIN hardware.products p ON sh.pId = p.pId "
        		+ "WHERE i.rId = '"+ InvNo +"' ";
		
		setTitle("view invoice");
		setBounds(300, 100, 550, 550);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		JLabel cNLbl = new JLabel("Invoice for: " + cName);
        cNLbl.setBounds(100, 10, 250, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);

        JLabel dLbl = new JLabel("Date: " + Date);
        dLbl.setBounds(350, 10, 150, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);
        
        genBtn = new JButton("Generate Delivery");
        genBtn.setBounds(350, 60, 150, 30);
        genBtn.addActionListener(this);
		add(genBtn);
		
		tLbl = new JLabel("Invoice Total: Ksh " + InvTotal);
        tLbl.setBounds(20, 40, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);

        balLbl = new JLabel("Balance: " + Balance);
        balLbl.setBounds(50, 70, 150, 20);
        balLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(balLbl);

        
        
        itModel = new DefaultTableModel(itemColumns, 0);
		itTable = new JTable(itModel) {
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	        	 
	             return false;
	         }
	       };
		itTable.setAutoCreateColumnsFromModel(true);
		
		
		JScrollPane scrollPane= new JScrollPane(itTable);
		scrollPane.setBounds(20, 100, 500, 350);
		add(scrollPane);
		
		printBtn = new JButton("Print");
		printBtn.setBounds(350, 470, 100, 30);
		printBtn.addActionListener(this);
		add(printBtn);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(50, 470, 100, 30);
		backBtn.addActionListener(this);
		add(backBtn);
		
		items = GetRecords.fetchInvItems(query);

		drawTable();

		float t= 0;
		for(int j =0; j < items.size(); j++) {
			t = t + Float.valueOf(items.get(j).getTotal());
		}
		InvTotal = String.valueOf(t);
		tLbl.setText("Invoice Total: Ksh " + InvTotal);

	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == printBtn) {
			printInvoice();
		} else if (e.getSource() == backBtn) {
			InvoiceView.this.dispose();
		} else if (e.getSource() == genBtn) {
			generateDelivery();
		}
		
	}


	private void generateDelivery() {
		
		new GenerateDelivery(items, cName, cContact, Date, InvNo, "Delivery");
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");
		
		InvoiceView.this.dispose();	
	}


	private void printInvoice() {
		
		new GenerateInvoice(items, cName, cContact, Teller, Date, InvNo, 
				Balance, InvTotal, "Invoice");
		
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");
		
//		if(JOptionPane.showConfirmDialog(null, "Pdf File geneated successfully "
//				+ "\n \n" + "Do you wish to continue to Print Out the document? \n ") == 0) {
//			
//		} else {
//			InvoiceView.this.dispose();		
//		}
		
		InvoiceView.this.dispose();
	}

	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)itTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		TableColumn col = null;
		for (int i = 0; i < 6; i++) {
		    col = itTable.getColumnModel().getColumn(i);    
		   
		    if(i == 0 ) {
		    	col.setMaxWidth(30);
		    }else if(i == 1) {
		    	col.setMaxWidth(200);
		    } else {
		    	col.setMaxWidth(80);
		    }
		    if(i != 1) {
		    	col.setCellRenderer(centerRenderer);  	
		    }
		}
		
		String code, pName = "", unit = "", Desc = "", dimens = "", particulars = "", total, price, quantity; 
    	int SN = 0;
    	
    	SaleItem item; 
    	
    	for(int j =0; j < items.size(); j++) {
			item = items.get(j);
			
			SN = SN +1;
			pName = item.getName();
			dimens = item.getDimens();
			quantity = item.getQuantity();
			unit = item.getUnit();
			price = item.getSellp();
			total = item.getTotal();

			if(dimens!= null ) {
				particulars = pName + " "+ dimens;					
			} else {
				particulars = pName;
			}
			

			Object[] record = {SN, particulars, quantity, unit, price, total};
			itModel.addRow(record);
			itModel.fireTableDataChanged();
			
		}
    	
    	
	}

	

}


