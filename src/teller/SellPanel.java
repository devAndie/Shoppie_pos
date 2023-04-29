package teller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import api.ConvertMeasure;
import api.ProductData;
import api.WriteSaleData;
import models.Pricing;
import models.Product;
import models.SaleItem;

public class SellPanel  extends JPanel implements ActionListener {
	private int SN = 0, tblH = 500, tblW = 800, txtbxH = 30, txtBxW = 100, p1 = 10, p2 = 20;

	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    private String[] columnNames = {
			"SN", "ITEM", "QUANTITY", "UNIT", "PRICE",  "TOTAL","Remove "
	};
    ArrayList<String> units;
    ArrayList<String> productNames;
    ArrayList<Product> products;
    ArrayList<String> unitMeasure;
    ArrayList<Pricing> pricingLst;
    ArrayList<SaleItem> items;
    
	DefaultComboBoxModel comboModel;

	private JLabel pDescLbl, receiptTtlLbl, SubTotalLbl, SubTotal, receiptTtl,
					QuantLbl, ItemLbl;
	private JTextField ItemTxt, QuantTxt, sellTxt;
	JComboBox productBx, csModeBx, unitBx;
	private JButton addPc, AddItem, chkOut, clrBtn;
	JLabel msmtLbl, lLbl, wLbl, pcsLbl;
	JTextField lTxt, wTxt, pcsTxt;
	JComboBox msmtBx;
	String[] measurements = { "Feet", "Inches", "Millimeters" };
	String leng, wid, pc, measure;
	boolean dimensLoaded = false;
	
	private JTable table;
	private DefaultTableModel model;
	private String dimens, ItemName, Quantity, Sell, Name, Desc, Unit;
	int Code;
	float sellp, quant, Stock, ItemSubTotal, Total, csPay, csChange;
	
	public SellPanel() {
		units = new ArrayList<>();
		productNames = new ArrayList<>();
	    products = new ArrayList<>();
	    unitMeasure = new ArrayList<>();
	    pricingLst =  new ArrayList<>();
	    items = new ArrayList<>();
	    
	    units.add("");			units.add("Kg");
	    units.add("Metre");		units.add("Feet");	units.add("Sqft");
	    units.add("Piece");		units.add("Pair");  units.add("Set");
	    units.add("Packet");    units.add("Full Length");	units.add("Sheet");
	    units.add("Roll");		    units.add("Bucket");
	    
	    sellp = 0; quant = 0; Total = 0.0f;
	    Code = 0;
	    Name = "";  dimens = "";
	    Desc = ""; measure = "";
	    Quantity = "0";
	    
		setBounds(0, 0, 800, 750);
		setLayout(null);
		
		JLabel tableLbl = new JLabel("NEW SALE");
		tableLbl.setBounds(250, 5, 200, 30);
		tableLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tableLbl);
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
	    drawTable();
	    
	    JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(20, 40, 600, 340);
		add(tablePane);
		
		receiptTtlLbl = new JLabel("Receipt Total");
		receiptTtlLbl.setBounds(680, 100, 100, 30);
		receiptTtlLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(receiptTtlLbl);
		
		receiptTtl = new JLabel();
		receiptTtl.setBounds(680, 140, 100, 50);
		receiptTtl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(receiptTtl);
		
		chkOut = new JButton();
		chkOut.setBounds(680, 250, 100, 30);
		chkOut.setText("Check Out");
		chkOut.addActionListener(this);
		add(chkOut);
		
		ItemLbl = new JLabel("Item");
		ItemLbl.setBounds(70, 400, 100, 30);
		ItemLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(ItemLbl);
		
		ItemTxt = new JTextField();
		ItemTxt.setBounds(20, 435, 150, 30);
		ItemTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(ItemTxt);
		
		productBx = new JComboBox();
		productBx.setBounds(20, 465, 150, 20);
		productBx.addActionListener(this);
		add(productBx);
		
		JLabel pDesc = new JLabel("Description");
		pDesc.setBounds(200, 400, 100, 30);
		pDesc.setFont(new Font("Dialog", Font.BOLD, 15));
		add(pDesc);
		
		pDescLbl = new JLabel();
		pDescLbl.setBounds(185, 435, 100, 30);
		pDescLbl.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(pDescLbl);
		
		JLabel unitLbl = new JLabel("Unit");
		unitLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		unitLbl.setBounds(350, 400, 50, 30);
		add(unitLbl);
		
		unitBx = new JComboBox();
		unitBx.setBounds(320, 435, 100, 30);
		unitBx.addActionListener(this);
		add(unitBx);
		
		QuantLbl = new JLabel("Quantity");
		QuantLbl.setBounds(450, 400, 70, 30);
		QuantLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(QuantLbl);
		
		QuantTxt = new JTextField();
		QuantTxt.setBounds(450, 435, 70, 30);
		QuantTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(QuantTxt);
		
		JLabel atLbl = new JLabel("Price");
		atLbl.setBounds(570, 400, 50, 30);
		atLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(atLbl);
		
		sellTxt = new JTextField();
		sellTxt.setBounds(550, 435, 100, 30);
		sellTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(sellTxt);
				
		SubTotalLbl = new JLabel("Sub Total");
		SubTotalLbl.setBounds(680, 400, 100, 30);
		SubTotalLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(SubTotalLbl);
		
		SubTotal= new JLabel();
		SubTotal.setBounds(680, 435, 100, 30);
		SubTotal.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(SubTotal);
		
		
		
		msmtLbl = new JLabel("Measure");
		msmtLbl.setBounds(210, 475, 70, 30);
		msmtLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
				
		msmtBx = new JComboBox(measurements);
		msmtBx.setBounds(190, 505, 100, 30);
		msmtBx.addActionListener(this);
		
		
		lLbl = new JLabel("Length");
		lLbl.setBounds(330, 475, 50, 30);
		lLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		lTxt = new JTextField();
		lTxt.setBounds(320, 505, 60, 30);
		lTxt.setFont(new Font("Dialog", Font.PLAIN, 14));

		wLbl = new JLabel("Width");
		wLbl.setBounds(420, 475, 50, 30);
		wLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		wTxt = new JTextField();
		wTxt.setBounds(410, 505, 60, 30);
		wTxt.setFont(new Font("Dialog", Font.PLAIN, 14));

		pcsLbl =  new JLabel("Pieces");
		pcsLbl.setBounds(510, 475, 50, 30);
		pcsLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		pcsTxt = new JTextField();
		pcsTxt.setBounds(500, 505, 60, 30);
		pcsTxt.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		addPc = new JButton("Add piece");
		addPc.setBounds(680, 500, 80, 30);
		addPc.addActionListener(this);
		
		AddItem = new JButton("Add Item");
		AddItem.setBounds(680, 550, 80, 30);
		AddItem.addActionListener(this);
		add(AddItem);
		
		clrBtn = new JButton("Clear");
		clrBtn.setBounds(680, 600, 70, 30);
		clrBtn.addActionListener(this);
		add(clrBtn);
		
		
		comboModel = new DefaultComboBoxModel();
		comboModel.addAll(units);
		unitBx.setModel(comboModel);
				
		ItemTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	getItems(ItemTxt.getText().strip());
	                }
	            });
	        }
		});
		QuantTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	itemTotal();
	                }
	            });
	        }
		});
		sellTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	itemTotal();
	                }
	            });
	        }
		});
		lTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	            		findSqft();
	                }
	            });
	        }
		});
		wTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	            		findSqft();
	                }
	            });
	        }
		});
		pcsTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	            		findSqft();
	                }
	            });
	        }
		});
		
	}
		
	public void drawTable() {
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);

		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect double click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               int col = target.getSelectedColumn();
	               if(col == 6) { 
	            	   removeItm(row);
	               }
	            }
	         }	
	      });
		
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		TableColumn column = null;
		for (int i = 0; i < 7; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0) {
		    	column.setMaxWidth(30);
		    }else if(i == 1) {
		    	column.setMaxWidth(300);
		    }else if(i == 6) {
		    	column.setMaxWidth(60);
		    } else {
		    	column.setMaxWidth(65);
		    }
		    
		    if(i != 1) {
		    	column.setCellRenderer(centerRenderer);
		    }
		}
	}
	
	public void removeItm(int row) {
		
	   items.remove(row);
	   
	   Total = 0;
		for(int j =0; j < items.size(); j++) {
			Total = Total + Float.valueOf(items.get(j).getTotal());
		}
		receiptTtl.setText("KSH "+String.valueOf(Total));
	   
	   drawTable();
	   
	   SN = 0;
	   for(int j = 0; j < items.size(); j++) {
			SN = SN + 1;
			String name = items.get(j).getName();
			String id = items.get(j).getCode();
			String quant = items.get(j).getQuantity();
			String unit = items.get(j).getUnit();
			String p = items.get(j).getSellp();
			String tt = items.get(j).getTotal();
			
			Object[] obj = {SN, name, quant, unit, p, tt, "_x_"};
			model.addRow(obj);
			model.fireTableDataChanged();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == productBx) {
			populateItem();
		
		} else if(e.getSource() == msmtBx) {
			measure = msmtBx.getSelectedItem().toString();
			
			
		} else if(e.getSource() == unitBx) {
			Unit = unitBx.getSelectedItem().toString();

			if(Name != "") {
				int index = unitBx.getSelectedIndex();
				if(index != 0 && pricingLst.size()!= 0) {
					
					int i = index -1;
					sellp = pricingLst.get(i).getPrice();
					Unit = unitBx.getSelectedItem().toString();
					Code = pricingLst.get(i).getPrID();
					
					sellTxt.setText(String.valueOf(sellp));
										
					itemTotal();
				}
			}
			
			if(Unit.equalsIgnoreCase("Sqft") ) {
				if (!dimensLoaded) { loadDimens(); }
				
			} else {
				if (dimensLoaded) { removeDimens(); }
			}
			
			 
			unitBx.hidePopup();
			
		} else if(e.getSource() == AddItem) {
			
			ItemName = ItemTxt.getText().toString().trim();
			Quantity = QuantTxt.getText().toString().trim();
			Sell = sellTxt.getText().toString().trim();
			
			if(ItemName.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Select item");
			} else if(Quantity.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Add Quantity");
			} else if(Unit.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Choose Unit of Measure");
			} else { 
				if(Unit.equalsIgnoreCase("Sqft") ) {
					if(!lTxt.getText().toString().trim().isEmpty() 
							&& !wTxt.getText().toString().trim().isEmpty()
							&& !pcsTxt.getText().toString().trim().isEmpty() 
							&& measure != "") {
						
						addSqftPc();
					}
				} else {
		        	itemTotal();
		        	addItem();
		        	
				}
				Total = 0;
	    		for(int j =0; j < items.size(); j++) {
	    			Total = Total + Float.valueOf(items.get(j).getTotal());
	    		}
	    		
	        	receiptTtl.setText("KSH "+String.valueOf(Total));
				
	        	clearInput();
	    		productBx.setSelectedIndex(0);

			}
		} else if(e.getSource() == addPc) {
			if(!lTxt.getText().toString().trim().isEmpty() 
					&& !wTxt.getText().toString().trim().isEmpty()
					&& !pcsTxt.getText().toString().trim().isEmpty() 
					&& measure != "") {
				
				addSqftPc();
				
				Total = 0;
	    		for(int j =0; j < items.size(); j++) {
	    			Total = Total + Float.valueOf(items.get(j).getTotal());
	    		}
	        	
	        	receiptTtl.setText("KSH "+String.valueOf(Total));
			}
			
		} else if(e.getSource() == chkOut) {
			if(model.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "No Items to Check Out");
				
			}else {
				new CheckOut(items, Total);

			}
		} else if(e.getSource() == clrBtn) {
			clearInput();
		}	
		
	}
	
	

	private void loadDimens() {
		add(msmtLbl);
		add(msmtBx);
		add(lLbl);
		add(lTxt);
		add(wTxt);
		add(pcsLbl);
		add(pcsTxt);
		add(wLbl);
		add(addPc);
		QuantTxt.setEditable(false);
		
		repaint();
		dimensLoaded = true;
	}
	private void removeDimens() {
		remove(msmtLbl);
		remove(msmtBx);
		remove(lLbl);
		remove(lTxt);
		remove(wTxt);
		remove(pcsLbl);
		remove(pcsTxt);
		remove(wLbl);
		remove(addPc);
		QuantTxt.setEditable(true);
		
		repaint();
		dimensLoaded = false;
	}


	private void itemTotal() {
		Quantity = QuantTxt.getText().toString().trim();
    	Sell = sellTxt.getText().toString().trim();
    	
    	if(Quantity.isEmpty()) {
    		Quantity = "0";
    	}if(Sell.isEmpty()) {
    		Sell = "0";
    	}
    	quant= Float.valueOf(Quantity);
    	sellp = Float.valueOf(Sell);
    	
    	ItemSubTotal = sellp * quant;
		SubTotal.setText(String.valueOf(ItemSubTotal));
	}
	
	private void populateItem() {
		ItemName = productBx.getSelectedItem().toString();
		int index = productBx.getSelectedIndex();
		if(index != 0) {
			int i = index -1;
			Code = products.get(i).getID();
			Desc = products.get(i).getDesc();
			Name = products.get(i).getName();
			
			ItemTxt.setText(Name);
			pDescLbl.setText(Desc);
			getPricing(Code);
		} else if(index == 0) {
			Name = "";
			comboModel = new DefaultComboBoxModel();
			comboModel.addAll(units);
			unitBx.setModel(comboModel);
		}
	}
	
	private void addItem() {
		ItemName = ItemTxt.getText().toString().trim();
		Quantity = QuantTxt.getText().toString().trim();
		Sell = sellTxt.getText().toString().trim();
		
		if(Name == "") {
			Name = ItemName;
			Desc = " ";
			Code = WriteSaleData.writeProduct(Name, Unit, Sell);
			
			productNames.add("");
			productNames.add(Name);
			
		}
			
		SN = SN +1;
		
		SaleItem i = new SaleItem(String.valueOf(Code), Name, Desc, Unit, Quantity, dimens, Sell, String.valueOf(ItemSubTotal));
		items.add(i);
		
		
		Object[] obj = {SN, Name+ " "+ dimens, Quantity, Unit, Sell, ItemSubTotal, "- x -"};
		
		model.addRow(obj);
		model.fireTableDataChanged();
		

	}
	
	
	private void addSqftPc() {
		leng = lTxt.getText().toString().trim();
		wid = wTxt.getText().toString().trim();
		pc = pcsTxt.getText().toString().trim();
		
		dimens = pc + " ( "+leng + " x " + wid +" ) " + measure;
		
		findSqft();
		addItem();
		
		msmtBx.setSelectedIndex(0);
		lTxt.setText("");
		wTxt.setText("");
		pcsTxt.setText("");
		dimens = "";
	}
	
	private void findSqft() {
		leng = lTxt.getText().toString().trim();
		wid = wTxt.getText().toString().trim();
		pc = pcsTxt.getText().toString().trim();
		measure = msmtBx.getSelectedItem().toString();

		if(leng.isEmpty()) {
			leng = "0";
		}
		if(wid.isEmpty()) {
			wid = "0";
		}
		if(pc.isEmpty()) {
			pc = "0";
		}
		
		double  l = Double.valueOf(leng);
		double w = Double.valueOf(wid);
		double sq = 0, p = Double.valueOf(pc);
		
		if(measure == "Inches") {
			l = ConvertMeasure.inchesToFeet(l);
			w = ConvertMeasure.inchesToFeet(w);
			
		} else if ( measure == "Millimeters" ) {
			l = ConvertMeasure.mmToFeet(l);
			w = ConvertMeasure.mmToFeet(w);
		}

		sq = l * w * p;
		
		QuantTxt.setText( String.valueOf(sq) );
		
		itemTotal();
	}

	private void getItems(String enteredText) {
		productNames = new ArrayList<>();
		products = new ArrayList<>();
		
		ProductData.getProducts(enteredText);
		
		productNames = ProductData.getProductNames();
		products = ProductData.getProducts();
		
		if (productNames.size() > 1) {
			productBx.setModel(
					new DefaultComboBoxModel(productNames.toArray()));
			productBx.setSelectedItem(enteredText);
			productBx.showPopup();
		} else {
			productBx.hidePopup();
	    }
		
	}

	private void getPricing(int prodID) {
		unitMeasure = new ArrayList<>();
		pricingLst = new ArrayList<>();
		
		ProductData.getPricing(prodID);
		
		unitMeasure = ProductData.getUnitMeasure();
		pricingLst = ProductData.getPricingLst();
				
		if (unitMeasure.size() > 1) {
			comboModel = new DefaultComboBoxModel();
//			comboModel.removeAllElements();
			comboModel.addAll(unitMeasure);
			unitBx.setModel(comboModel);
			
			unitBx.setSelectedIndex(0);
			unitBx.showPopup();
		} else {
			unitBx.hidePopup();
	    }
	}

	
	void clearInput() {
		Name = "";		Desc = "";		Unit = "";
		Quantity = ""; 	dimens = "";
		sellp = 0.0f;	ItemSubTotal = 0.0f;
		quant = 0;	   	    
		Code = 0;
	    	    
		ItemTxt.setText(Name);
		pDescLbl.setText(Desc);
		QuantTxt.setText(Quantity);
		
		sellTxt.setText("");
		SubTotal.setText("");
		
		msmtBx.setSelectedIndex(0);
		lTxt.setText("");
		wTxt.setText("");
		pcsTxt.setText("");
		
//		productBx.setSelectedIndex(0);

		comboModel = new DefaultComboBoxModel();
		comboModel.addAll(units);
		unitBx.setModel(comboModel);
		
		if (dimensLoaded) { removeDimens(); }
	}
	
	
	void clear() {
		clearInput();
		
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		
		Total = 0;
		
		productNames = new ArrayList<>();
	    products = new ArrayList<>();
	    unitMeasure = new ArrayList<>();
	    pricingLst =  new ArrayList<>();
	    items = new ArrayList<>();
	    
		ItemTxt.setText(Name);
		pDescLbl.setText(Desc);
		QuantTxt.setText(Quantity);
		receiptTtl.setText("KSH 0.0");
		sellTxt.setText("");
		SubTotal.setText("");
		
		comboModel = new DefaultComboBoxModel();
		
		productBx.setModel(comboModel);
		
		comboModel.addAll(units);
		unitBx.setModel(comboModel);
	}
	
	// close the resultSet
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