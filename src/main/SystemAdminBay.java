package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import manager.ManagerDashboard;
import store.StoreDashboard;
import teller.TellerDashboard;


public class SystemAdminBay extends JFrame {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SystemAdminBay();
			}
		});
	}
	
	public SystemAdminBay() {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Point of Sale");
        setBounds(400, 100, 500, 500);
        setLayout(null);
        setBackground(Color.GRAY);
        
		JLabel tLbl =  new JLabel("Choose Endpoint");
		tLbl.setBounds(200, 20, 150, 30);
		tLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(tLbl);
		
		JPanel tellerPane = new JPanel();
		tellerPane.setBounds(150, 80, 200, 50);
		tellerPane.setBackground(Color.GRAY);
		tellerPane.addMouseListener(new PanelAdapter(tellerPane){
			public void mouseClicked(MouseEvent e) {
				new TellerDashboard().setVisible(true);
			}
		});
		tellerPane.setLayout(null);
		add(tellerPane);
		
		JLabel teller = new JLabel("Sales Dashboard");
		teller.setBounds(30, 0, 170, 50);
		teller.setForeground(Color.WHITE);
		teller.setFont(new Font("Dialog", Font.BOLD, 14));
		tellerPane.add(teller);
		
		JPanel managerPane = new JPanel();
		managerPane.setBounds(150, 140, 200, 50);
		managerPane.setBackground(Color.GRAY);
		managerPane.addMouseListener(new PanelAdapter(managerPane){
			public void mouseClicked(MouseEvent e) {
				new ManagerDashboard().setVisible(true);
			}
		});
		managerPane.setLayout(null);
		add(managerPane);
		JLabel manager = new JLabel("Manager Dashboard");
		manager.setBounds(30, 0, 170, 50);
		manager.setForeground(Color.WHITE);
		manager.setFont(new Font("Dialog", Font.BOLD, 14));
		managerPane.add(manager);
		
		JPanel storePane = new JPanel();
		storePane.setBounds(150, 200, 200, 50);
		storePane.setBackground(Color.GRAY);
		storePane.addMouseListener(new PanelAdapter(storePane){
			public void mouseClicked(MouseEvent e) {
				new StoreDashboard().setVisible(true);
			}
		});
		storePane.setLayout(null);
		add(storePane);
		
		JLabel store = new JLabel("Store Dashboard");
		store.setBounds(30, 0, 150, 50);
		store.setForeground(Color.WHITE);
		store.setFont(new Font("Dialog", Font.BOLD, 14));
		storePane.add(store);
		
		
		
		
		JPanel logOutPane = new JPanel();
		logOutPane.setBounds(150, 400, 200, 50);
		logOutPane.setBackground(Color.GRAY);
		logOutPane.addMouseListener(new PanelAdapter(logOutPane){
			public void mouseClicked(MouseEvent e) {
			 	if(JOptionPane.showConfirmDialog(null, "You will be Logged Out of the system "
						+ "\n" + " Do you wish to continue?") == 0) {
					
					LogIn logIn = new LogIn();
					logIn.setVisible(true);
					
					SystemAdminBay.this.dispose();
				}
			}
		});
		add(logOutPane);
		logOutPane.setLayout(null);
		// ------ //
		JLabel logOut = new JLabel("Log Out");
		logOut.setBounds(50, 0, 170, 50);
		logOut.setForeground(Color.WHITE);
		logOut.setFont(new Font("Dialog", Font.BOLD, 14));
		logOutPane.add(logOut);
	}

	
	private class PanelAdapter extends MouseAdapter {
		JPanel panel;
		
		public PanelAdapter(JPanel pane) {
			this.panel = pane;
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			panel.setBackground(Color.LIGHT_GRAY);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			panel.setBackground(Color.GRAY);
		}
		@Override
		public void mousePressed(MouseEvent e) {
			panel.setBackground(Color.DARK_GRAY);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			panel.setBackground(Color.LIGHT_GRAY);
		}
	}
}
