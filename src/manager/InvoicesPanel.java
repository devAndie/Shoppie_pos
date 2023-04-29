package manager;

import java.awt.*;
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
import models.InvoiceModel;
import singletons.InvoiceView;
import singletons.InvoiceViewManager;


public class InvoicesPanel extends JPanel implements ActionListener {
	private static String[] columnNames = {
			"SN", "INVOICE NO", "CUSTOMER NAME", "ITEMS", "TOTAL", "BALANCE", "TELLER", "DATE"
			};
	long millis = System.currentTimeMillis();  
	Date date = new Date(millis);
	boolean todayOnly = false, withBalance = false;
	
	private String selector = "SELECT r.rId, c.cName, cContact, count(i.rId) AS items, "
			+ "r.rTotal, r.rBal AS balance, r.uID, r.rDate "
    		+ "FROM hardware.receipts r "
    		+ "LEFT JOIN hardware.customers c ON c.cId = r.cId "
    		+ "JOIN hardware.receipt_items i ON i.rId = r.rId "
    		+ "WHERE rType = 'invoice' ";
	private String filter = "AND r.rDate = '"+date +"' ";
	private String balFilter = "AND r.rBal > 0 ";
	private String group = "group by i.rId ORDER BY r.rId DESC ";
	
	private String allRecords = selector + group;
	private String todayRecords =  selector + filter + group;
	private String balRecords =  selector + filter + balFilter + group;
	private String show = "", bal = "";
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn, filterBtn;
	private JComboBox yrBx, mnthBx, dayBx;
	private JCheckBox showChk, balChk;
	private JScrollPane scrollPane;
	
	ArrayList<InvoiceModel> invoices;
	ArrayList<String> years, months, days;
	
	public InvoicesPanel(){
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		
		JLabel tableLbl = new JLabel("INVOICES");
		tableLbl.setBounds(350, 20, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tableLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(300, 60, 150, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
	        			
        				String searchSql = "";
	    				if(todayOnly) {
	    					if(withBalance) {
		    					 searchSql = selector + filter + balFilter + "AND c.cName LIKE '%"+txt+"%' "+ group;
	    					} else {
		    					 searchSql = selector + filter + "AND c.cName LIKE '%"+txt+"%' "+ group;
	    					}
	    				} else {
	    					if(withBalance) {
		    					 searchSql = selector + balFilter + "AND c.cName LIKE '%"+txt+"%' "+ group;
	    					} else {
		    					searchSql = selector + "AND c.cName LIKE '%"+txt+"%' "+ group;
	    					}
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
//		showChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
		add(showChk);

		balChk = new JCheckBox("Show Wth Balance Only");
		balChk.setBounds(20, 420, 150, 30);
		balChk.addActionListener(this);
//		balChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
		add(balChk);
		

		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
		table.setAutoCreateColumnsFromModel(true);
		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect doble click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               	               
	               new InvoiceViewManager(invoices.get(row));
	            }
	         }	
	      });
			
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(170, 100, 620, 550);
		add(scrollPane);
		
		fetchData();
		
		years = GetData.getYears();
		
		if(years.size() >=1) {
			DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
			comboModel.addAll(years);
			yrBx.setModel(comboModel);
			
			yrBx.setSelectedIndex(0);
			
			loadFilter();
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String searchSql = "";
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			
			if(!txt.isEmpty()) {
				
				if(todayOnly) {
					if(withBalance) {
    					 searchSql = selector + filter + balFilter + "AND c.cName LIKE '%"+txt+"%' "+ group;
					} else {
    					 searchSql = selector + filter + "AND c.cName LIKE '%"+txt+"%' "+ group;
					}
				} else {
					if(withBalance) {
    					 searchSql = selector + balFilter + "AND c.cName LIKE '%"+txt+"%' "+ group;
					} else {
    					searchSql = selector + "AND c.cName LIKE '%"+txt+"%' "+ group;
					}
				}
				fetchRecords(searchSql);
			}
		} else if(e.getSource() == showChk) {
			if(showChk.isSelected()) {
				todayOnly = true;
				if(withBalance) {
					 searchSql = selector + filter + balFilter + group;
				} else {
					 searchSql = selector + filter + group;
				}
			} else {
				todayOnly = false;
				if(withBalance) {
					 searchSql = selector  + balFilter + group;
				} else {
					 searchSql = selector + group;
				}
			}
			fetchRecords(searchSql);
			
		} else if(e.getSource() == balChk) {
			if(balChk.isSelected()) {
				withBalance = true;
				if(todayOnly) {
					searchSql = selector + filter + balFilter + group;
				}else {
					searchSql = selector  + balFilter + group;
				}
			} else {
				withBalance = false;
				searchSql = selector + group;
			}
			fetchRecords(searchSql);
		} else if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();


		} else if  (e.getSource() == yrBx) {
			loadFilter();

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
		}
	}
	
	private void loadFilter() {

		if(yrBx.getSelectedIndex() >= 0) {
			months = GetData.getMonths(yrBx.getSelectedItem().toString());
			
			if(months.size() >=1) {
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
				comboModel.addAll(months);
				mnthBx.setModel(comboModel);
				
				mnthBx.setSelectedIndex(0);
			}
		}
		
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

		String dateFilter = "AND r.rDate = '"+ year + month + day+"' ";
		
		String query = "";
		
		if(withBalance) {
			 query = selector + dateFilter + balFilter + group;
		} else {
			 query = selector + dateFilter + group;
		}
		
		fetchRecords(query);
		
	}
			
	void fetchData(){
		String query = "";
		
		if(todayOnly) {
			if(withBalance) {
				 query = selector + filter + balFilter + group;
			} else {
				 query = selector + filter + group;
			}
		} else {
			if(withBalance) {
				 query = selector  + balFilter + group;
			} else {
				 query = selector + group;
			}
		}
		fetchRecords(query);
	}
	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		TableColumn column = null;
		for (int i = 0; i < 8; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(30);
		    } else if(i == 1) {
		    	column.setMaxWidth(70);
		    } else if(i == 2) {
		    	column.setMaxWidth(250);
		    } else {
		    	column.setMaxWidth(100);
		    }
		    if(i !=2) {
		    	column.setCellRenderer(centerRenderer);  	
		    }
		}
	}
		
	private void fetchRecords(String query) {
		invoices = new ArrayList<>();
		
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	
	    	String invNo= "", cName = "", payment = "", status = "", 
	    			teller = "", cContact =""; 
	    	int SN = 0, itemsNo = 0 , counter = 0;
	    	String total = "", bal = "", date = null;
	    	
	    	drawTable();
			
	    	while (resultSet.next()) {
	    		SN = SN + 1;
	    		invNo = resultSet.getString("rId");
	    		cName = resultSet.getString("cName");
	    		cContact = resultSet.getString("cContact");
	    		itemsNo = resultSet.getInt("items");
	    		total = resultSet.getString("rTotal");
	    		counter = resultSet.getInt("uID");
	    		bal = resultSet.getString("balance");
	    		date = resultSet.getString("rDate");
	    		
	    		preparedStatement = connect
	                    .prepareStatement( "SELECT uName FROM hardware.users "
	                    		+ "WHERE uID = "+ counter);
				
				ResultSet rs = preparedStatement.executeQuery();
	    		while (rs.next()) {
	    			teller = rs.getString("uName");
				}
	    		rs.close();
	    		
	    		InvoiceModel inv = new InvoiceModel(invNo, cName, cContact, total, 
	    				bal, teller, date);
				invoices.add(inv );
	    		
	    		Object[] record = {SN, invNo, cName, itemsNo, total, bal, teller, date};

	    		model.addRow(record);
				model.fireTableDataChanged();
	    	}
	    	
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            close();
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
            if (preparedStatement != null) {
            	preparedStatement.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }


}
