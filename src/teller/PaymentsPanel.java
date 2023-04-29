package teller;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.GetData;
import singletons.Statics;

public class PaymentsPanel extends JPanel implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
	
	int UID;
	long millis = System.currentTimeMillis();  
	Date date = new Date(millis);
	boolean todayOnly = false;
	
    private static String[] columnNames = {
			"SN", "PAY_NO", "CUSTOMER NAME", "CASH PAID", "MODE", "RECIEVED BY", "DATE"
			};
    private String select =  "SELECT p.payId, c.cName, p.cash, p.payDate, p.payMode, u.uName "
    		+"FROM hardware.payments p "
    		+"JOIN hardware.receipts r ON r.rId = p.rId "
    		+"JOIN hardware.customers c ON r.cId = c.cId "
    		+"JOIN hardware.users u ON p.uId = u.uId ";
    private String filter = "WHERE p.payDate = '"+ date + "' ";

    private String order = "ORDER BY p.payId DESC ";
    
    private String allRecords = select + order;
    private String todayRecords =  select + filter + order;
    
    private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn, filterBtn;
	private JComboBox yrBx, mnthBx, dayBx;
	private JCheckBox showChk;
	
	ArrayList<String> years, months, days;
	
	public PaymentsPanel() {
		setBounds(0, 0, 800, 750);
		setLayout(null);

		UID = Statics.getUSERID();
		millis = System.currentTimeMillis();  
        date = new Date(millis);
        
		JLabel tLbl = new JLabel("PAYMENTS");
		tLbl.setBounds(350, 20, 200, 30);
		tLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(300, 60, 150, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
        			
	                	String searchSql = "";
	    				if(todayOnly) {
	    					 searchSql = select + filter + "AND c.cName LIKE '%"+txt+"%' "+ order;
	    				} else {
	    					searchSql = select + "WHERE  c.cName LIKE '%"+txt+"%' "+ order;
	    				}
	    				
        				fetchRecords(searchSql);	                
	        		}
	            });
	        }
		});
		add(searchTxt);
		
		searchBtn = new JButton("SEARCH");
		searchBtn.setBounds(480, 60, 100, 30);
		searchBtn.addActionListener(this);
		add(searchBtn);
		
		refreshBtn = new JButton("Refresh");
		refreshBtn.setBounds(680, 60, 70, 30);
		refreshBtn.addActionListener(this);
		add(refreshBtn);
		
		
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
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
       table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row    
//	               showCashBuy(row);
	            }
	         }	
	      });
	    table.setAutoCreateColumnsFromModel(true);
				
		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(170, 100, 620, 550);
		add(tablePane);
		
		fetchData();
		
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
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) { 
				
				String searchSql = "";
				if(todayOnly) {
					 searchSql = select + filter + "AND c.cName LIKE '%"+txt+"%' "+ order;
				} else {
					searchSql = select + "WHERE  c.cName LIKE '%"+txt+"%' "+ order;
				}
				fetchRecords(searchSql);
			}
		} else if(e.getSource() == showChk) {
			if(showChk.isSelected()) {
				todayOnly = true;
				fetchData();
				
			} else {
				todayOnly = false;
				fetchData();
			}
			
		} else if(e.getSource() == refreshBtn) {
			TellerDashboard.refreshData();
		 
		} else if  (e.getSource() == yrBx) {
			if(yrBx.getSelectedIndex() >= 0) {
				months = GetData.getMonths(yrBx.getSelectedItem().toString());
				
				if(months.size() >=1) {
					DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
					comboModel.addAll(months);
					mnthBx.setModel(comboModel);
					
					mnthBx.setSelectedIndex(0);
				}
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
			showChk.setSelected(false);
		}
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
		
		fetchRecords(select
	    		+ "WHERE p.payDate = '"+dateFilter +"' " +order);
		
	}

	
	private void fetchData() {
		if(todayOnly) {
			fetchRecords(todayRecords);
		} else {
			fetchRecords(allRecords);
		}
	}
	
	private void fetchRecords(String query) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	drawTable();
	    	
	    	String cName = "", payCode = null, teller = null, mode = "";
	    	int SN = 0;
	    	float cash = 0.0f;
	    	Date date = null;
	    	while (resultSet.next()) {
	    		SN = SN+1;
	    		payCode = resultSet.getString("payId");
	    		cName = resultSet.getString("cName");
	    		cash = resultSet.getFloat("cash");
	    		mode = resultSet.getString("payMode");
	    		date = resultSet.getDate("payDate");
	    		teller = resultSet.getString("uName");


	    		Object[] record = {SN, payCode, cName, cash, mode, teller, date};
	    		
	    		model.addRow(record);
				model.fireTableDataChanged();
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
	}
	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		TableColumn column = null;
		for (int i = 0; i < 7; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(30);
		    	column.setCellRenderer(centerRenderer); 
		    } else if(i == 1) {
		    	column.setMaxWidth(60);
		    	column.setCellRenderer(centerRenderer); 
		    } else if(i == 2) {
		    	column.setMaxWidth(200);
//		    	column.setCellRenderer(centerRenderer); 
		    } else if(i ==3 ) {
		    	column.setMaxWidth(100);
//		    	column.setCellRenderer(centerRenderer);  	
		    } else {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer); 
		    }
		}
	}
	
	void refresh() {
		fetchData();
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
