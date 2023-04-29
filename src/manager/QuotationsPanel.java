package manager;

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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.GetData;
import models.QuoteModel;
import singletons.QuoteView;
import singletons.QuoteViewManager;

public class QuotationsPanel extends JPanel implements ActionListener {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    boolean todayOnly = false, allQuotes = false;
    long millis = System.currentTimeMillis();  
	Date date = new Date(millis);
	
    private static String[] columnNames = {
			"SN", "QUOTE_NO", "CUSTOMER NAME", "PROJECT", "TOTAL", "PREPARED BY", "DATE"
			};
    private String selector = "SELECT qId,  cName, cContact, qProject, qDate, qTotal, qInvoiced, "
    		+ "uName FROM hardware.quotations q LEFT JOIN hardware.customers c ON "
    		+ "c.cId = q.cId LEFT JOIN hardware.users u on q.uId = u.uID ";
    private String filter = " qInvoiced = 'NO' ";
    private String filter2 = " qDate = '"+ date +"' ";
    private String group = "GROUP BY qId ORDER BY qId DESC; ";
    
    private String unInvoiced = selector +" WHERE "+ filter + group;
    private String today = selector +" WHERE "+  filter +" AND "+ filter2 + group;
    private String todayAll = selector +" WHERE "+ filter2 + group;
    private String allRecords = selector  + group;
    
    private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt;
	private JButton searchBtn, refreshBtn, filterBtn;
	private JComboBox yrBx, mnthBx, dayBx;

	private JCheckBox showChk, todayChk;
	int SN = 0;
    ArrayList<QuoteModel> quotes;
    ArrayList<String> years, months, days;
    
    
	public QuotationsPanel() {
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		quotes = new ArrayList<>();
		
		JLabel tLbl = new JLabel("QUOTATIONS");
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
	        			if (allQuotes) {
	        				searchSql = selector 
	        						+ "WHERE c.cName LIKE '%"+ txt +"%' "
	        						+ group;
	        			} else {
	        				searchSql = selector +" WHERE "+  filter
	        						+ "AND c.cName LIKE '%"+ txt +"%' "
	        						+ group;
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
		refreshBtn.setBounds(700, 35, 70, 30);
		refreshBtn.addActionListener(this);
		add(refreshBtn);
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
        
	    table.setAutoCreateColumnsFromModel(true);
	    table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row    
	               
	               new QuoteViewManager(quotes.get(row));
	               
	            }
	         }	
	      });
	    		
		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(170, 100, 620, 550);
		add(tablePane);
		
		
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
		
		todayChk = new JCheckBox("Show Todays Only");
		todayChk.setBounds(30, 380, 130, 25);
		todayChk.addActionListener(this);
//		todayChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
		add(todayChk);

		showChk = new JCheckBox("Show Invoiced Quotes");
		showChk.setBounds(30, 420, 140, 25);
		showChk.addActionListener(this);
//		showChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 13));
		add(showChk);
		
		
		fetchRecords(unInvoiced);

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
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			String searchSql = "";
			
			if (allQuotes) {
				searchSql = selector + "WHERE c.cName LIKE '%"+ txt +"%' "
						+ group;
			} else {
				searchSql = selector +" WHERE "+  filter + "AND c.cName LIKE '%"+ txt +"%' "
						+ group;
			}
			fetchRecords(searchSql);
			
		} else if(e.getSource() == todayChk) {
			if( todayChk.isSelected()) {
				 todayOnly = true;
				 fetchData();
				 
			 }  else {
				 todayOnly = false;
				 fetchData();
			 }
			 
		} else if(e.getSource() == showChk) {
			 if( showChk.isSelected()) {
				 allQuotes = true;
				 fetchData();
				 
			 }  else {
				 allQuotes = false;
				 fetchData();
			 }
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

		String dateFilter = "qDate = '"+ year + month + day+"' ";
		
		String query = "";
		
		if(allQuotes) {
			 query = selector +"WHERE " +dateFilter + group;
		} else {
			 query = selector +"WHERE " + filter + " AND "+  dateFilter + group;
		}
		
		fetchRecords(query);
		
	}

	private void fetchData() {
		if(allQuotes) {
			if(todayOnly) {
				fetchRecords(todayAll);
			} else {
				fetchRecords(allRecords);
			}
		} else {
			if(todayOnly) {
				fetchRecords(today);
			} else {
				fetchRecords(unInvoiced);
			}
		}
	}

	
	void refresh() {
		fetchData();
	}

	private void drawTable() {
		quotes = new ArrayList<>();

		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
	
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
	
//		"SN", "QUOTE_NO", "CLIENT NAME", "PROJECT", "TOTAL", "PREPARED BY", "DATE"
		
		TableColumn column = null;
		for (int i = 0; i < 7; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(30);
		    	column.setCellRenderer(centerRenderer); 
		    } else if(i == 1) {
		    	column.setMaxWidth(80);
		    	column.setCellRenderer(centerRenderer); 
		    } else if(i == 2 | i ==3) {
		    	column.setMaxWidth(150);
		    } else {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer); 
		    }
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
			
			SN = 0;
			String qID, cName, cContact, project, total, teller, date, invoiced;
//			"SN", "QUOTE_NO", "CLIENT NAME", "PROJECT", "TOTAL", "PREPARED BY", "DATE"
			while (resultSet.next()) {
				SN = SN +1;
				qID = resultSet.getString("qId");
				cName = resultSet.getString("cName");
				cContact = resultSet.getString("cContact");
				project = resultSet.getString("qProject");
				total = resultSet.getString("qTotal");
				teller = resultSet.getString("uName");
				date = resultSet.getString("qDate");
				invoiced = resultSet.getString("qInvoiced");

				
				QuoteModel q = new QuoteModel(qID, cName, cContact, project, total, date, teller, invoiced);
				quotes.add(q);
				Object[] record = {SN, qID, cName, project, total, teller, date};
	    		
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

        }
    }
}
