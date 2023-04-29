package teller;

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
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.GetData;
import api.GetRecords;
import api.InvoiceData;
import models.SaleItem;
import singletons.Statics;

public class PayInvoice extends JFrame implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

	private String[] itemColumns= {
			"SN", "ITEM", "QUANTITY", "UNIT", "PRICE", "TOTAL"
	};
	
	private JCheckBox mpesaChk, cashChk;
	private ButtonGroup viewgroup;
	String payMode;
	JButton payBtn;
	JLabel newBal;
	private JTextField payTxt, discTxt, cashTxt; 

	private JTable table, itTable;
	private DefaultTableModel model, itModel;
	
	String query, InvNo, CName, Teller, Date, Total, Balance, Discount;
	int invID ;
	float bal;
	ArrayList<SaleItem> items;
	
	
	public PayInvoice(String invNo, String cName, String teller, String date, String total, String balance) {
		InvNo = invNo;
		CName = cName;
		Teller = teller;
		Date = date;
		Total = total;
		Balance = balance;
		payMode = "";
		
		invID = Integer.valueOf(invNo);
		bal = Float.valueOf(balance);
		
		items = new ArrayList<>();
		
		query = "SELECT itId, p.pId, p.pName, p.pDescription, i.itQuantity, unit, "
        		+ "itSellP, i.itTotal, i.itDimens "
        		+ "FROM hardware.receipt_items i "
        		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
        		+ "JOIN hardware.products p ON sh.pId = p.pId "
        		+ "WHERE i.rId = '"+ InvNo +"' ";
		
		Discount = InvoiceData.getInvDiscount(invNo);
		
		setTitle("View Invoice");
		setBounds(200, 100, 800, 500);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		JLabel cNLbl = new JLabel("Invoice for: " + cName);
        cNLbl.setBounds(350, 20, 150, 20);
        cNLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(cNLbl);
		
        JLabel dLbl = new JLabel("Date: " + date);
        dLbl.setBounds(50, 40, 150, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(dLbl);
        
        JLabel tLbl = new JLabel("Invoice Total: " + total);
        tLbl.setBounds(550, 40, 150, 20);
        tLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 15));
        add(tLbl);
        
        JLabel balLbl = new JLabel("Balance: " + balance);
        balLbl.setBounds(580, 80, 150, 20);
        balLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(balLbl);
        
        
        
        itModel = new DefaultTableModel(itemColumns, 0);
		itTable = new JTable(itModel){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
		
		JScrollPane scrollPane= new JScrollPane(itTable);
		scrollPane.setBounds(20, 70, 500, 350);
		add(scrollPane);
		
		JLabel nbLbl = new JLabel("New Balance");
		nbLbl.setBounds(550, 150, 100, 30);
		nbLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
		add(nbLbl);
		
		newBal = new JLabel();
		newBal.setBounds(700, 150, 100, 30);
		newBal.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
		add(newBal);
		
		JLabel disc = new JLabel("Discount");
        disc.setBounds(550, 200, 100, 30);
        disc.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(disc);
        
        discTxt = new JTextField();
        discTxt.setBounds(650, 200, 100, 30);
        discTxt.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        discTxt.setText(Discount);
        add(discTxt);
        
        JLabel npLbl = new JLabel("New Payment");
        npLbl.setBounds(620, 250, 150, 20);
        npLbl.setFont(new Font("Dialog", Font.BOLD, 12));
        add(npLbl);
        
        JLabel ksh = new JLabel("KSh");
        ksh.setBounds(600, 280, 30, 30);
        ksh.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(ksh);
        
        payTxt = new JTextField();
        payTxt.setBounds(650, 280, 100, 30);
        payTxt.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
        add(payTxt);
                
        cashChk = new JCheckBox("Cash");
        cashChk.setBounds(630, 315, 70, 30);
		cashChk.addActionListener(this);
		cashChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(cashChk);
		
        mpesaChk = new JCheckBox("M-Pesa");
        mpesaChk.setBounds(630, 340, 70, 30);
        mpesaChk.addActionListener(this);
        mpesaChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
        add(mpesaChk);
		 
		viewgroup = new ButtonGroup();
 		viewgroup.add(cashChk);
 		viewgroup.add(mpesaChk);
        
        payBtn = new JButton("Pay");
        payBtn.setBounds(660, 400, 100, 30);
        payBtn.addActionListener(this);
        add(payBtn);
        

        if (!Discount.equalsIgnoreCase("0")) {
        	discTxt.setEditable(false);
    	}
    	
		
		items = GetRecords.fetchInvItems(query);
		drawTable();
		
		
		
        payTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {

	                	getBalance();	                	
	            		
	                }
	            });
	        }
		});
        
        discTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	getBalance();
	                }
	            });
	        }
		});

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cashChk) {
			if(cashChk.isSelected()) {
    			payMode = "Cash";
			} else {
    			payMode = "";
			}
			
		} else if(e.getSource() == mpesaChk) {
			if(mpesaChk.isSelected()) {
				payMode = "Mpesa";
    		} else {
    			payMode = "";
			}
			
		} else if (e.getSource() == payBtn) {
			submit();
		}
			
	}
	
	private void getBalance() {
		float ds = 0;
		String d = discTxt.getText().trim();
    	String p = payTxt.getText().trim();
    	    	
    	if(d.isEmpty()) { 
    		d = "0";
    	}
    	if(p.isEmpty()) { 
    		p = "0";
    	}
    	
    	if (!d.equalsIgnoreCase(Discount)) {
    		ds = Float.valueOf(d);
    	}
    	
		float t = Float.valueOf(Total);
    	float b = bal;
    	float pym = Float.valueOf(p);
    	
    	b = b - ds - pym;

		newBal.setText(String.valueOf(b));
	}

	private void submit() {
		float balance;
		String cash = payTxt.getText().toString().trim();
		String discount = discTxt.getText().trim();
    	
		if( !cash.isEmpty() ) {
	    	
			if(discount.isEmpty()) { discount = "0"; }
	    	if(cash.isEmpty()) { cash = "0"; }

	    	float ds = Float.valueOf(discount);
	    	float pym = Float.valueOf(cash);

	    	bal = bal - ds - pym;

			if(cashChk.isSelected()) {
    			payMode = "Cash";
    		} else if (mpesaChk.isSelected()) {
    			payMode = "M-Pesa";
    		} else {
    			JOptionPane.showMessageDialog(PayInvoice.this, "Select Payment Mode");
    		} 
			
			if (payMode != "") {
				submitPay(invID, cash, bal, discount, payMode);
					
    		}
		} else {
			JOptionPane.showMessageDialog(PayInvoice.this, "Input Cash Recieved");
		}

	}

	private void submitPay(int invID, String cash, float balance, String discount, String payMode) {
		
		//Get Current Date
        long millis = System.currentTimeMillis();  
        Date date = new Date(millis);
        int UID = Statics.getUSERID();
        
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                            + "user=sqluser&password=sqluserpw");
            statement = connect.createStatement();
            
            
//            Write to payments
            preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`payments` (`cash`, `payDate`, `payMode`, `rId`, `uId` ) "
                    		+ "VALUES ( ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, cash);
            preparedStatement.setDate(2, date);
            preparedStatement.setString(3, payMode);
            preparedStatement.setInt(4, invID);
            preparedStatement.setInt(5, UID);

            preparedStatement.executeUpdate();
            
//            Update Balance
            preparedStatement = connect
                    .prepareStatement("UPDATE `hardware`.`receipts` SET `rBal` = '"+ balance +"', `rDiscount` = '"+ discount
                    		+"' WHERE (`rId` = '" + invID +"') ");
            
            preparedStatement.executeUpdate();
                        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		
		TellerDashboard.refreshData();
		PayInvoice.this.dispose();
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
		    } else if(i == 3 | i == 4) {
		    	col.setMaxWidth(70);
		    } else {
		    	col.setMaxWidth(100);
		    }
		}
		
		String pName = "", unit = "", Desc = "", dimens = "", particulars = ""; 
    	String code, total, price, quantity;
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

	private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
            if (preparedStatement != null) {
            	preparedStatement.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

	
	
}
