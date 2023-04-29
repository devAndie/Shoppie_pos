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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import api.GetData;
import api.GetRecords;
import api.ProductData;
import api.UpdateData;
import document.GenerateDelivery;
import document.GenerateInvoice;
import manager.ManagerDashboard;
import models.InvoiceModel;
import models.Pricing;
import models.Product;
import models.SaleItem;

public class InvoiceViewManager extends JFrame implements ActionListener {
    
    private String[] itemColumns= {
			"SN", "ITEM", "QUANTITY", "UNIT", "PRICE", "TOTAL", " "
			};
    private JLabel tLbl, balLbl ;
    private JTable itTable;
	JScrollPane scrollPane;
	private DefaultTableModel itModel;
	private JButton printBtn, backBtn, genBtn, delBtn;
	
	private String query, cName, cContact, Teller, Date, InvTotal, InvNo, Balance, DocType = "Invoice";
	ArrayList<SaleItem> items;
	
	public InvoiceViewManager(InvoiceModel invoice) {
		InvNo = invoice.getInvNo();
		cName = invoice.getcName();
		cContact = invoice.getcContact();
		Teller = invoice.getTeller();
		Date = invoice.getDate();
		InvTotal = invoice.getInvTotal();
		Balance = invoice.getBalance();
		
		items = new ArrayList<>();
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
                
        tLbl = new JLabel("Invoice Total: Ksh " + InvTotal);
        tLbl.setBounds(20, 40, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);

        balLbl = new JLabel("Balance: KSh " + Balance);
        balLbl.setBounds(50, 70, 150, 20);
        balLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(balLbl);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 100, 500, 350);
		add(scrollPane);
		
        genBtn = new JButton("Generate Delivery");
        genBtn.setBounds(350, 60, 150, 30);
        genBtn.addActionListener(this);
		add(genBtn);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(20, 470, 100, 30);
		backBtn.addActionListener(this);
		add(backBtn);

		delBtn = new JButton("Delete");
		delBtn.setBounds(210, 470, 100, 30);
		delBtn.addActionListener(this);
		add(delBtn);
		
		printBtn = new JButton("Print");
		printBtn.setBounds(420, 470, 100, 30);
		printBtn.addActionListener(this);
		add(printBtn);
		
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
			InvoiceViewManager.this.dispose();
			
		} else if (e.getSource() == genBtn) {
			generateDelivery();
			
		} else if (e.getSource() == delBtn) {
			deleteInvoice();
		}
		
	}


	private void productEdit(String itID) {
		ProductData.getProductDetails(itID);
		Product p = ProductData.getProduct();
		Pricing pr = ProductData.getPricing();
		
		String Pid, Name, Desc, Unit, SellP;
		Pid = String.valueOf(p.getID());
		Name = p.getName();
		Desc = p.getDesc();
		Unit = pr.getUnit();
		SellP = String.valueOf(pr.getPrice());
		
		new ProductView(Pid, Name, Desc, Unit, SellP);
		
	}

	private void generateDelivery() {
		
		new GenerateDelivery(items, cName, cContact, Date, InvNo, "Delivery");
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");
		
		InvoiceViewManager.this.dispose();	
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
		
		InvoiceViewManager.this.dispose();
	}
	
	public void deleteInvoice(){
		
		if(JOptionPane.showConfirmDialog(null, "You are about to delete this Record"
				+ "\n \n" + "Do you wish to continue? \n ") == 0) {

			UpdateData.deleteReceipt(InvNo);
			
			InvoiceViewManager.this.dispose();

			JOptionPane.showMessageDialog(null, "Record Deleted successfully ");

			ManagerDashboard.refresh();
		}
		
	}

	private void drawTable() {
		itModel = new DefaultTableModel(itemColumns, 0) {
			boolean[] canEdit = new boolean[]{
                    false, false, true, false, true, false, false
            };
			
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
		};
		
		itTable = new JTable(itModel);
		
		
		itTable.setAutoCreateColumnsFromModel(true);
		scrollPane.setViewportView(itTable);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)itTable.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		TableColumn col = null;
		for (int i = 0; i < 7; i++) {
		    col = itTable.getColumnModel().getColumn(i);    
		   
		    if(i == 0 ) {
		    	col.setMaxWidth(30);
		    } else if(i == 1) {
		    	col.setMaxWidth(200);
		    } else if(i == 6) {
			  col.setMaxWidth(50);  
		    } else {
		    	col.setMaxWidth(80);
		    }
		    if(i != 1) {
		    	col.setCellRenderer(centerRenderer);  	
		    }
		}
		paintTable();
		
		itTable.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	        	 float remTotal = 0;
	        	 
	            if (me.getClickCount() == 2) {     // to detect double click events
	               int row = itTable.getSelectedRow(); // select a row
	               int col = itTable.getSelectedColumn();
	               
	               String itID = items.get(row).getCode();
            	   
	               if (col == 1) {
	            	   productEdit(itID);
	            	   
	               } else if(col == 6) { 
	            	   remTotal = Float.valueOf(itModel.getValueAt(row, 5).toString());
	            	   
	            	   UpdateData.removeItem(itID);
//	            	   calcInvTotal(remTotal);
	            	   
	            	   itModel.removeRow(row);
	            	   items.remove(row);
//	            	   itModel.fireTableDataChanged();
	            	   
	               }
	            }
	         }	
	      });
		
		itModel.addTableModelListener(new TableModelListener() {
			@Override
			  public void tableChanged(TableModelEvent e) {
				
				int col = e.getColumn();
				int row = e.getFirstRow();
				String itID = items.get(row).getCode();
				
				if(row <= itModel.getRowCount() ) {
					String quantity = itModel.getValueAt(row, 2).toString();  
					String price = itModel.getValueAt(row, 4).toString();

					float sellP =  Float.valueOf(price);
					float quant = Float.valueOf(quantity);
					
					
					float total = quant * sellP;
					
					if (col == 2) {
//						update quantity

						UpdateData.updateQuantity(itID, quantity, String.valueOf(total));
						
						itModel.setValueAt(total, row, 5);

						
					} else if (col == 4) {
//						update price
						UpdateData.updatePrice(itID, price, String.valueOf(total));
						
						itModel.setValueAt(total, row, 5);
						
					} 
					
				}

				
				//update receipt total
				calcInvTotal(0);
				
			  }

			});

		
	}
	
	public void calcInvTotal(float remTotal) {
		float Total = 0;
		
		for(int j = 0; j < itModel.getRowCount(); j++) {
			Total = Total + Float.valueOf(itModel.getValueAt(j, 5).toString());								        	
		}
		
		float invTotal, bal, diff;
		invTotal = Float.valueOf(InvTotal);
		bal = Float.valueOf(Balance);
		
		// account for changes
		Total = Total - remTotal;
		
		if(Total > invTotal) {
			diff = Total - invTotal;        
			bal = bal + diff;
			
		} else {
			diff = invTotal - Total;
			bal = bal - diff;
			
		}

		InvTotal = String.valueOf(Total);
		Balance = String.valueOf(bal);

		UpdateData.updateReceiptTotal(InvTotal, Balance, InvNo);
		
		
		tLbl.setText("Invoice Total: KSh " + InvTotal);
		balLbl.setText("Balance: KSh " + Balance);

		ManagerDashboard.refresh();

	}
	
	private void paintTable() {

		String code, pName = "", unit = "", Desc = "", dimens = "", particulars = ""; 
		String total, price, quantity;
    	
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
			

			Object[] record = {SN, particulars, quantity, unit, price, total, "-x-"};
			itModel.addRow(record);
			itModel.fireTableDataChanged();
			
		}

	}

	

}


