package main;
import javax.swing.*;

import manager.ManagerDashboard;
import singletons.Statics;
import store.StoreDashboard;
import teller.TellerDashboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class LogIn extends JFrame implements ActionListener {

	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	
    private String Name, Pass;
    int uLvl;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LogIn();
			}
		});
	}

	private JButton logIn;
	private JTextField nameTxt;
	private JPasswordField passTxt;
	private JComboBox level;
	private JLabel levelLbl, nameLbl, passLbl;
	
	public LogIn() {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		
		setTitle("Point of Sale");
        setBounds(300, 100, 450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
		setVisible(true);
		setLayout(null);
        
		nameLbl = new JLabel("User name");
		nameLbl.setBounds(70, 100, 100, 30);
		add(nameLbl);
		
		nameTxt = new JTextField();
		nameTxt.setBounds(180, 100, 170, 30);
		add(nameTxt);
		
		passLbl = new JLabel("Password");
		passLbl.setBounds(70, 150, 100, 30);
	    add(passLbl);
	    
	    passTxt= new JPasswordField();
	    passTxt.setBounds(180, 150, 170, 30);
	    add(passTxt);
	    
	    logIn = new JButton(" LOG IN ");
	    logIn.addActionListener(this);
	    logIn.setBounds(125, 250, 200, 30);
	    add(logIn);
	  
	}
	
	@Override
	public void actionPerformed (ActionEvent e) {
		if(e.getSource() == logIn) {
			Name = nameTxt.getText().toString();
			Pass = passTxt.getText().toString();
			
			if(Name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input User name");
			} else if(Pass.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input password");
			} else {
				authenticate();
			}
			
		}
	}
	
	
	private void authenticate() {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery("SELECT uID, uName, uPass, uLevel FROM hardware.users "
	                		+ "WHERE uName = '"+ Name +"'");
	    	
	    	String uPass = null; 
	    	int UID = 0;
	    	while (resultSet.next()) {
	    		uPass = resultSet.getString("uPass");
	    		UID = resultSet.getInt("uID");
	    		uLvl = resultSet.getInt("uLevel");
	    	}
	    	
	    	if(uPass != null) {
	    		if (uPass.equals(Pass)) {
		    		Statics.setUSERID(UID);
		    		navigate();
	    		} else {
		    		JOptionPane.showMessageDialog(this, 
		    				"Incorrect Password for: "+ Name);
		    	}
	    	} else {
	    		JOptionPane.showMessageDialog(this, 
	    				"No User with the name: "+ Name);
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
	}
	
	public void navigate() {
		switch (uLvl) {
		case 1: 
//			System Admin
			new SystemAdminBay().setVisible(true);
			LogIn.this.dispose();
			break;
		case 2: 
//			Manager
			new ManagerDashboard().setVisible(true);
			LogIn.this.dispose();
			break;
			
		case 3:
//			Teller
			new TellerDashboard().setVisible(true);
			LogIn.this.dispose();
			break;
		
		case 4:
//			Store Manager
			new StoreDashboard().setVisible(true);
			LogIn.this.dispose();
			break;
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
        } catch (Exception e) {

        }
    }

}
