package singletons;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

import manager.ManagerDashboard;

public class UserView extends JFrame implements ActionListener {
	private Connection connect = null;
    private PreparedStatement preparedStatement = null;
    
	private static String[] userLevels = {
			"", "Manager", "Sales Counter", "Store Keeper"
	};
	private JLabel nameLbl, uName, deptLbl, uDept;
	private JButton editBtn, deleteBtn, updateBtn, exitBtn;
	private JComboBox deptBx;
	private JTextField pwdTxt;
	
	private static String Name, Dept, Uid;
	private String chPass;
	private int chDept;
	
	
	public UserView(String uid, String name, String dept) {
		Name = name;
		Dept = dept;
		Uid = uid;
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		setTitle("View User");
		setBounds(300, 200, 400, 350);
		setResizable(false);
		setVisible(true);
		setLayout(null);

		JLabel ttl = new JLabel("User Details");
		ttl.setBounds(150, 10, 150, 30);
		ttl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(ttl);
		
		nameLbl = new JLabel("Name");
		nameLbl.setBounds(60, 55, 70, 30);
		nameLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(nameLbl);
		
		uName = new JLabel(Name);
		uName.setBounds(150, 55, 100, 30);
		add(uName);
		
		deptLbl = new JLabel("Department");
		deptLbl.setBounds(60, 80, 70, 30);
		deptLbl.setFont(new Font("Dialog", Font.BOLD, 12));
		add(deptLbl);
		
		uDept = new JLabel(Dept);
		uDept.setBounds(150, 80, 100, 30);
		add(uDept);
		
		editBtn = new JButton("edit User");
		editBtn.setBounds(270, 80, 100, 30);
		editBtn.addActionListener(this);
		add(editBtn);
		
		deleteBtn = new JButton("Delete User");
		deleteBtn.setBounds(50, 270, 100, 30);
		deleteBtn.addActionListener(this);
		add(deleteBtn);
		
		exitBtn = new JButton("Back");
		exitBtn.setBounds(250, 270, 100, 30);
		exitBtn.addActionListener(this);
		add(exitBtn);		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String updateQuery;
		
		if(e.getSource() == editBtn) {
			editUser();
		} else if(e.getSource() == exitBtn) {
			UserView.this.dispose();
			
		} else if(e.getSource() == updateBtn) {
			chDept = deptBx.getSelectedIndex() + 1;
			chPass = pwdTxt.getText().toString().strip();
			String lvl = deptBx.getSelectedItem().toString();
			
			
			if(!lvl.isEmpty() && !chPass.isEmpty()) {
				updateQuery = "UPDATE `hardware`.`users` SET `uPass` = '"+chPass+"', "
						+ "`uLevel` = '"+chDept+"' WHERE (`uID` = '"+Uid+"');";
				updateUser(updateQuery);
				
			} else if (!lvl.isEmpty()) {
				updateQuery = "UPDATE `hardware`.`users` SET "
						+ "`uLevel` = '"+chDept+"' WHERE (`uID` = '"+Uid+"');";
				updateUser(updateQuery);
				
			} else if (!chPass.isEmpty()) {
				updateQuery = "UPDATE `hardware`.`users` SET `uPass` = '"+chPass+"' "
						+ "WHERE (`uID` = '"+Uid+"');";
				updateUser(updateQuery);
				
			}	
		} else if(e.getSource() == deleteBtn) {
			updateQuery = "DELETE FROM `hardware`.`users` WHERE (`uID` = '"+ Uid+"');";
			updateUser(updateQuery);
		}
	}

	private void editUser() {
		
		JLabel deptLbl = new JLabel("Change Department");
		deptLbl.setBounds(30, 120, 130, 30);
		add(deptLbl);
		
		deptBx = new JComboBox(userLevels);
		deptBx.setBounds(150, 120, 150, 30);
		add(deptBx);
		
		JLabel chPass = new JLabel("Change Password");
		chPass.setBounds(30, 160, 150, 30);
		add(chPass);
		
		pwdTxt = new JTextField();
		pwdTxt.setBounds(150, 160, 150, 30);
		add(pwdTxt);

		updateBtn = new JButton("Update User");
		updateBtn.setBounds(250, 200, 100, 30);
		updateBtn.addActionListener(this);
		add(updateBtn);
		
		repaint();
	}

	private void updateUser(String query) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			preparedStatement = connect
                    .prepareStatement(query);
			preparedStatement.executeUpdate();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
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

        }
        
        ManagerDashboard.refresh();
        UserView.this.dispose();
    }
}


