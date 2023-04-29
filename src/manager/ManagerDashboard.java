package manager;

import javax.swing.*;

import main.LogIn;
import singletons.Statics;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManagerDashboard extends JFrame {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ManagerDashboard();				
			}
		});

	}
	
	JPanel menu, salesPane, invoicesPane, stockPane, productsPane, 
			  customersPane, usersPane;
	
	JLabel logo, sales, invoices, stock, products, customers, users, logOut;
	private static CashSalesPanel salesPanel;
	private static InvoicesPanel invoicesPanel;
	private static StockPanel stockPanel;
	private static ProductsPanel productsPanel;
	private static CustomerRecords customersPanel;
	private static UsersPanel usersPanel;
	private static QuotationsPanel quotationsPanel;
	private static PaymentsPanel Payments;
	private static SummaryPanel summaryPanel;
	
	public ManagerDashboard(){

		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

		salesPanel = new CashSalesPanel();
		invoicesPanel = new InvoicesPanel();
		quotationsPanel = new QuotationsPanel();
		Payments = new PaymentsPanel();
		customersPanel = new CustomerRecords();
		summaryPanel = new SummaryPanel();
		productsPanel = new ProductsPanel();
		stockPanel = new StockPanel();
		usersPanel = new UsersPanel();
		
		salesPanel.setVisible(true);
		invoicesPanel.setVisible(false);
		stockPanel.setVisible(false);
		productsPanel.setVisible(false);
		customersPanel.setVisible(false);
		usersPanel.setVisible(false);
		quotationsPanel.setVisible(false);
		Payments.setVisible(false);
		
		setTitle("Manager DashBoard");
		setBounds(0, 0, 1024, 720);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		menu = new JPanel();
		menu.setBackground(new Color(40, 50, 70));
		menu.setBounds(0, 0, 200, 750);
		add(menu);		
		menu.setLayout(null);

		//----//
		logo = new JLabel();
		logo.setBackground(Color.WHITE);
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setBounds(20, 20, 150, 180);
		logo.setForeground(Color.WHITE);
//		logo.setIcon(new ImageIcon(getClass().getResource("shop.png")));
		menu.add(logo);

		// ------ //
		salesPane = new JPanel();
		salesPane.setBounds(5, 180, 195, 50);
		salesPane.setBackground(Color.GRAY);
		menu.add(salesPane);
		salesPane.setLayout(null);

		// ------ //  
		invoicesPane = new JPanel();
		invoicesPane.setBounds(5, 235, 195, 50);
		invoicesPane.setBackground(Color.GRAY);
		menu.add(invoicesPane);
		invoicesPane.setLayout(null);
		
		//
		JPanel quotePane = new JPanel();
		quotePane.setBounds(5, 290, 195, 50);
		quotePane.setBackground(Color.GRAY);
		menu.add(quotePane);
		quotePane.setLayout(null);
		
		// ------- //
		customersPane = new JPanel();
		customersPane.setBounds(5, 345, 195, 50);
		customersPane.setBackground(Color.GRAY);
		menu.add(customersPane);
		customersPane.setLayout(null);
		
		// ------- //
		JPanel payPane = new JPanel();
		payPane.setBounds(5, 400, 195, 50);
		payPane.setBackground(Color.GRAY);
		payPane.addMouseListener(new PanelAdapter(payPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(Payments);
			}
		});
		menu.add(payPane);
		payPane.setLayout(null);
		
		// ------ //
		productsPane = new JPanel();
		productsPane.setBounds(5, 455, 195, 50);
		productsPane.setBackground(Color.GRAY);
		menu.add(productsPane);
		productsPane.setLayout(null);

		// ------- //
		JPanel summaryPane = new JPanel();
		summaryPane.setBounds(5, 510, 195, 50);
		summaryPane.setBackground(Color.GRAY);
		menu.add(summaryPane);
		summaryPane.setLayout(null);

				
		stockPane = new JPanel();
		stockPane.setBounds(5, 510, 195, 50);
		stockPane.setBackground(Color.GRAY);
//		menu.add(stockPane);
		stockPane.setLayout(null);

		// ------- //
		usersPane = new JPanel();
		usersPane.setBounds(5, 565, 195, 50);
		usersPane.setBackground(Color.GRAY);
				menu.add(usersPane);
		usersPane.setLayout(null);

		JPanel logOutPane = new JPanel();
		logOutPane.setBounds(5, 620, 195, 50);
		logOutPane.setBackground(Color.GRAY);
		menu.add(logOutPane);
		logOutPane.setLayout(null);
		
		
		// ------ //
		sales = new JLabel("Cash sales");
		sales.setBounds(30, 0, 100, 50);
		sales.setForeground(Color.WHITE);
		sales.setFont(new Font("Dialog", Font.BOLD, 14));
		salesPane.add(sales);
		
		// ------- //
		invoices = new JLabel("Invoices");
		invoices.setBounds(30, 0, 100, 50);
		invoices.setForeground(Color.WHITE);
		invoices.setFont(new Font("Dialog", Font.BOLD, 14));
		invoicesPane.add(invoices);
		
		JLabel quotes = new JLabel("Quotations");
		quotes.setBounds(30, 0, 100, 50);
		quotes.setForeground(Color.WHITE);
		quotes.setFont(new Font("Dialog", Font.BOLD, 14));
		quotePane.add(quotes);
		// ------ //
		customers = new JLabel("Customers");
		customers.setBounds(30, 0, 100, 50);
		customers.setForeground(Color.WHITE);
		customers.setFont(new Font("Dialog", Font.BOLD, 14));
		customersPane.add(customers);
				
		// -- //
		JLabel pay = new JLabel("Payments");
		pay.setBounds(30, 0, 100, 50);
		pay.setForeground(Color.WHITE);
		pay.setFont(new Font("Dialog", Font.BOLD, 14));
		payPane.add(pay);
		
		//
		JLabel summary = new JLabel("Summary");
		summary.setBounds(30, 0, 200, 50);
		summary.setForeground(Color.WHITE);
		summary.setFont(new Font("Dialog", Font.BOLD, 14));
		summaryPane.add(summary);
		
		// ------- //
		stock = new JLabel("Stock");
		stock.setBounds(30, 0, 100, 50);
		stock.setForeground(Color.WHITE);
		stock.setFont(new Font("Dialog", Font.BOLD, 14));
		stockPane.add(stock);
		
		// ------ //
		products = new JLabel("Products and pricing");
		products.setBounds(30, 0, 150, 50);
		products.setForeground(Color.WHITE);
		products.setFont(new Font("Dialog", Font.BOLD, 14));
		productsPane.add(products);

		// ------ //
		users = new JLabel("Users");
		users.setBounds(30, 0, 100, 50);
		users.setForeground(Color.WHITE);
		users.setFont(new Font("Dialog", Font.BOLD, 14));
		usersPane.add(users);	

		// ------ //
		logOut = new JLabel("Log Out");
		logOut.setBounds(30, 0, 100, 50);
		logOut.setForeground(Color.WHITE);
		logOut.setFont(new Font("Dialog", Font.BOLD, 14));
		logOutPane.add(logOut);

		//-------//
		JPanel content = new JPanel();
		content.setBounds(200, 0, 1200, 750);
		add(content);	
		content.setLayout(null);
		
		content.add(salesPanel);
		content.add(invoicesPanel);
		content.add(customersPanel);
		content.add(quotationsPanel);
		content.add(Payments);
		content.add(summaryPanel);
		content.add(productsPanel);
		content.add(stockPanel);
		content.add(usersPanel);		
		
		salesPane.addMouseListener(new PanelAdapter(salesPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(salesPanel);
			}
		});
		invoicesPane.addMouseListener(new PanelAdapter(invoicesPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(invoicesPanel);
			}
		});
		quotePane.addMouseListener(new PanelAdapter(quotePane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(quotationsPanel);
			}
		});
		customersPane.addMouseListener(new PanelAdapter(customersPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(customersPanel);
			}
		});
		summaryPane.addMouseListener(new PanelAdapter(summaryPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(summaryPanel);
			}
		});
		productsPane.addMouseListener(new PanelAdapter(productsPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(productsPanel);
			}
		});
		stockPane.addMouseListener(new PanelAdapter(stockPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(stockPanel);
			}
		});

		usersPane.addMouseListener(new PanelAdapter(usersPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(usersPanel);
			}
		});
		logOutPane.addMouseListener(new PanelAdapter(logOutPane){
			public void mouseClicked(MouseEvent e) {
			 	if(JOptionPane.showConfirmDialog(null, "You will be Logged Out of the system "
						+ "\n" + " Do you wish to continue?") == 0) {
					
					LogIn logIn = new LogIn();
					logIn.setVisible(true);
					
					Statics.setUSERID(0);
					ManagerDashboard.this.dispose();
				}
			}
		});

	}
	
	public void menuClicked(JPanel panel) {
		salesPanel.setVisible(false);
		invoicesPanel.setVisible(false);
		stockPanel.setVisible(false);
		productsPanel.setVisible(false);
		customersPanel.setVisible(false);
		usersPanel.setVisible(false);
		quotationsPanel.setVisible(false);
		Payments.setVisible(false);
		summaryPanel.setVisible(false);
		
		panel.setVisible(true);
	}
	
	public static void refresh() {
		salesPanel.refresh();
		invoicesPanel.refresh();
		stockPanel.refresh();
		productsPanel.refresh();
		customersPanel.refresh();
		usersPanel.refresh();
		quotationsPanel.refresh();
		Payments.refresh();
		summaryPanel.refresh();
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
