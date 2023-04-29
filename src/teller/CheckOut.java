package teller;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import api.CustomerData;
import api.GetData;
import api.WriteSaleData;
import models.Customer;
import models.SaleItem;

public class CheckOut extends JFrame  implements ActionListener {
	    
	private JTextField cNameTxt, cPhoneTxt, csPayTxt;
	private JLabel  change;
	private String  cName, cPhone, recMode, teller, payMode;
	private JButton submitBtn;
	private JCheckBox mpesaChk, cashChk;
	private ButtonGroup viewgroup;
	private JComboBox cnBox, cpBox;
	ArrayList<String> contacts;
	ArrayList<String> names;
	ArrayList<Customer> customers;

	float csPay, csChange;
	String cp, cId = "0";
	private ArrayList<SaleItem> items;
	
    float total;

	public CheckOut( ArrayList<SaleItem>  Items, float t) {
		setTitle("Check out");
		setBounds(300, 100, 500, 300);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		items = Items;
		total = t;
		
    	csPay = 0;
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		
         
         JLabel cNLbl = new JLabel("Customer Name");
         cNLbl.setBounds(40, 20, 150, 30);
         cNLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
         add(cNLbl);
         
         cNameTxt = new JTextField();
         cNameTxt.setBounds(20, 50, 130, 30);
         cNameTxt.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
         add(cNameTxt);
    
         cnBox = new JComboBox();
         cnBox.setBounds(20, 80, 130, 20);
         cnBox.addActionListener(this);
         add(cnBox);
         
         JLabel cPhnLbl = new JLabel("Phone Numer");
         cPhnLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 14));
         cPhnLbl.setBounds(195, 20, 150, 30);
         add(cPhnLbl);
         
         cPhoneTxt = new JTextField();
         cPhoneTxt.setBounds(180, 50, 130, 30);
         cPhoneTxt.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
         add(cPhoneTxt);
         
         cpBox = new JComboBox();
         cpBox.setBounds(180, 80, 130, 20);
         cpBox.addActionListener(this);
         add(cpBox);
         
         JLabel csPayLbl = new JLabel("Cash Paid (KSh)");
         csPayLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
         csPayLbl.setBounds(60, 130, 100, 30);
         
         csPayTxt = new JTextField();
         csPayTxt.setBounds(170, 130, 100, 30);
         csPayTxt.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
         add(csPayLbl);
         add(csPayTxt);
         
         cashChk = new JCheckBox("Cash");
         cashChk.setBounds(170, 170, 70, 30);
 		 cashChk.addActionListener(this);
 		 cashChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
 		 add(cashChk);
 		
         mpesaChk = new JCheckBox("M-Pesa");
         mpesaChk.setBounds(170, 205, 70, 30);
         mpesaChk.addActionListener(this);
         mpesaChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
 		 add(mpesaChk);
 		 
 		 viewgroup = new ButtonGroup();
 		 viewgroup.add(cashChk);
 		 viewgroup.add(mpesaChk);
                         
         JLabel changeLbl = new JLabel("CHANGE");
         changeLbl.setBounds(380, 100, 100, 30);
         changeLbl.setFont(new Font("Dialog", Font.BOLD, 14));
         add(changeLbl);
         
         change = new JLabel("0");
         change.setBounds(400, 130, 100, 30);
         change.setFont(new Font("Dialog", Font.BOLD, 14));
         add(change);
         
         submitBtn = new JButton("Submit");
         submitBtn.setBounds(350, 200, 100, 30);
         submitBtn.addActionListener(this);
         add(submitBtn);

	     cNameTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	getNames(cNameTxt.getText());
	                }
	            });
	        }
		 });
	     cPhoneTxt.addKeyListener(new KeyAdapter() {
	    	 public void keyReleased(KeyEvent ke) {
	    		 SwingUtilities.invokeLater(new Runnable() {
	    			 public void run() {
		                getContacts(cPhoneTxt.getText());
		             }
		         });
		     }
		});
		csPayTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getChange(csPayTxt.getText());
					}
				});
			}
		});
      
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submitBtn) {
    		cName = cNameTxt.getText().toString().strip();
    		cPhone = cPhoneTxt.getText().toString().strip();
    		cp = csPayTxt.getText().toString().strip();
    		payMode = "";
    		if(cName.isEmpty() | cPhone.isEmpty()) {
    			
    			JOptionPane.showMessageDialog( CheckOut.this, 
    					"Input All Customer Details");
    			
    		} else if(!cp.isEmpty()) {
    			csPay = Float.valueOf(cp);
    			csChange = Float.valueOf(change.getText().toString().trim());
    				
    			if(cashChk.isSelected()) {
	    			payMode = "Cash";
	    		} else if (mpesaChk.isSelected()) {
	    			payMode = "M-Pesa";
	    		} 
	    		if (payMode == ""){
	    			JOptionPane.showMessageDialog(CheckOut.this, "Select Payment Mode");
	    		} else {
	    			submit();
	    		}
    		} else {
    			submit();
    		}
    	} else if(e.getSource() == cnBox) {
    		int index = cnBox.getSelectedIndex();
    		if(index != 0) {
    			
    			cName = cnBox.getSelectedItem().toString();
    			cPhone = customers.get(index-1).getContact();
    			cId = customers.get(index-1).getID();
    			
    			cNameTxt.setText(cName);
    			
    		} else {
    			cId = "0";
    			cPhone = "";

    		}
			cPhoneTxt.setText(cPhone);    			

    	} else if(e.getSource() == cpBox) {
    		int i = cnBox.getSelectedIndex();
    		if(i != 0) {
    			cPhone = cpBox.getSelectedItem().toString();
    			cName = customers.get(i-1).getName();
    			cId = customers.get(i-1).getID();

    			cPhoneTxt.setText(cPhone);    			
    			
    		} else {
    			cId = "0";
    			cName = "";
    			
    		}
			cNameTxt.setText(cName);

    	}
			
	}
	
	void getNames(String text){
		names = new ArrayList<>();
		names = CustomerData.getNames(text);
		customers = CustomerData.getCustomers();
		
		if (names.size() > 1) {
			cnBox.setModel(
					new DefaultComboBoxModel(names.toArray()));
			
			cnBox.setSelectedItem(text);
			cnBox.showPopup();
		} else {
			cnBox.hidePopup();
	    }
		
		
	}
	
	void getContacts(String text){
		contacts = new ArrayList<>();
		contacts = CustomerData.getContacts(text);
		customers = CustomerData.getCustomers();
		
		if (contacts.size() > 1) {
			cpBox.setModel(
					new DefaultComboBoxModel(contacts.toArray()));
			cpBox.setSelectedItem(text);
			cpBox.showPopup();
		} else {
			cpBox.hidePopup();
	    }
		
	}
	
	private void submit() {
		 if(!cp.isEmpty()) {
			if(csPay >= total) {
				recMode = "cash";
				writeToDb();
				
			} else if(JOptionPane.showConfirmDialog(null,
					"\n" + "Complete sale as Invoice? "+ "\n" 
					+  "\n") == 0) {
				recMode = "invoice";
				writeToDb();
			}

		}else if(JOptionPane.showConfirmDialog(null, 
				"\n" + "Complete sale as Invoice? "+ "\n" 
				+  "\n") == 0) {
			csPay = 0;
			recMode = "invoice";
			writeToDb();
		}

		
	}

	private void getChange(String text) {
		float csh = 0f, n = 0f;
		
		if(!text.isEmpty()) {
			n = Float.valueOf(text);
		}
		csh = n - total;
		
		change.setText(String.valueOf(csh));

	}
	
	private void writeToDb() {
		WriteSaleData.writeReceipt(items, cId, cName, cPhone, recMode, csPay, payMode);

		CheckOut.this.dispose();
		
		TellerDashboard.refreshData();
		
	}
	
}
