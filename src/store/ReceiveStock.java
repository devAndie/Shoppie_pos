package store;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import singletons.Statics;
import models.Pricing;
import models.Product;

import java.awt.*;

public class ReceiveStock extends JPanel implements ActionListener  {
	
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
	private String[] columnNames = {
			"SN", "CODE", "ITEM NAME", "DESCRIPTION", "UNIT", "QUANTITY", "REMOVE"
	};
    private String[] units = { "", "Kg", "Metre", "Sqft", "Piece", "Pair", "Sheet", "Packet", "Full Length", "Roll" };

	ArrayList<String> productNames = new ArrayList<>();
	ArrayList<String> unitMeasure = new ArrayList<>();
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Pricing> pricingLst =  new ArrayList<>();
    int Code, pCode, SN =0;
	String ItemName, Name, Unit, Desc, Quant, sName, sContact;
	float Stock;
	
	private JTable table;
	private DefaultTableModel model;
	private JPanel tablePanel;
	private JComboBox productBx, unitBx;
	private JButton AddItem, chkIn;
	private JLabel QuantLbl, ItemLbl, unitJl, pDescLbl;
	private JTextField ItemTxt, QuantTxt, sNameTxt, sPhoneTxt;
	DefaultComboBoxModel comboModel;
	
	public ReceiveStock() {
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		JLabel title = new JLabel("ITEMS DELIVERY");
		title.setBounds(250, 0, 150, 30);
		title.setFont(new Font("Dialog", Font.BOLD, 15));
		add(title);
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
	    drawTable();
		
		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(20, 50, 600, 400);
		add(tablePane);
		
		JLabel supplierLbl =  new JLabel("SUPPLIER DETAILS");
		supplierLbl.setBounds(650, 50, 150, 30);
		supplierLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(supplierLbl);
		
		JLabel sNameLbl = new JLabel("Supplier Name");
		sNameLbl.setBounds(640, 90, 100, 30);
		add(sNameLbl);
				
		sNameTxt = new JTextField();
		sNameTxt.setBounds(640, 120, 150, 30);
		add(sNameTxt);
		
		JLabel sPhoneLbl = new JLabel("Contact");
		sPhoneLbl.setBounds(640, 160, 100, 30);
		add(sPhoneLbl);
		
		sPhoneTxt = new JTextField();
		sPhoneTxt.setBounds(640, 190, 150, 30);
		add(sPhoneTxt);
		
		JLabel pNameLbl = new JLabel("Product Name");
		pNameLbl.setBounds(50, 470, 100, 30);
		pNameLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(pNameLbl);
		
		ItemTxt = new JTextField();
		ItemTxt.setBounds(30, 505, 150, 30);
		ItemTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		ItemTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	searchItem(ItemTxt.getText());
	                }
	            });
	        }
		});
		add(ItemTxt);
		
		productBx = new JComboBox();
		productBx.setBounds(30, 535, 150, 20);
		productBx.addActionListener(this);
		add(productBx);
		
		JLabel pDesc = new JLabel("Description");
		pDesc.setBounds(200, 470, 100, 30);
		pDesc.setFont(new Font("Dialog", Font.BOLD, 15));
		add(pDesc);
		
		pDescLbl = new JLabel();
		pDescLbl.setBounds(200, 500, 100, 30);
		pDescLbl.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(pDescLbl);
		
		JLabel unitLbl = new JLabel("Unit");
		unitLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		unitLbl.setBounds(415, 470, 50, 30);
		add(unitLbl);
		
		unitBx = new JComboBox(units);
		unitBx.setBounds(380, 500, 100, 30);
		unitBx.addActionListener(this);
		add(unitBx);
				
		QuantLbl = new JLabel("Quantity");
		QuantLbl.setBounds(550, 470, 100, 30);
		QuantLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(QuantLbl);
		
		QuantTxt = new JTextField();
		QuantTxt.setBounds(530, 500, 100, 30);
		add(QuantTxt);
		
		AddItem = new JButton();
		AddItem.setBounds(550, 580, 100, 30);
		AddItem.setText("Add Item");
		AddItem.addActionListener(this);
		add(AddItem);
		
		chkIn = new JButton();
		chkIn.setBounds(680, 350, 100, 30);
		chkIn.setText("Check In");
		chkIn.addActionListener(this);
		add(chkIn);
		
		getItems("");
	}
	
	private void drawTable() {
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		table.setAutoCreateColumnsFromModel(true);
		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect double click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               int col = target.getSelectedColumn();
	               if(col == 6) { 
	            	   model.removeRow(row);
	            	   }
	            }
	         }	
	      });
		TableColumn column = null;
		for (int i = 0; i <= 6; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0 ) {
		    	column.setMaxWidth(50);
		    }else if(i == 2 | i == 3) {
		    	column.setMaxWidth(250);
		    }else if(i == 1 | i == 4 | i == 6) {
		    	column.setMaxWidth(60);
		    } else if(i == 5) {
		    	column.setMaxWidth(80);
		    }
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == productBx) {
			int index = productBx.getSelectedIndex();
			if(index != 0) {
				int i = index -1;
				Code = products.get(i).getID();
				Name = products.get(i).getName();
				Desc = products.get(i).getDesc();
//				Stock = products.get(i).getStock();
				
				ItemTxt.setText(Name);
				pDescLbl.setText(Desc);
				
				getPricing(Code);
			}
			
		} else if(e.getSource() == unitBx) {
			
			Unit = unitBx.getSelectedItem().toString();
			int index = unitBx.getSelectedIndex();
			if(index != 0  && pricingLst.size()!= 0) {
				int i = index -1;
				Unit = unitBx.getSelectedItem().toString();
				Code = pricingLst.get(i).getPrID();
			}
		} else if(e.getSource() == AddItem) {
			
			ItemName = ItemTxt.getText().toString().trim();
			Quant = QuantTxt.getText().toString();
			
			if(ItemName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Select item");
			} else if(Quant.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Add Quantity");
			} else {
				addItem();
			}
		} else if(e.getSource() == chkIn) {
			sName = sNameTxt.getText().toString();
			sContact = sPhoneTxt.getText().toString();
			
			if(model.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "No Items to Check In");
				
			} else if(sName.isEmpty() | sContact.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input All of the Suppliers' info");
				
			} else {
				writeData();
				reset();
			}
		}
		
	}

	private void searchItem(String text) {
		getItems(text);
		
		if (productNames.size() > 1) {
			productBx.setSelectedItem(text);
			productBx.showPopup();
		} else {
			productBx.hidePopup();
	    }
	}

	private void getItems(String enteredText) {
		productNames = new ArrayList<>();
		products = new ArrayList<>();
		int code = 0;
		float stock;
		String desc, unit;
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.products WHERE"
			        		+ "`pName` LIKE '%"+enteredText+"%'");
			resultSet = preparedStatement.executeQuery();
			
			productNames.add("");
			
			while (resultSet.next()) {
				String iName = resultSet.getString("pName");
				productNames.add(iName);
				
				code = resultSet.getInt("pId");
				desc = resultSet.getString("pDescription");
				stock = resultSet.getFloat("pStock");

				Product p = new Product(code, iName, desc);

//				Product p = new Product(code, iName, desc, stock);
				
				products.add(p);
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	    }
		if (productNames.size() > 1) {
			comboModel = new DefaultComboBoxModel();
			comboModel.addAll(productNames);
			productBx.setModel(comboModel);
		}
		
		
	}
	
	private void getPricing(int prodID) {
		unitMeasure = new ArrayList<>();
		pricingLst = new ArrayList<>();
		
		String Unit;
		float Price;
		int PrID;
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.pricing WHERE pId ='"+ Code+"'");
			resultSet = preparedStatement.executeQuery();
			
			unitMeasure.add("");
			
			while (resultSet.next()) {
				Unit = resultSet.getString("unit");
				PrID = resultSet.getInt("prId");
				Price = resultSet.getFloat("price");
				
				unitMeasure.add(Unit);

				Pricing pr = new Pricing(PrID, Unit, Price, Code);
				pricingLst.add(pr);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	        close();
	    }
				
		if (unitMeasure.size() > 1) {
			comboModel = new DefaultComboBoxModel();
			comboModel.addAll(unitMeasure);
			unitBx.setModel(comboModel);
			
			unitBx.setSelectedIndex(0);
			unitBx.showPopup();
		} else {
			unitBx.hidePopup();
	    }
	}
	
	private void addItem() {
		
		if(Name == "") {
			Name = ItemName;
			
			writeProduct(Name, Unit);
		}
		SN = SN +1;
				
		
		Object[] obj = {SN, Code, Name, Desc, Unit, Quant, "_x_"};
		
		model.addRow(obj);
		model.fireTableDataChanged();
		
		Code = 0;
		Name = "";
		Unit = "";
		Quant = "";
		ItemTxt.setText(Name);
		QuantTxt.setText(Quant);
		
		getItems("");
		productBx.setSelectedIndex(0);

		comboModel = new DefaultComboBoxModel();
		unitBx.setModel(comboModel);
	}
	

	private void writeProduct(String name, String unit) {
		int pId = 0;
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
            preparedStatement = connect
			        .prepareStatement("SELECT pId FROM hardware.products WHERE"
			        		+ "`pName` = '"+name+"'; ");
    		resultSet = preparedStatement.executeQuery();
    		
			while (resultSet.next()) {      		
				pId = resultSet.getInt("pId");
			}
			
			if(pId ==0) {
				preparedStatement = connect
				        .prepareStatement("INSERT INTO `hardware`.`products` (`pName`) VALUES ('"+name+"'); ");
				preparedStatement.executeUpdate();
				
				preparedStatement = connect
				        .prepareStatement("SELECT pId  FROM hardware.products WHERE pName ='"+name +"'; ");
	
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {      		
					pId = resultSet.getInt("pId");
				}
			}
			
			preparedStatement = connect
			        .prepareStatement("INSERT INTO `hardware`.`pricing` (`unit`, `price`, `pId`) "
			        		+ "VALUES ('"+ unit +"', '"+ pId +"'); ");
			preparedStatement.executeUpdate();
			
			preparedStatement = connect
			        .prepareStatement("SELECT prId  FROM hardware.pricing "
			        		+ "WHERE unit ='"+unit +"' AND pId ='"+ pId+"' ; ");

			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {      		
				Code = resultSet.getInt("prId");
			}
			        		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	        close();
	    }
	}
	
	private void writeData() {
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                            + "user=sqluser&password=sqluserpw");
            statement = connect.createStatement();
            
          //Get Supplier ID
            resultSet = statement
                    .executeQuery("SELECT sID FROM hardware.suppliers "
                    		+ "WHERE sName = '"+ sName +"' AND sContact ='"+ sContact+"';");
            int supID = 0;
            while (resultSet.next()) {
            	supID = resultSet.getInt("sID");
            }
            if (supID ==0) {
                //Write to Suppliers
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`suppliers` (`sName`, `sContact`) "
                        		+ "VALUES (?, ?)");
                preparedStatement.setString(1, sName);
                preparedStatement.setString(2, sContact);
                preparedStatement.executeUpdate();
                
                //Get Supplier ID
                resultSet = statement
                        .executeQuery("SELECT sID FROM hardware.suppliers "
                        		+ "WHERE sName = '"+ sName +"' AND sContact ='"+ sContact+"';");
                while (resultSet.next()) {
                	supID = resultSet.getInt("sID");
                }
            }
            
          //Get Current Date
            long millis = System.currentTimeMillis();  
            Date date = new Date(millis);
            int uID = Statics.getUSERID();
            
            //Write Delivery
            preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`deliveries` (`dDate`, `sID`, `uID`) "
                    		+ "VALUES (?, ?, ?)");
            preparedStatement.setDate(1, date);
            preparedStatement.setInt(2, supID);
            preparedStatement.setInt(3, uID);
            
            preparedStatement.executeUpdate();
            
          //Get Delivery Id
            resultSet = statement
                    .executeQuery("SELECT dID FROM hardware.deliveries "
                    		+ "WHERE dDate = '"+ date +"' AND uID = '"+ uID +"' "
                    		+ "ORDER BY dID DESC LIMIT 1");
            int dlvID = 0;
            while (resultSet.next()) {
            	dlvID = resultSet.getInt("dId");
            }
            
          //Write Delivery items
            int itemsNo = model.getRowCount();
            for(int i = 0; i < itemsNo; i++) {
            	
            	int prID = Integer.valueOf(
            			model.getValueAt(i, 1).toString());
            	
            	float dQuant = Float.valueOf(
            			model.getValueAt(i, 5).toString());
            	
            	
            	preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`delivery_items` (`ditQuantity`, `dID`, `prId`) "
                    		+ "VALUES ( ?, ?, ?)");
            	preparedStatement.setFloat(1, dQuant);
            	preparedStatement.setInt(2, dlvID);
            	preparedStatement.setInt(3, prID);
            	preparedStatement.executeUpdate();
            	
            	//GET STOCK
//            	resultSet = statement
//                        .executeQuery("SELECT p.pId, pStock FROM hardware.pricing pr "
//                        		+ "JOIN hardware.products p ON pr.pId = p.pId WHERE prId = '"+ prID );
//            	
//            	float stock = 0, upStock = 0;
//            	int prodID = 0;
//            	while (resultSet.next()) {
//            		stock = resultSet.getFloat("pStock");
//            		prodID = resultSet.getInt("pId");
//                }
            	// UPDATE STOCK
//            	upStock = stock + dQuant;
//            	preparedStatement = connect
//                        .prepareStatement("UPDATE `hardware`.`products` SET `pStock` = '"+ upStock
//                        		+"' WHERE (`pId` = '"+ prodID +"')");
//            	preparedStatement.executeUpdate();
            }
            
		} catch (Exception e) {
    		e.printStackTrace();
        } finally {
            close();
        }
	}
	
	private void reset() {
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		model.fireTableDataChanged();
		
		Code = 0; SN =0;
		Name = "";
		Stock = 0;
		Desc ="";
		
		Unit ="";
		Quant = "";
		sName = "";
		sContact = "";
		
		
		ItemTxt.setText(Name);
		QuantTxt.setText(Quant);
		pDescLbl.setText(Desc);
		sNameTxt.setText(sName);
		sPhoneTxt.setText(sContact);
		
		getItems("");
		productBx.setSelectedIndex(0);

		comboModel = new DefaultComboBoxModel();
		unitBx.setModel(comboModel);
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
