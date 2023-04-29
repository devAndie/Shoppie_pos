package singletons;

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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.GetData;
import api.GetRecords;
import api.ProductData;
import api.UpdateData;
import document.GenerateInvoice;
import document.GenerateQuotation;
import manager.ManagerDashboard;
import models.Pricing;
import models.Product;
import models.QuoteModel;
import models.SaleItem;
import teller.TellerDashboard;

public class QuoteViewManager extends JFrame implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    private String[] itemColumns= {
    		"QUANTITY", "DESCRIPTION", "UNIT", "PRICE", "TOTAL"
			};
    JLabel tLbl;
    private JTable table;
	private DefaultTableModel model;
	private JButton printBtn, backBtn, toInvBtn, delBtn;
	
	String QId,  CName, CContact, Project, Date, Total, Teller, Invoiced;
	ArrayList<SaleItem> items;
	
	
    public QuoteViewManager(QuoteModel quote) {
    	QId = quote.getQId();
    	CName = quote.getCName();
    	CContact = quote.getCContact();
    	Project = quote.getProject();
    	Date = quote.getDate();
    	Total = quote.getTotal();
    	Teller  = quote.getTeller();
    	Invoiced = quote.getInvoiced();
    	
    	items = new ArrayList<>();
		
    	setTitle("View Quote");
		setBounds(300, 100, 550, 550);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
    	JLabel cNLbl = new JLabel("Quote for: " + CName);
        cNLbl.setBounds(100, 10, 250, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);
        
        JLabel dLbl = new JLabel("Date: " + Date);
        dLbl.setBounds(50, 45, 150, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);

        tLbl = new JLabel("Quote Total: Ksh " + Total);
        tLbl.setBounds(50, 75, 200, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(tLbl);

        
        table = new JTable();
	    table.setAutoCreateColumnsFromModel(true);
		
		
		JScrollPane scrollPane= new JScrollPane(table);
		scrollPane.setBounds(20, 100, 500, 350);
		add(scrollPane);
				
		toInvBtn = new JButton("Generate Invoice");
		toInvBtn.setBounds(400, 50, 120, 30);
		toInvBtn.addActionListener(this);
		
		if(Invoiced.equalsIgnoreCase("NO")) {
			add(toInvBtn);
		}
		
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

		
		fetchItems(QId);

		float total = 0;
		for(int j =0; j < items.size(); j++) {
			total = total + Float.valueOf(items.get(j).getTotal());
		}
		Total = String.valueOf(total);
		tLbl.setText("Quote Total: Ksh " + Total);
        
    }
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == printBtn) {
			printQuote();
			
		} else if (e.getSource() == toInvBtn) {
			generateInv();

		} else if (e.getSource() == delBtn) {
			deleteQuote();

		} else if (e.getSource() == backBtn) {
			QuoteViewManager.this.dispose();
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
	
	private void printQuote() {
		new GenerateQuotation(items, CName, CContact, Teller, 
				Date,  QId, Total, "Quote");
		
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");

//		if(JOptionPane.showConfirmDialog(null, "Pdf File geneated successfully "
//				+ "\n \n" + "Do you wish to continue to Print Out the document? \n ") == 0) {
//			
//		} else {
//			QuoteView.this.dispose();
//		}
		QuoteViewManager.this.dispose();
	}
	
	public void deleteQuote() {
		if(JOptionPane.showConfirmDialog(null, "You are about to delete this Record"
				+ "\n \n" + "Do you wish to continue? \n ") == 0) {

			UpdateData.deleteReceipt(QId);
			
			QuoteViewManager.this.dispose();

			JOptionPane.showMessageDialog(null, "Record Deleted successfully ");

			ManagerDashboard.refresh();
		}
	}
	
	private long millis; 
    private Date date;
    private int tellerId;
    int custID, recID;
    
	private void generateInv() {
		
		millis = System.currentTimeMillis(); 
		date = new Date(millis);
		tellerId = Statics.getUSERID();
		
		writeInvoice();
		
		JOptionPane.showMessageDialog(null, "Invoice geneated successfully ");
		
//		if(JOptionPane.showConfirmDialog(null,  "\n \n" 
//				+ "Do you wish to continue to geneate an Invoice document? \n ") == 0) {
//			new GenerateInvoice(items, CName, CContact, Teller, Date, InvNo, 
//					Balance, Total, "Invoice");
//		} else {
//			QuoteView.this.dispose();
//			TellerDashboard.refreshData();		
//		}
		
		QuoteViewManager.this.dispose();
		TellerDashboard.refreshData();
		
	}
	
	private void fetchItems(String qId) {
		drawTable();
		items = GetRecords.fetchQuoteItems(qId);
		
		String code, pName = "", unit = "", Desc = "", dimens = "", particulars = ""; 
		String total, price, quant;
    	
		int SN = 0;
    	SaleItem item; 
    	
    	for(int j =0; j < items.size(); j++) {
			item = items.get(j);
			
			SN = SN +1;
			pName = item.getName();
			dimens = item.getDimens();
			quant = item.getQuantity();
			unit = item.getUnit();
			price = item.getSellp();
			total = item.getTotal();

			if(dimens!= null ) {
				particulars = pName + " "+ dimens;					
			} else {
				particulars = pName;
			}
			

			Object[] record = {quant, particulars, unit, price, total};
			model.addRow(record);
			model.fireTableDataChanged();
			
		}
		
    	model.addTableModelListener(new TableModelListener() {
			@Override
			  public void tableChanged(TableModelEvent e) {
				
				int col = e.getColumn();
				int row = e.getFirstRow();
				String itID = items.get(row).getCode();
				
				String quantity = model.getValueAt(row, 0).toString();  
				String price = model.getValueAt(row, 3).toString();

				float sellP =  Float.valueOf(price);
				float quant = Float.valueOf(quantity);
				
				float total = quant * sellP;
				
				if (col == 0) {
//					update quantity
					UpdateData.updateQuantity(itID, quantity, String.valueOf(total));
					
					model.setValueAt(total, row, 4);

				} else if (col == 3) {
//					update price
					UpdateData.updatePrice(itID, price, String.valueOf(total));
					
					model.setValueAt(total, row, 4);
				}	
				
				float qTotal = 0;
				for(int j =0; j < model.getRowCount(); j++) {
					qTotal = qTotal + Float.valueOf(model.getValueAt(j, 4).toString());								        	
				}
				Total = String.valueOf(qTotal);
				UpdateData.updateQuoteTotal(qTotal, QId);
				tLbl.setText("Quote Total: Ksh " + Total);
		       
				ManagerDashboard.refresh();
				
			  }

			});

		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	        	 float remTotal = 0;
	        	 
	            if (me.getClickCount() == 2) {     // to detect double click events
	               int row = table.getSelectedRow(); // select a row
	               int col = table.getSelectedColumn();
	               
	               String itID = items.get(row).getCode();
           	   
	               if (col == 1) {
	            	   productEdit(itID);
	            	   
	               }
	            }
	         }
		});

		
	}
	
	private void drawTable() {
		model = new DefaultTableModel(itemColumns, 0) {
			boolean[] canEdit = new boolean[]{
	                true, false, false, true, false
	        };
			
	        public boolean isCellEditable(int rowIndex, int columnIndex) {
	            return canEdit[columnIndex];
	        }
		};
		
		table.setModel(model);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);

//		"QUANTITY", "DESCRIPTION", "UNIT", "PRICE", "TOTAL"

		TableColumn col = null;
		for (int i = 0; i < 5; i++) {
		    col = table.getColumnModel().getColumn(i);    
		   
		    if(i == 0 ) {
		    	col.setMaxWidth(80);
		    }else if(i == 1) {
		    	col.setMaxWidth(200);
		    } else {
		    	col.setMaxWidth(100);
		    }
		    if(i !=1) {
		    	col.setCellRenderer(centerRenderer);  	
		    }
		}
	}
	
	

	private void writeInvoice() {
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                            + "user=sqluser&password=sqluserpw");
            statement = connect.createStatement();
            
            resultSet = statement
                    .executeQuery("select cId FROM `hardware`.`customers` "
                    		+ "WHERE cContact = '"+CContact+"' ");
            
            while (resultSet.next()) {
            	custID = resultSet.getInt("cId");
            }
            
			preparedStatement = connect
	                .prepareStatement("INSERT INTO `hardware`.`receipts` (`rTotal`, `rDate`, `rBal`, `rType`, `uID`, `cId`)"
	                		+ " VALUES (?, ?, ?, ?, ?, ?)");
	        preparedStatement.setString(1, Total);
	        preparedStatement.setDate(2, date);
	        preparedStatement.setString(3, Total);
            preparedStatement.setString(4, "invoice");
	        preparedStatement.setInt(5, tellerId);
	        preparedStatement.setInt(6, custID);
	        
	        preparedStatement.executeUpdate();
	        
	        //Get Receipt Id
	        resultSet = statement
	                .executeQuery("select rId FROM `hardware`.`receipts` WHERE rDate = '"
	                		+ date+"' AND uID ="+ tellerId+ "  ORDER BY rId Desc LIMIT 1");
	        
	        while (resultSet.next()) {
	        	recID = resultSet.getInt("rId");
	        }
	        
//	        update
	        preparedStatement = connect
                    .prepareStatement( "UPDATE `hardware`.`receipt_items` SET `rId` = '"+ recID +"' "
                    		+ "WHERE (`qId` = '"+ QId +"'); ");
	        preparedStatement.executeUpdate();
	        

    		preparedStatement = connect
                    .prepareStatement("UPDATE `hardware`.`quotations` SET `qInvoiced` = 'YES' "
                    		+ "WHERE (`qId` = '"+ QId +"'); ");
	        preparedStatement.executeUpdate();
	        
		} catch (Exception e) {
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
        	e.printStackTrace();
        }
    }
		

}
