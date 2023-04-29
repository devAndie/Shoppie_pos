package singletons;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

import api.UpdateData;
import manager.ManagerDashboard;

public class ProductView extends JFrame implements ActionListener {
	private Connection connect = null;
    private PreparedStatement preparedStatement = null;
    
    JLabel namel, descl, unitl, sell;
    JButton editBtn, updateBtn, delBtn, backBtn;
    JTextField nameTxt, descTxt, unitTxt, sellTxt;
    
	String Pid, Name, Desc, Unit, SellP;

	public ProductView(String pid, String pName, String descr, String unitMsr, String sellP) {
		Pid = pid;
		Name = pName;
		Desc = descr;
		Unit = unitMsr;
		SellP = sellP;
		
		setTitle("View Product");
		setBounds(220, 100, 450, 350);
		setResizable(false);
		setVisible(true);
		setLayout(null);
		
		JLabel ttl = new JLabel("Product Description");
		ttl.setBounds(150, 10, 200, 30);
		ttl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(ttl);
		
		JLabel nameLbl = new JLabel("Name");
		nameLbl.setBounds(30, 50, 70, 30);
		nameLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(nameLbl);
		
		namel = new JLabel(Name);
		namel.setBounds(110, 50, 150, 30);
		add(namel);
		
		JLabel descLbl = new JLabel("Description");
		descLbl.setBounds(30, 85, 70, 30);
		descLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(descLbl);
		
		descl = new JLabel(Desc);
		descl.setBounds(110, 85, 100, 30);
		add(descl);
		
		JLabel unitLbl = new JLabel("Unit");
		unitLbl.setBounds(30, 120, 70, 30);
		unitLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(unitLbl);
		
		unitl = new JLabel(Unit);
		unitl.setBounds(110, 120, 100, 30);
		add(unitl);
				
		JLabel sellLbl = new JLabel("Price per "+ Unit);
		sellLbl.setBounds(30, 155, 100, 30);
		sellLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(sellLbl);
		
		sell = new JLabel("KSh "+ SellP);
		sell.setBounds(125, 155, 100, 30);
		add(sell);
		
		editBtn = new JButton("Edit");
		editBtn.setBounds(300, 50, 70, 30);
		editBtn.addActionListener(this);
		add(editBtn);
		
		updateBtn = new JButton("Update");
		updateBtn.addActionListener(this);
		updateBtn.setBounds(300, 100, 70, 30);
		
		
		delBtn = new JButton("Delete");
		delBtn.addActionListener(this);
		delBtn.setBounds(300, 150, 70, 30);
		add(delBtn);
		
		backBtn = new JButton("Back");
		backBtn.addActionListener(this);
		backBtn.setBounds(300, 200, 70, 30);
		add(backBtn);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String query;
		
		if(e.getSource() == editBtn) {
			allowEdit();
		} else if(e.getSource() == updateBtn) {
			String pName = nameTxt.getText().toString().trim();
			String pDesc = descTxt.getText().toString().trim();
			String uPrice = sellTxt.getText().toString().trim();
			
			if(pName != Name | pDesc != Desc ) {
				query = "UPDATE `hardware`.`products` SET `pName` = '"+ pName+"', "
						+ "`pDescription` = '"+ pDesc+"' WHERE (`pId` = '"+Pid+"'); ";
				updateProduct(query);
			}
			if(uPrice!= SellP) {
				query = "UPDATE `hardware`.`pricing` SET `price` = '"+uPrice+"' "
						+ "WHERE (`pID` = '"+Pid+"' AND unit = '"+Unit+"');";
				updateProduct(query);
			}
			
		} else if(e.getSource() == delBtn) {
			deleteProduct();
			
		} else if(e.getSource() == backBtn) {
			ProductView.this.dispose();
		}
		
	}

	private void allowEdit() {
		
		nameTxt = new JTextField();
		nameTxt.setBounds(110, 50, 150, 30);
		nameTxt.setText(Name);
		add(nameTxt);
		remove(namel);
		
		descTxt = new JTextField();
		descTxt.setBounds(110, 85, 150, 30);
		descTxt.setText(Desc);
		add(descTxt);
		remove(descl);
		
		sellTxt = new JTextField();
		sellTxt.setBounds(125, 155, 150, 30);
		sellTxt.setText(SellP);
		add(sellTxt);
		remove(sell);
		
		add(updateBtn);
		repaint();
	}
	
	private void updateProduct(String query) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			preparedStatement = connect
                    .prepareStatement(query);
			preparedStatement.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            close();
        }
		
	}
	private void deleteProduct() {
		if(JOptionPane.showConfirmDialog(null, "You are about to delete this Product"
				+ "\n \n" + "Do you wish to continue? \n ") == 0) {

			UpdateData.deleteProduct(Pid);

			ProductView.this.dispose();

			JOptionPane.showMessageDialog(null, "Record Deleted successfully ");

			ManagerDashboard.refresh();
		}
	}
	
	private void close() {
        try {
            if (connect != null) {
                connect.close();
            }
            if (preparedStatement != null) {
            	preparedStatement.close();
            }
        } catch (Exception e) {
			e.printStackTrace();
        }
        
        ManagerDashboard.refresh();
        ProductView.this.dispose();
	}
	
	
}
