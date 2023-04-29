package store;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import main.LogIn;
import singletons.Statics;


public class StoreDashboard extends JFrame {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new StoreDashboard();
			}
		});
	}
	
	private Container c;
	private JPanel menu;
	private static StockPanel stockPanel;
	private ReceiveStock receiveStock;
	private static SuppliesPanel deliveriesPanel;
	
	public StoreDashboard() {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		
		receiveStock = new ReceiveStock();
		receiveStock.setVisible(true);
		deliveriesPanel = new SuppliesPanel();
		deliveriesPanel.setVisible(false);
		stockPanel = new StockPanel();
		stockPanel.setVisible(false);

		
		setBounds(0, 0, 1024, 720);
		setTitle("Store Dashboard");
		setVisible(true);
		setResizable(false);
		
		c = getContentPane();
		c.setLayout(null);
				
		menu = new JPanel();
		menu.setBackground(new Color(40, 50, 70));
		menu.setBounds(0, 0, 200, 750);
		c.add(menu);		
		menu.setLayout(null);
		
		//----//
		JLabel logo = new JLabel();
		logo.setBackground(Color.WHITE);
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setBounds(20, 20, 180, 180);
		logo.setForeground(Color.WHITE);
//				logo.setIcon(new ImageIcon(getClass().getResource("shop.png")));
		menu.add(logo);
		
		
//<--->
		JPanel stInPane = new JPanel();
		stInPane.addMouseListener(new PanelAdapter(stInPane) {
			public void mouseClicked(MouseEvent e) {
				menuClicked(receiveStock);
			}
		});
		stInPane.setBackground(Color.GRAY);
		stInPane.setBounds(5, 180, 195, 50);
		menu.add(stInPane);
		stInPane.setLayout(null);
	
		
		JLabel stockIn = new JLabel("Recieve Stock");
		stockIn.setBounds(20, 0, 150, 50);
		stockIn.setForeground(Color.WHITE);
		stockIn.setFont(new Font("Dialog", Font.BOLD, 14));
		stInPane.add(stockIn);
		
		JPanel delivPane = new JPanel();
		delivPane.setBounds(5, 235, 195, 50);
		delivPane.setBackground(Color.GRAY);
		delivPane.addMouseListener(new PanelAdapter(delivPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(deliveriesPanel);
			}
		});
		menu.add(delivPane);
		delivPane.setLayout(null);
		
		JLabel deliv = new JLabel("Supplies recieved");
		deliv.setBounds(20, 0, 150, 50);
		deliv.setForeground(Color.WHITE);
		deliv.setFont(new Font("Dialog", Font.BOLD, 14));
		delivPane.add(deliv);
		// -------- //
		JPanel stockPane = new JPanel();
		stockPane.setBounds(5, 290, 195, 50);
		stockPane.setBackground(Color.GRAY);
		stockPane.addMouseListener(new PanelAdapter(stockPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(stockPanel);
			}
		});
		menu.add(stockPane);
		stockPane.setLayout(null);
		
		JLabel stock = new JLabel("Stock On Shelve");
		stock.setBounds(20, 0, 150, 50);
		stock.setForeground(Color.WHITE);
		stock.setFont(new Font("Dialog", Font.BOLD, 14));
		stockPane.add(stock);
		
		
		
		JPanel logOutPane = new JPanel();
		logOutPane.setBounds(5, 565, 195, 50);
		logOutPane.setBackground(Color.GRAY);
		logOutPane.addMouseListener(new PanelAdapter(logOutPane){
			public void mouseClicked(MouseEvent e) {
				if(JOptionPane.showConfirmDialog(null, "You will be Logged Out of the system "
						+ "\n" + " Do you wish to continue?") == 0) {
					
					LogIn logIn = new LogIn();
					logIn.setVisible(true);
					Statics.setUSERID(0);
					StoreDashboard.this.dispose();
				}
			}
		});
		menu.add(logOutPane);
		logOutPane.setLayout(null);
		// ------ //
		JLabel logOut = new JLabel("Log Out");
		logOut.setBounds(30, 0, 100, 50);
		logOut.setForeground(Color.WHITE);
		logOut.setFont(new Font("Dialog", Font.BOLD, 14));
		logOutPane.add(logOut);
		
		
		JPanel content = new JPanel();
		content.setBounds(200, 0, 1200, 750);
		c.add(content);	
		content.setLayout(null);
		
		content.add(stockPanel);
		content.add(receiveStock);
		content.add(deliveriesPanel);

	}
	
	public void menuClicked(JPanel panel) {
		receiveStock.setVisible(false);
		deliveriesPanel.setVisible(false);
		stockPanel.setVisible(false);
		
		panel.setVisible(true);
	}
	
	public static void refresh() {
		deliveriesPanel.refresh();
		stockPanel.refresh();
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
			panel.setBackground(Color.LIGHT_GRAY);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			panel.setBackground(Color.DARK_GRAY);
		}
	}
}
