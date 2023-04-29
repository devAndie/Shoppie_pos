package singletons;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import api.CustomerData;
import api.GetData;
import api.UpdateData;
import manager.ManagerDashboard;
import models.Customer;

public class CustomerView extends JFrame implements ActionListener {

	String Id, Name, Visits, Items, Total, Balance, Date, Cashs, Invoices;
	String Contact;
	
	JTextField nm, cn;
	JButton editBtn, cancelBtn, submitBtn;
	
	Customer customer;
	
	public CustomerView(String id, String name, String visits, String items, String total, String balance, String date,
			String cashs, String invoices) {
		Id = id;
		Name = name;
		Visits = visits;
		Items = items;
		Total = total;
		Balance = balance;
		Date = date;
		Cashs = cashs;
		Invoices = invoices;
		
		customer = CustomerData.getCustomer(Id);
		Contact = customer.getContact();
		
		setTitle("Summary");
		setBounds(200, 100, 550, 400);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		JLabel tLbl = new JLabel("Customer Summary ");
        tLbl.setBounds(100, 10, 250, 20);
        tLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        add(tLbl);
		
        JLabel nmLbl = new JLabel("Name:");
        nmLbl.setBounds(50, 50, 70, 30);
        nmLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(nmLbl);
        
        nm = new JTextField(Name);
        nm.setEditable(false);
        nm.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        nm.setBounds(130, 50, 200, 30);
        add(nm);
        
        JLabel cnLbl = new JLabel("Contact");
        cnLbl.setBounds(50, 90, 70, 30);
        cnLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(cnLbl);
        
        cn = new JTextField(Contact);
        cn.setEditable(false);
        cn.setBounds(130, 90, 200, 30);
        cn.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(cn);
        
        editBtn = new JButton("Edit");
        editBtn.addActionListener(this);
        editBtn.setBounds(350, 70, 70, 30);
        add(editBtn);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
        cancelBtn.setBounds(350, 90, 70, 30);
        
        submitBtn = new JButton("Update");
        submitBtn.addActionListener(this);
        submitBtn.setBounds(350, 50, 70, 30);
        
        
        JLabel nLbl = new JLabel("No of purchases: " + Visits);
        nLbl.setBounds(50, 140, 150, 20);
        nLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(nLbl);
        
        JLabel dLbl = new JLabel("Last purchase was on: " + Date);
        dLbl.setBounds(50, 180, 300, 20);
        dLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(dLbl);
        
        JLabel csLbl = new JLabel("Cash Buys: " + Cashs);
        csLbl.setBounds(50, 220, 300, 20);
        csLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(csLbl);
        
        JLabel inLbl = new JLabel("Invoices: " + Invoices);
        inLbl.setBounds(200, 220, 300, 20);
        inLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(inLbl);
        
        JLabel iLbl = new JLabel("Items Purchased: " + Items);
        iLbl.setBounds(350, 140, 150, 20);
        iLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(iLbl);
        
        JLabel ttLbl = new JLabel("Receipts Total: " + Total);
        ttLbl.setBounds(350, 180, 150, 20);
        ttLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(ttLbl);
        
        JLabel balLbl = new JLabel("Unpaid Bill: " + Balance);
        balLbl.setBounds(350, 220, 150, 20);
        balLbl.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
        add(balLbl);
		        
		        
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == editBtn) {
	
			nm.setEditable(true);
			cn.setEditable(true);
			remove(editBtn);
			add(submitBtn);
			add(cancelBtn);
			
			repaint();
			
		} else if(e.getSource() == cancelBtn) {
			
			nm.setText(Name);
			cn.setText(Contact);
			
			nm.setEditable(false);
			cn.setEditable(false);
			add(editBtn);
			remove(submitBtn);
			remove(cancelBtn);
			
			repaint();
		} else if(e.getSource() == submitBtn) {
			
			Name = nm.getText().toString().trim();
			Contact = cn.getText().toString().trim();
			customer.setName(Name);
			customer.setContact(Contact);
			
			UpdateData.updateCutomer(customer);
			
			nm.setEditable(false);
			cn.setEditable(false);
			add(editBtn);
			remove(submitBtn);
			remove(cancelBtn);
			
			repaint();
			
			ManagerDashboard.refresh();
		}
	}

}
