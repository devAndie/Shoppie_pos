package manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import singletons.UserView;

public class UsersPanel extends JPanel implements ActionListener {

	private static String[] columnNames = {
			"SN", "USER ID", "USER NAME", "DEPARTMENT"
	};
	private static String[] userLevels = {
			"", "Manager", "Sales Counter", "Store Keeper"
	};
	private String allRecords = "SELECT * FROM hardware.users";

	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    int SN = 0, uLvl, uId;
    
    String uName, uPass, cPass, lvl;
	private JLabel searchLbl, addLbl, nameLbl, deptLbl, accessPortLbl, passLbl, confPassLbl;
	private JPanel tablePanel;
	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt, nameTxt, passTxt;
	private JButton searchBtn, addBtn, refreshBtn;
	private JComboBox deptBx, portBx;

	public UsersPanel() {
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		
		JLabel tableLbl = new JLabel("USERS");
		tableLbl.setBounds(250, 5, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 16));
		add(tableLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(50, 50, 150, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
	        			
        				String searchSql = "SELECT * FROM hardware.users "
        						+ "WHERE uName LIKE '%"+txt +"%'";
        				fetchUsers(searchSql);
	        			
	                }
	            });
	        }
		});
		add(searchTxt);
		
		searchBtn = new JButton("SEARCH");
		searchBtn.setBounds(220, 50, 100, 30);
		searchBtn.addActionListener(this);
		add(searchBtn);
		
		refreshBtn = new JButton("Refresh");
		refreshBtn.setBounds(350, 50, 70, 30);
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
	            if (me.getClickCount() == 2) {     // to detect doble click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               showUser(row);
	            }
	         }
	      });
		
		JScrollPane scrollPane= new JScrollPane(table);
		scrollPane.setBounds(20, 90, 400, 550);
		add(scrollPane);
			
		int x = 500, y = 90, h = 30, m=20, w=100, wt = 200;
		
		addLbl = new JLabel("ADD NEW USER");
		addLbl.setBounds(550, 50, 200, 30);
		addLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(addLbl);
		
		nameLbl = new JLabel("User Name");
		nameLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		nameLbl.setBounds(x, 90, 100, 30);
		add(nameLbl);
		
		nameTxt = new JTextField();
		nameTxt.setBounds(x, 120, 200, 30);
		add(nameTxt);
		
		deptLbl = new JLabel("Department");
		deptLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		deptLbl.setBounds(x, 160, 100, 30);
		add(deptLbl);
		
		deptBx = new JComboBox(userLevels);
		deptBx.setBounds(x, 190, 200, 30);
		add(deptBx);
				
		passLbl = new JLabel("Password");
		passLbl.setBounds(x, 230, 200, 30);
		passLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		add(passLbl);
		
		passTxt = new JTextField();
		passTxt.setBounds(x, 260, 200, 30);
		add(passTxt);
		
		addBtn = new JButton("Add User");
		addBtn.setBounds(580, 400, 100, 30);
		addBtn.addActionListener(this);
		add(addBtn);
		
		fetchUsers(allRecords);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) {
				String searchSql = "SELECT * FROM hardware.users "
						+ "WHERE uName LIKE '%"+txt +"%'";
				fetchUsers(searchSql);
			}
		} else if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();
		} else if(e.getSource() == addBtn) {
			
			uName = nameTxt.getText().toString().strip();
			uLvl = deptBx.getSelectedIndex() + 1;
			lvl = deptBx.getSelectedItem().toString();
 
			uPass = passTxt.getText().toString().strip();
			
			if(uName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input User name");
			} else if(uPass.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input Password");
			} else if(lvl.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Select item");
			} else {
				addUser();
			}
			
			
		}
	}

	private void showUser(int row) {
		String Name, Dept, Uid;
		Uid = model.getValueAt(row, 1).toString();
		Name = model.getValueAt(row, 2).toString();
		Dept = model.getValueAt(row, 3).toString();
		
		new UserView(Uid, Name, Dept);
		
	}	
	
	private void drawTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		TableColumn column = null;
		for (int i = 0; i < 4; i++) {
		    column = table.getColumnModel().getColumn(i);    
		   
		    if(i == 0 |i == 1) {
		    	column.setMaxWidth(50);
		    	
		    }else if(i == 2) {
		    	column.setMaxWidth(200);
		    } else {
		    	column.setMaxWidth(150);
		    }
		    column.setCellRenderer(centerRenderer);
		}
	}
	private void fetchUsers(String query) {
		drawTable();
		
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	SN =0;
	    	while (resultSet.next()) {
	    		SN = SN +1;
	    		
	    		uId = resultSet.getInt("uID");
	    		String UNAME = resultSet.getString("uName");
	    		int uLvl = resultSet.getInt("uLevel");
	    		
	    		lvl = userLevels[uLvl -1];
	    		if(uLvl != 1) {
		    		Object[] user = {SN, uId, UNAME, lvl};
		    		
		    		model.addRow(user);
					model.fireTableDataChanged();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		
	}
	
	private void addUser() {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`users` (`uName`, `uPass`, `uLevel`)"
                    		+ " VALUES ( ?, ?, ?)");
			
			preparedStatement.setString(1, uName);
			preparedStatement.setString(2, uPass);
			preparedStatement.setInt(3, uLvl);
			
			preparedStatement.executeUpdate();
			
			SN = SN +1;
			uId = uId +1;
			Object[] user = {SN, uId, uName, lvl};
    		
    		model.addRow(user);
			model.fireTableDataChanged();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            close();
        }
		
		nameTxt.setText("");
		passTxt.setText("");
		deptBx.setSelectedIndex(0);
	}
	
	void refresh() {
		fetchUsers(allRecords);
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
