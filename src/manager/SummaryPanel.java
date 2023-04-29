package manager;

import javax.swing.*;

import api.GetData;
import document.GenerateSummary;
import singletons.Statics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SummaryPanel extends JPanel implements ActionListener {
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

	private JLabel tLbl, customers, quotes, invoiceNo, cashNo, mpesa, cash, totalMoney;
	private Font Heading, Text;
	private JButton refreshBtn, filterBtn, printBtn;
	JComboBox yrBx, mnthBx, dayBx;
	JCheckBox showChk;
	
	ArrayList<String> years, months, days;
	
	Date date;
	int UID;
	long millis;
	
	public SummaryPanel(){
		setBounds(0, 0, 1100, 750);
		setLayout(null);
		
		UID = Statics.getUSERID();
		millis = System.currentTimeMillis();  
        date = new Date(millis);
		
		Heading = new Font("Dialog", Font.BOLD, 15);
		Text = new Font("Dialog", Font.ROMAN_BASELINE, 14);
        
		tLbl = new JLabel();
		tLbl.setBounds(200, 20, 300, 30);
		tLbl.setFont(new Font("Dialog", Font.BOLD, 16));
		add(tLbl);
		
		
		JLabel filter = new JLabel("Filter");
		filter.setBounds(50, 90, 70, 30);
		filter.setFont(new Font("Dialog", Font.BOLD, 14));
		add(filter);
		
		JLabel yr = new JLabel("Year");
		yr.setBounds(50, 135, 70, 30);
		add(yr);
		
		yrBx = new JComboBox();
		yrBx.setBounds(30, 165, 110, 30);
		yrBx.addActionListener(this);
		add(yrBx);
		
		JLabel mnth = new JLabel("Month");
		mnth.setBounds(50, 200, 70, 30);
		add(mnth);
		
		mnthBx = new JComboBox();
		mnthBx.setBounds(30, 230, 110, 30);
		mnthBx.addActionListener(this);
		add(mnthBx);

		JLabel day = new JLabel("Day");
		day.setBounds(50, 265, 70, 30);
		add(day);
		
		dayBx = new JComboBox();
		dayBx.setBounds(30, 295, 110, 30);
		dayBx.addActionListener(this);
		add(dayBx);
		
		filterBtn = new JButton("Filter");
		filterBtn.setBounds(70, 340, 70, 30);
		filterBtn.addActionListener(this); 
		add(filterBtn);

		showChk = new JCheckBox("Show Todays Only");
		showChk.setBounds(20, 380, 150, 30);
		showChk.addActionListener(this);
		add(showChk);

		
		refreshBtn = new JButton("Refresh");
		refreshBtn.setBounds(680, 60, 70, 30);
		refreshBtn.addActionListener(this);
		add(refreshBtn);
			
		JLabel tally = new JLabel("Activity");
		tally.setBounds(350, 90, 70, 30);
		tally.setFont(Heading);
		add(tally);
		
		int y1 = 120, y2 = 150, y3 = 200, y4 = 230, y5 = 260;
		
		JLabel customersLbl = new JLabel("Customers");
		customersLbl.setFont(Heading);
		customersLbl.setBounds(200, y1, 100, 30);
		add(customersLbl);
		
		customers = new JLabel();
		customers.setFont(Text);
		customers.setBounds(210, y2, 70, 30);
		add(customers);
		
		JLabel quotesLbl = new JLabel("Quotes");
		quotesLbl.setBounds(300, y1, 70, 30);
		quotesLbl.setFont(Heading);
		add(quotesLbl);
		
		quotes = new JLabel("0");
		quotes.setBounds(310, y2, 70, 30);
		quotes.setFont(Text);
		add(quotes);
		
		JLabel invoicesLbl = new JLabel("Invoices"); 
		invoicesLbl.setFont(Heading);
		invoicesLbl.setBounds(400, y1, 70, 30);
		add(invoicesLbl);
		
		invoiceNo = new JLabel(); 
		invoiceNo.setFont(Text);
		invoiceNo.setBounds(410, y2, 70, 30);
		add(invoiceNo);

		JLabel receiptsLbl = new JLabel("Receipts"); 
		receiptsLbl.setFont(Heading);
		receiptsLbl.setBounds(500, y1, 70, 30);
		add(receiptsLbl);
		
		cashNo = new JLabel(); 
		cashNo.setFont(Text);
		cashNo.setBounds(510, y2, 70, 30);
		add(cashNo);
		
		//
		JLabel cashInLbl = new JLabel("Money In");
		cashInLbl.setFont(Heading);
		cashInLbl.setBounds(350, y3, 100, 30);
		add(cashInLbl);
		
		JLabel mpesaLbl = new JLabel("M-Pesa");
		mpesaLbl.setFont(Heading);
		mpesaLbl.setBounds(200, y4, 100, 30);
		add(mpesaLbl);

		mpesa =  new JLabel();
		mpesa.setFont(Text);
		mpesa.setBounds(200, y5, 150, 30);
		add(mpesa);
		
		JLabel cashLbl  = new JLabel("Cash ");
		cashLbl.setFont(Heading);
		cashLbl.setBounds(370, y4, 100, 30);
		add(cashLbl);
		
		cash  = new JLabel();
		cash.setFont(Text);
		cash.setBounds(370, y5, 150, 30);
		add(cash);
		
		JLabel total= new JLabel("Total");
		total.setBounds(540, y4, 70, 30);
		total.setFont(Heading);
		add(total);
		
		totalMoney  = new JLabel();
		totalMoney.setFont(Text);
		totalMoney.setBounds(540, y5, 100, 30);
		add(totalMoney);
						
		printBtn = new JButton("Print");
		printBtn.setBounds(500, 400, 100, 30);
		printBtn.addActionListener(this);
		add(printBtn);
		
		fetchRecords();
	
		
		years = GetData.getYears();
		
		if(years.size() >=1) {
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
			comboModel.addAll(years);
			yrBx.setModel(comboModel);
			
			yrBx.setSelectedIndex(0);
			
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();

		} else if  (e.getSource() == showChk) {
			if(showChk.isSelected()) {
				date = new Date(millis);
				
				fetchRecords();

			} 
			
		} else if  (e.getSource() == yrBx) {
			
			months = GetData.getMonths(yrBx.getSelectedItem().toString());
			
			if(months.size() >=1) {
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
				comboModel.addAll(months);
				mnthBx.setModel(comboModel);
				
				mnthBx.setSelectedIndex(0);
			}
			
		} else if  (e.getSource() == mnthBx) {
			if(mnthBx.getSelectedIndex() >= 0) {
				String year = yrBx.getSelectedItem().toString();
				String month = mnthBx.getSelectedItem().toString();
				
				if(Integer.valueOf(month) < 10) {
					days = GetData.getDays(year+"-0"+month);
				} else {
					days = GetData.getDays(year+"-"+month);	
				}

				if(days.size() >=1) {
					DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
					comboModel.addAll(days);
					dayBx.setModel(comboModel);
					
					dayBx.setSelectedIndex(0);
				}
			}
			
		} else if  (e.getSource() == yrBx) {
				
		} else if (e.getSource() == filterBtn) {
			filter();
			if(showChk.isSelected()) {showChk.setSelected(false);}
			
		} else if (e.getSource() == printBtn) {
			printSummary();
		
		}
	}
	
	private void printSummary() {

		String CustNo = customers.getText().toString(),
				qNo =  quotes.getText().toString(),
				invNo = invoiceNo.getText().toString(), 
				csNo = cashNo.getText().toString(),
				mMoney = mpesa.getText().toString(), 
				cMoney = cash.getText().toString(),
				tMoney = totalMoney.getText().toString();
		String Date = String.valueOf(date);

		new GenerateSummary(Date, CustNo, qNo, invNo, csNo, mMoney, cMoney, tMoney);
		JOptionPane.showMessageDialog(null, "Pdf File geneated successfully ");

	}

	private void filter() {
		String year = yrBx.getSelectedItem().toString();
		String month = mnthBx.getSelectedItem().toString();
		String day = dayBx.getSelectedItem().toString();
		
		if(Integer.valueOf(month) < 10) {
			month = "-0"+ month;
		} else {
			month = "-"+ month;	
		}
		if(Integer.valueOf(day) < 10) {
			day = "-0"+ day;
		} else {
			day = "-"+ day;	
		}

		String dateFilter = year + month + day;
		
		date = Date.valueOf(dateFilter);
		
		fetchRecords();
		
	}


	void fetchRecords() {
		
		tLbl.setText("Summary for date: "+ date);
        
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			
			resultSet = statement
	                .executeQuery("SELECT COUNT(cName) AS customers FROM hardware.customers c "
	                		+ "JOIN hardware.receipts r ON c.cId = r.cId "
	                		+ "Where r.rDate = '"+ date +"' ");
			
			while (resultSet.next()) {				
	            customers.setText(resultSet.getString("customers"));
			}
			
			//Quotes
			resultSet = statement
	                .executeQuery("SELECT COUNT(qId) AS quotes, SUM(qTotal) AS total "
	                		+ "FROM hardware.quotations "
	                		+ "WHERE qDate = '"+ date +"' ");
			
			while (resultSet.next()) {				
				 quotes.setText(resultSet.getString("quotes"));

			}
			
			
			//Invoices 
			resultSet = statement
	                .executeQuery("SELECT COUNT(rId) AS invoices, SUM(rTotal) AS total "
	                		+ "FROM hardware.receipts "
	                		+ "WHERE rType = 'invoice' AND rDate = '"+ date +"' ");
			while (resultSet.next()) {				
				 invoiceNo.setText(resultSet.getString("invoices"));

			}
			//receipts
			resultSet = statement
	                .executeQuery("SELECT COUNT(rId) AS buys "
	                		+ "FROM hardware.receipts  "
	                		+ "WHERE rType = 'cash' AND rDate = '"+ date +"' ");
			while (resultSet.next()) {
				cashNo.setText(resultSet.getString("buys"));
			}
			//M-Pesa
			resultSet = statement
	                .executeQuery("SELECT IFNULL(SUM(cash), 0)  AS total FROM hardware.payments "
	                		+ "WHERE payDate = '"+ date +"' AND payMode = 'M-Pesa'  " );
			while (resultSet.next()) {				
				mpesa.setText(resultSet.getString("total"));
			}
			//cash
			resultSet = statement
	                .executeQuery("SELECT IFNULL(SUM(cash), 0)  AS total FROM hardware.payments "
	                		+ "WHERE payDate = '"+ date +"' AND payMode = 'cash'  " );
			while (resultSet.next()) {				
				cash.setText(resultSet.getString("total"));
			}
			//totalMoney
			resultSet = statement
	                .executeQuery("SELECT IFNULL(SUM(cash), 0)  AS total FROM hardware.payments "
	                		+ "WHERE payDate = '"+ date +"' ");
			while (resultSet.next()) {				
				totalMoney.setText(resultSet.getString("total"));
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
	}
	
	void refresh() {
		fetchRecords();
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
