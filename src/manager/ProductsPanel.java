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
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import singletons.ProductView;

public class ProductsPanel extends JPanel implements ActionListener {
	private static String[] columnNames = {
			"CODE", "ITEM NAME", "DESCRIPTION", "UNIT", "SELL PRICE"
	};
	private static String[] unitBundle = {
			"", "No", "g", "Kg", "Mtr"
	};
	
	private String selector = "SELECT i.pId, pName, pDescription, unit, price "
			+ "FROM hardware.products i "
			+ "RIGHT JOIN hardware.pricing s ON s.pId = i.pId;";
	
	private String allRecords = selector;
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
	
    private String Name, Description, Unit, buy, sell, quant;
    float bp, sp, stock;
    int SN;
    
	private String[][] data = null;
	private JLabel addLbl, nameLbl, unitLbl, descLbl, sellLbl;
	private JTable table;
	private DefaultTableModel model;
	private JTextField searchTxt, nameTxt, descTxt, buyTxt, kgTxt, mtrTxt,
	pcTxt, sqftTxt, shtTxt, flTxt, rlTxt, stTxt, pTxt, pktTxt, bktTxt;
	private JButton searchBtn, addBtn, refreshBtn;
	private JComboBox unitBx;
	private JCheckBox kgChk, mtrChk, pcChk, sqftChk, shtChk, flChk, rlChk, 
	stChk, pChk, pktChk, bktChk;
	
	public ProductsPanel() {
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		
		JLabel tableLbl = new JLabel("PRODUCTS ON SALE");
		tableLbl.setBounds(250, 5, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 16));
		add(tableLbl);
		
		searchTxt = new JTextField();
		searchTxt.setBounds(80, 50, 150, 30);
		searchTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	String txt = searchTxt.getText().strip();
	        			
        				String searchSql = selector + "WHERE pName LIKE '%"+txt+"%' ";
        				fetchRecords(searchSql);
	                }
	            });
	        }
		});
		add(searchTxt);
		
		searchBtn = new JButton("SEARCH");
		searchBtn.setBounds(250, 50, 100, 30);
		searchBtn.addActionListener(this);
		add(searchBtn);
		
		refreshBtn = new JButton("Refresh");
		refreshBtn.setBounds(400, 50, 70, 30);
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
	               
	            	int row = table.getSelectedRow();
	               
	               showProduct(row);
	            }
	         }	
	      }
		);

		JScrollPane scrollPane= new JScrollPane(table);
		scrollPane.setBounds(10, 90, 500, 550);
		add(scrollPane);
		
		addLbl = new JLabel("ADD NEW ITEM");
		addLbl.setBounds(600, 20, 200, 30);
		addLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(addLbl);
		
		int x = 550, x2 = 650, y = 90, 
				h = 30, m=20, w=100, wt = 200;
		
		
		nameLbl = new JLabel("Item Name");
		nameLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		nameLbl.setBounds(x, 60, w, h);
		add(nameLbl);
		
		nameTxt = new JTextField();
		nameTxt.setBounds(x, 90, wt, h);
		add(nameTxt);
		
		descLbl = new JLabel("Description");
		descLbl.setBounds(x, 130, wt, h);
		descLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		add(descLbl);
		
		descTxt = new JTextField();
		descTxt.setBounds(x, 160, wt, h);
		add(descTxt);
		
		unitLbl = new JLabel("Unit");
		unitLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		unitLbl.setBounds(x, 195, 70, h);
		add(unitLbl);
		
		sellLbl = new JLabel("Price per 1 Unit (KSh)");
		sellLbl.setFont(new Font("Dialog", Font.TRUETYPE_FONT, 14));
		sellLbl.setBounds(640, 195, 150, h);
		add(sellLbl);
		
		int uy1=230, uy2=265, uy3=300, uy4=335, uy5=370, uy6=405, 
				uy7=440, uy8=475, uy9=510, uy10=545, uy11=580, uy12 = 620;
	
//		"Kg"
		kgChk = new JCheckBox("Kg");
		kgChk.setBounds(x, uy1, 70, h);
		kgChk.addActionListener(this);
		kgChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(kgChk);
		kgTxt = new JTextField();
		kgTxt.setBounds(x2, uy1, 100, h);
//		add(kgTxt);

//		"Metre"
		mtrChk= new JCheckBox("Metre");
		mtrChk.setBounds(x, uy2, 70, h);
		mtrChk.addActionListener(this);
		mtrChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(mtrChk);
		mtrTxt = new JTextField();
		mtrTxt.setBounds(x2, uy2, 100, h);
//		add(mtrTxt);
		
//		"Piece"
		pcChk = new JCheckBox("Piece");
		pcChk.setBounds(x, uy3, 70, h);
		pcChk.addActionListener(this);
		pcChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(pcChk);
		pcTxt = new JTextField();
		pcTxt.setBounds(x2, uy3, 100, h);
//		add(pcTxt);

//		"Sqft"
		sqftChk = new JCheckBox("Square Ft");
		sqftChk.setBounds(x, uy4, 80, h);
		sqftChk.addActionListener(this);
		sqftChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(sqftChk);
		sqftTxt = new JTextField();
		sqftTxt.setBounds(x2, uy4, 100, h);
//		add(sqftTxt);

//		"Sheet"
		shtChk = new JCheckBox("Sheet");
		shtChk.setBounds(x, uy5, 80, h);
		shtChk.addActionListener(this);
		shtChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(shtChk);
		shtTxt = new JTextField();
		shtTxt.setBounds(x2, uy5, 100, h);

//		"Full Length"
		flChk = new JCheckBox("Full Length");
		flChk.setBounds(x, uy6, 100, h);
		flChk.addActionListener(this);
		flChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(flChk);
		flTxt = new JTextField();
		flTxt.setBounds(x2, uy6, 100, h);
//		"Roll"
		rlChk = new JCheckBox("Roll");
		rlChk.setBounds(x, uy7, 80, h);
		rlChk.addActionListener(this);
		rlChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(rlChk);
		rlTxt = new JTextField();
		rlTxt.setBounds(x2, uy7, 100, h);
		
//		Set
		stChk = new JCheckBox("Set");
		stChk.setBounds(x, uy8, 80, h);
		stChk.addActionListener(this);
		stChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(stChk);
		stTxt = new JTextField();
		stTxt.setBounds(x2, uy8, 100, h);
		
//		Pair
		pChk = new JCheckBox("Pair");
		pChk.setBounds(x, uy9, 80, h);
		pChk.addActionListener(this);
		pChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(pChk);
		pTxt = new JTextField();
		pTxt.setBounds(x2, uy9, 100, h);
		
//		Packet
		pktChk = new JCheckBox("Packet");
		pktChk.setBounds(x, uy10, 80, h);
		pktChk.addActionListener(this);
		pktChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(pktChk);
		pktTxt = new JTextField();
		pktTxt.setBounds(x2, uy10, 100, h);
		
//		Bucket
		bktChk = new JCheckBox("Bucket");
		bktChk.setBounds(x, uy11, 80, h);
		bktChk.addActionListener(this);
		bktChk.setFont(new Font("Dialog", Font.ROMAN_BASELINE, 12));
		add(bktChk);
		bktTxt = new JTextField();
		bktTxt.setBounds(x2, uy11, 100, h);
		
		
		
		addBtn = new JButton("Add Item");
		addBtn.setBounds(680, uy12, 100, 30);
		addBtn.addActionListener(this);
		add(addBtn);

		fetchRecords(allRecords);
		
	}

	protected void showProduct(int row) {
		String pId, name, desc, unit, sell;
		
		pId = model.getValueAt(row, 0).toString();
		name = model.getValueAt(row, 1).toString();
		
		
		if (model.getValueAt(row, 2) != null) {
			desc = model.getValueAt(row, 2).toString();
	
		} else {
			desc = " ";
		}
		if (model.getValueAt(row, 3) != null) {
			unit = model.getValueAt(row, 3).toString();
			
		} else {			
			unit = " ";
		}
		if (model.getValueAt(row, 2) != null){
			sell = model.getValueAt(row, 4).toString();
			
		} else {
			sell = " ";
		}
		
		new ProductView(pId, name, desc, unit, sell);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchBtn) {
			String txt = searchTxt.getText().strip();
			if(!txt.isEmpty()) {
				String searchSql = selector + "WHERE pName LIKE '%"+txt+"%' ";
				fetchRecords(searchSql);
			}
			
		} else if(e.getSource() == refreshBtn) {
			ManagerDashboard.refresh();
			
		} else if(e.getSource() == kgChk) {
			if(kgChk.isSelected()) {
				add(kgTxt);
				repaint();
			} else {
				remove(kgTxt);
				repaint();
			}
		} else if(e.getSource() == mtrChk) {
			if(mtrChk.isSelected()) {
				add(mtrTxt);
				repaint();
			}else {
				remove(mtrTxt);
				repaint();
			}
		} else if(e.getSource() == pcChk) {
			if(pcChk.isSelected()) {
				add(pcTxt);
				repaint();
			}else {
				remove(pcTxt);
				repaint();
			}
		} else if(e.getSource() == sqftChk) {
			if(sqftChk.isSelected()) {
				add(sqftTxt);
				repaint();
			}else {
				remove(sqftTxt);
				repaint();
			}
		} else if(e.getSource() == shtChk) {
			if(shtChk.isSelected()) {
				add(shtTxt);
				repaint();
			}else {
				remove(shtTxt);
				repaint();
			}
		} else if(e.getSource() == flChk) {
			if(flChk.isSelected()) {
				add(flTxt);
				repaint();
			}else {
				remove(flTxt);
				repaint();
			}
		} else if(e.getSource() == stChk) {
			if(stChk.isSelected()) {
				add(stTxt);
				repaint();
			}else {
				remove(stTxt);
				repaint();
			}
		} else if(e.getSource() == rlChk) {
			if(rlChk.isSelected()) {
				add(rlTxt);
				repaint();
			}else {
				remove(rlTxt);
				repaint();
			}
		} else if(e.getSource() == pChk) {
			if(pChk.isSelected()) {
				add(pTxt);
				repaint();
			}else {
				remove(pTxt);
				repaint();
			}
		} else if(e.getSource() == pChk) {
			if(pChk.isSelected()) {
				add(pTxt);
				repaint();
			}else {
				remove(pTxt);
				repaint();
			}
		} else if(e.getSource() == pktChk) {
			if(pktChk.isSelected()) {
				add(pktTxt);
				repaint();
			}else {
				remove(pktTxt);
				repaint();
			}
		} else if(e.getSource() == bktChk) {
			if(bktChk.isSelected()) {
				add(bktTxt);
				repaint();
			}else {
				remove(bktTxt);
				repaint();
			}
			
		} else if(e.getSource() == addBtn) {
			Name = nameTxt.getText().toString().strip();
			Description = descTxt.getText().toString().strip();
            
			if(Name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input Product name");
				
			} else if(Description.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input Product Description");
				
			} else {
				
	            addProduct();
			}
		}

	}
	
    public void addProduct() {
    	try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                            + "user=sqluser&password=sqluserpw");
            
            preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`products` (`pName`, `pDescription`) "
                    		+ "VALUES (?, ?)");
            
            preparedStatement.setString(1, Name);
            preparedStatement.setString(2, Description);
                        
            preparedStatement.executeUpdate();
            
            resultSet = preparedStatement
                    .executeQuery("select pId FROM `hardware`.`products` WHERE pName = '"
                    		+ Name +"'");
            int pID = 0;
            while (resultSet.next()) {
            	pID = resultSet.getInt("pId");
            }
            if(kgChk.isSelected()) {
            	double kgSell = Double.valueOf(kgTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Kg");
				preparedStatement.setDouble(2, kgSell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Kg", kgSell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(mtrChk.isSelected()) {
            	double mtrSell = Double.valueOf(mtrTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Metre");
				preparedStatement.setDouble(2, mtrSell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Metre", mtrSell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(pcChk.isSelected()) {
            	double pcSell = Double.valueOf(pcTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Piece");
				preparedStatement.setDouble(2, pcSell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Piece", pcSell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(sqftChk.isSelected()) {
            	double sqftSell = Double.valueOf(sqftTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Sqft");
				preparedStatement.setDouble(2, sqftSell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Sqft", sqftSell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(shtChk.isSelected()) {
            	double Sell = Double.valueOf(shtTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Sheet");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Sheet", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(flChk.isSelected()) {
            	double Sell = Double.valueOf(flTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Full Length");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Full Length", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(rlChk.isSelected()) {
            	double Sell = Double.valueOf(rlTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Roll");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Roll", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            
            if(stChk.isSelected()) {
            	double Sell = Double.valueOf(stTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Set");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Set", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(pChk.isSelected()) {
            	double Sell = Double.valueOf(pTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Pair");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Pair", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(pktChk.isSelected()) {
            	double Sell = Double.valueOf(pktTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Packet");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Packet", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            if(bktChk.isSelected()) {
            	double Sell = Double.valueOf(bktTxt.getText().toString().trim());
            	
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
                        		+ "VALUES (?, ?, ?);");
                
                preparedStatement.setString(1, "Bucket");
				preparedStatement.setDouble(2, Sell);
				preparedStatement.setInt(3, pID);
				
				preparedStatement.executeUpdate();
				
				SN = SN+1;
	            Object[] product = {pID, Name, Description, "Bucket", Sell};
	    		
	    		model.addRow(product);
				model.fireTableDataChanged();
            }
            
    	} catch (Exception e) {
			e.printStackTrace();
        } finally {
            close();
        }
    	
    	clear();
    }
    
	void fetchRecords(String query) {
    	try {
	    	connect = DriverManager
	                .getConnection("jdbc:mysql://localhost/hardware?"
	                        + "user=sqluser&password=sqluserpw");
	    	
	    	statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	        paintTable(resultSet);
	    	
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }
    
    private void paintTable(ResultSet rs) {	
    	drawTable();
		try {
			SN = 0;
			while (rs.next()) {
				SN = SN+1;
				String code = rs.getString("pId");
				String name = rs.getString("pName");
				String Description = rs.getString("pDescription");
				String Unit = rs.getString("Unit");
				String SellPrice  = rs.getString("price");

				Object[] product = {code, name, Description, Unit, SellPrice};
				
				model.addRow(product);
				model.fireTableDataChanged();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		for (int i = 0; i < 5; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 | i == 3 ) {
		    	column.setMaxWidth(50);
		    	column.setCellRenderer(centerRenderer);
		    } else if(i == 1 | i == 2) {
		    	column.setMaxWidth(150);
		    } else {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer);
		    }
		    
		}
    }
    
    
    void refresh() {
    	fetchRecords(allRecords);
    }
    
    private void clear() {
		Name = null; Description = null; Unit = null; bp  = 0; sp = 0; stock = 0;
		nameTxt.setText("");
		descTxt.setText("");
		kgTxt.setText("");
		mtrTxt.setText("");
		pcTxt.setText("");
		sqftTxt.setText("");
		shtTxt.setText("");
		flTxt.setText("");
		rlTxt.setText("");
		stTxt.setText("");
		pTxt.setText("");
		pktTxt.setText("");
		bktTxt.setText("");
		
		kgChk.setSelected(false);
		mtrChk.setSelected(false);
		pcChk.setSelected(false);
		sqftChk.setSelected(false);
		shtChk.setSelected(false);
		flChk.setSelected(false);
		rlChk.setSelected(false);
		stChk.setSelected(false);
		pChk.setSelected(false);
		pktChk.setSelected(false);
		bktChk.setSelected(false);
		
		remove(kgTxt);
		remove(mtrTxt);
		remove(pcTxt);
		remove(sqftTxt);
		remove(shtTxt);
		remove(flTxt);
		remove(rlTxt);
		remove(stTxt);
		remove(pTxt);
		remove(pktTxt);
		remove(bktTxt);
		
		repaint();
		
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
