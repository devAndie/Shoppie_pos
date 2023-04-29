package teller;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import main.LogIn;
import singletons.Statics;

public class TellerDashboard extends JFrame {

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new TellerDashboard();			
			}
		});
	}
	
	private JPanel menu;
	
	private static SellPanel sellPanel;
	private NewQuotation newQuote;
	private static CashSales cashSales;
	private static SummaryPanel summaryPanel;
	private static PendingInvoices invoicesPanel;
	private static InvoiceRecords iRecords;
	private static PaymentsPanel payPanel;
	private static QuotationRecsPanel quotations;
	
	public TellerDashboard(){
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         } catch (Exception e) {
            e.printStackTrace();
         }
		sellPanel = new SellPanel();
		newQuote = new NewQuotation();
		cashSales = new  CashSales();
		invoicesPanel = new PendingInvoices();
		iRecords = new InvoiceRecords();
		payPanel = new PaymentsPanel();
		quotations= new QuotationRecsPanel();
		summaryPanel = new  SummaryPanel();
				
		sellPanel.setVisible(true);
		newQuote.setVisible(false);
		cashSales.setVisible(false);
		invoicesPanel.setVisible(false);
		iRecords.setVisible(false);
		payPanel.setVisible(false);
		quotations.setVisible(false);
		summaryPanel.setVisible(false);
		
		setTitle("Teller DashBoard");
		setBounds(0, 0, 1024, 720);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		setLayout(null);
		
		menu = new JPanel();
		menu.setBackground(new Color(40, 50, 70));
		menu.setBounds(0, 0, 200, 750);
		add(menu);		
		menu.setLayout(null);
		
		//----//
		JLabel logo = new JLabel();
		logo.setBackground(Color.WHITE);
		logo.setHorizontalAlignment(SwingConstants.CENTER);
		logo.setBounds(20, 20, 180, 180);
		logo.setForeground(Color.WHITE);
//		logo.setIcon(new ImageIcon(getClass().getResource("shop.png")));
		menu.add(logo);
		
		//<--->
		JPanel salePane = new JPanel();
		salePane.setBackground(Color.GRAY);
		salePane.setBounds(5, 180, 195, 50);
		menu.add(salePane);
		salePane.setLayout(null);
		// ------ //
		
		JPanel quotePane = new JPanel();
		quotePane.setBackground(Color.GRAY);
		quotePane.setBounds(5, 235, 195, 50);
		menu.add(quotePane);
		quotePane.setLayout(null);

		// ------ //
		JPanel cashPane = new JPanel();
		cashPane.setBackground(Color.GRAY);
		cashPane.setBounds(5, 290, 195, 50);
		menu.add(cashPane);
		cashPane.setLayout(null);

		// ------ //
		JPanel PendInvoicesPane = new JPanel();
		PendInvoicesPane.setBounds(5, 345, 195, 50);
		PendInvoicesPane.setBackground(Color.GRAY);
		menu.add(PendInvoicesPane);
		PendInvoicesPane.setLayout(null);
		
		// ------- //
		JPanel allInvoicesPane = new JPanel();
		allInvoicesPane.setBounds(5, 400, 195, 50);
		allInvoicesPane.setBackground(Color.GRAY);
		menu.add(allInvoicesPane);
		allInvoicesPane.setLayout(null);
		
		// ------ //
		JPanel quotesRecPane = new JPanel();
		quotesRecPane.setBackground(Color.GRAY);
		quotesRecPane.setBounds(5, 455, 195, 50);
		menu.add(quotesRecPane);
		quotesRecPane.setLayout(null);
		
		// ------- //
		JPanel payPane = new JPanel();
		payPane.setBackground(Color.GRAY);
		payPane.setBounds(5, 510, 195, 50);
		menu.add(payPane);
		payPane.setLayout(null);
		
		// ------- //
		JPanel summaryPane = new JPanel();
		summaryPane.setBackground(Color.GRAY);
		summaryPane.setBounds(5, 565, 195, 50);
		menu.add(summaryPane);
		summaryPane.setLayout(null);	
		//
		
		JPanel logOutPane = new JPanel();
		logOutPane.setBackground(Color.GRAY);
		logOutPane.setBounds(5, 620, 195, 50);
		menu.add(logOutPane);
		logOutPane.setLayout(null);
		
		//-------//
		JLabel sell = new JLabel("New Sale");
		sell.setBounds(30, 0, 200, 50);
		sell.setForeground(Color.WHITE);
		sell.setFont(new Font("Dialog", Font.BOLD, 14));
		salePane.add(sell);
		
		JLabel quote = new JLabel("New Quote");
		quote.setBounds(30, 0, 200, 50);
		quote.setForeground(Color.WHITE);
		quote.setFont(new Font("Dialog", Font.BOLD, 14));
		quotePane.add(quote);
		
		
		JLabel records = new JLabel("Cashed Receipts");
		records.setBounds(30, 0, 200, 50);
		records.setForeground(Color.WHITE);
		records.setFont(new Font("Dialog", Font.BOLD, 14));
		cashPane.add(records);
		
		JLabel invoices = new JLabel("Uncleared Invoices");
		invoices.setBounds(30, 0, 200, 50);
		invoices.setForeground(Color.WHITE);
		invoices.setFont(new Font("Dialog", Font.BOLD, 14));
		PendInvoicesPane.add(invoices);
		
		JLabel all = new JLabel("All Invoices");
		all.setBounds(30, 0, 200, 50);
		all.setForeground(Color.WHITE);
		all.setFont(new Font("Dialog", Font.BOLD, 14));
		allInvoicesPane.add(all);

		JLabel quotesRecords = new JLabel("Quotations");
		quotesRecords.setBounds(30, 0, 200, 50);
		quotesRecords.setForeground(Color.WHITE);
		quotesRecords.setFont(new Font("Dialog", Font.BOLD, 14));
		quotesRecPane.add(quotesRecords);

		JLabel pay = new JLabel("Payments");
		pay.setBounds(30, 0, 200, 50);
		pay.setForeground(Color.WHITE);
		pay.setFont(new Font("Dialog", Font.BOLD, 14));
		payPane.add(pay);
		
		JLabel summary = new JLabel("Summary");
		summary.setBounds(30, 0, 200, 50);
		summary.setForeground(Color.WHITE);
		summary.setFont(new Font("Dialog", Font.BOLD, 14));
		summaryPane.add(summary);
		
		JLabel logOut = new JLabel("Log Out");
		logOut.setBounds(30, 0, 200, 50);
		logOut.setForeground(Color.WHITE);
		logOut.setFont(new Font("Dialog", Font.BOLD, 14));
		logOutPane.add(logOut);
		
		
		//-------//
		JPanel content = new JPanel();
		content.setBounds(200, 0, 1200, 750);
		add(content);	
		content.setLayout(null);
			
		content.add(sellPanel);
		content.add(newQuote);
		content.add(cashSales);
		content.add(invoicesPanel);
		content.add(iRecords);
		content.add(payPanel);
		content.add(quotations);		
		content.add(summaryPanel);
		
		salePane.addMouseListener(new PanelAdapter(salePane) {
			public void mouseClicked(MouseEvent e) {
				menuClicked(sellPanel);
			}
		});
		quotePane.addMouseListener(new PanelAdapter(quotePane) {
			public void mouseClicked(MouseEvent e) {
				menuClicked(newQuote);
			}
		});
		cashPane.addMouseListener(new PanelAdapter(cashPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(cashSales);
			}
		});
		PendInvoicesPane.addMouseListener(new PanelAdapter(PendInvoicesPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(invoicesPanel);
			}
		});
		allInvoicesPane.addMouseListener(new PanelAdapter(allInvoicesPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(iRecords);
			}
		});
		payPane.addMouseListener(new PanelAdapter(payPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(payPanel);
			}
		});
		quotesRecPane.addMouseListener(new PanelAdapter(quotesRecPane) {
			public void mouseClicked(MouseEvent e) {
				menuClicked(quotations);
			}
		});
		summaryPane.addMouseListener(new PanelAdapter(summaryPane){
			public void mouseClicked(MouseEvent e) {
				menuClicked(summaryPanel);
			}
		});
		logOutPane.addMouseListener(new PanelAdapter(logOutPane){
			public void mouseClicked(MouseEvent e) {
				if(JOptionPane.showConfirmDialog(null, "You will be Logged Out of the system "
						+ "\n \n" + "Do you wish to continue? \n ") == 0) {
					
					LogIn logIn = new LogIn();
					logIn.setVisible(true);
					Statics.setUSERID(0);
					TellerDashboard.this.dispose();
				}
			}
		});
		
	}
	
	public void menuClicked(JPanel panel) {
		sellPanel.setVisible(false);
		newQuote.setVisible(false);
		cashSales.setVisible(false);
		summaryPanel.setVisible(false);
		invoicesPanel.setVisible(false);
		iRecords.setVisible(false);
		payPanel.setVisible(false);
		quotations.setVisible(false);
		
		
		panel.setVisible(true);
	}
	
	public static void refreshData() {
		sellPanel.clear();
		cashSales.refresh();
		summaryPanel.refresh();
		invoicesPanel.refresh();
		iRecords.refresh();
		payPanel.refresh();
		quotations.refresh();
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
