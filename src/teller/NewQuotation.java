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

import api.ConvertMeasure;
import api.CustomerData;
import api.GetData;
import api.ProductData;
import api.WriteSaleData;
import document.GenerateQuotation;
import singletons.Statics;
import models.Pricing;
import models.Product;
import models.Customer;
import models.SaleItem;

public class NewQuotation extends JPanel implements ActionListener  {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String[] columnNames = {
			"QUANTITY", "DESCRIPTION", "UNIT", "PRICE",  "TOTAL", "Remove "
	};
    ArrayList<String> units;    
    private long millis; 
    private Date date;
    private int tellerId;
    
    private JLabel quoteTtlLbl, quoteTtl, pDescLbl, SubTotalLbl, SubTotal,
	QuantLbl, ItemLbl;
    
    private JButton addPc, clrBtn;
	JLabel msmtLbl, lLbl, wLbl, pcsLbl;
	JTextField lTxt, wTxt, pcsTxt;
	JComboBox msmtBx;
	String[] measurements = { "Feet", "Inches", "Millimeters" };
	String leng, wid, pc, measure;
	boolean dimensLoaded = false;
	
    private JTextField rNameTxt, rContactTxt, projTxt, ItemTxt, QuantTxt, sellTxt;

    private JTable table;
    private JComboBox productBx, unitBx, cnBox, cpBox;
	private JButton AddItem, submit;
	private DefaultTableModel model;
	private DefaultComboBoxModel comboModel;
	
	private String dimens = " ";
	private  String  rName, rContact,  pId, proj, ItemName, Quantity, Sell, Name, Desc, Unit, length, width, teller;
	
	float sellp, quant, sqft, l, w, Stock, ItemSubTotal, Total, csPay, csChange;
	private ArrayList<String> productNames;
	private ArrayList<Product> products;
	private ArrayList<String> unitMeasure;
	private ArrayList<Pricing> pricingLst;
	private ArrayList<SaleItem> items;
	ArrayList<String> contacts;
	ArrayList<String> names;
	ArrayList<Customer> customers;

	String custID;
	int QID =0, Code;
	
	public NewQuotation() {
		productNames = new ArrayList<>();
	    products = new ArrayList<>();
	    unitMeasure = new ArrayList<>();
	    pricingLst =  new ArrayList<>();
	    items = new ArrayList<>();

	    units = new ArrayList<>();
	    units.add("");			units.add("Kg");
	    units.add("Metre");		units.add("Feet");	units.add("Sqft");
	    units.add("Piece");		units.add("Pair");  units.add("Set");
	    units.add("Packet");    units.add("Full Length");	units.add("Sheet");
	    units.add("Roll");		    units.add("Bucket");

	    setBounds(0, 0, 800, 750);
		setLayout(null);
		
		JLabel tLbl = new JLabel("NEW QUOTATION");
		tLbl.setBounds(250, 5, 200, 30);
		tLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(tLbl);
		
		JLabel cName = new JLabel("Client Name");
		cName.setBounds(50, 50, 100, 20);
		cName.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(cName);
		
		rNameTxt = new JTextField();
		rNameTxt.setBounds(20, 70, 180, 30);
		rNameTxt.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(rNameTxt);
		
        cnBox = new JComboBox();
        cnBox.setBounds(20, 100, 180, 20);
        cnBox.addActionListener(this);
        add(cnBox);
		
		JLabel cContact = new JLabel("Client Contact");
		cContact.setBounds(270, 50, 100, 20);
		cContact.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(cContact);
		
		rContactTxt = new JTextField();
		rContactTxt.setBounds(250, 70, 180, 30);
		rContactTxt.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(rContactTxt);
		
		cpBox = new JComboBox();
        cpBox.setBounds(250, 100, 180, 20);
        cpBox.addActionListener(this);
        add(cpBox);
        		
		JLabel projLbl = new JLabel("Project Description");
		projLbl.setBounds(500, 50, 150, 20);
		projLbl.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(projLbl);
		
		projTxt= new JTextField();
		projTxt.setBounds(480, 70, 200, 30);
		projTxt.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		add(projTxt);
		
		table = new JTable(){
	         public boolean editCellAt(int row, int column, java.util.EventObject e) {
	             return false;
	         }
	       };
	    drawTable();
	    
	    JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(20, 130, 600, 330);
		add(tablePane);
		
		quoteTtlLbl = new JLabel("Quote Total");
		quoteTtlLbl.setBounds(680, 130, 100, 30);
		quoteTtlLbl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(quoteTtlLbl);
		
		quoteTtl = new JLabel();
		quoteTtl.setBounds(680, 170, 100, 50);
		quoteTtl.setFont(new Font("Dialog", Font.BOLD, 14));
		add(quoteTtl);
		
		submit = new JButton();
		submit.setBounds(680, 350, 100, 30);
		submit.setText("Submit");
		submit.addActionListener(this);
		add(submit);
		
		///////
		int y1 = 480, y2 = 515, y3 = 545, y4 = 550, y5 = 570, y6 = 600;
		
		ItemLbl = new JLabel("Item");
		ItemLbl.setBounds(70, y1, 100, 30);
		ItemLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(ItemLbl);
		
		ItemTxt = new JTextField();
		ItemTxt.setBounds(20, y2, 150, 30);
		ItemTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(ItemTxt);
		
		productBx = new JComboBox();
		productBx.setBounds(20, y3, 150, 20);
		productBx.addActionListener(this);
		add(productBx);
		
		JLabel pDesc = new JLabel("Description");
		pDesc.setBounds(200, y1, 100, 30);
		pDesc.setFont(new Font("Dialog", Font.BOLD, 15));
		add(pDesc);
		
		pDescLbl = new JLabel();
		pDescLbl.setBounds(185, y2, 100, 30);
		pDescLbl.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(pDescLbl);
		
		JLabel unitLbl = new JLabel("Unit");
		unitLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		unitLbl.setBounds(350, y1, 50, 30);
		add(unitLbl);
		
		unitBx = new JComboBox();
		unitBx.setBounds(320, y2, 100, 30);
		unitBx.addActionListener(this);
		add(unitBx);
		
		QuantLbl = new JLabel("Quantity");
		QuantLbl.setBounds(450, y1, 70, 30);
		QuantLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(QuantLbl);
		
		QuantTxt = new JTextField();
		QuantTxt.setBounds(450, y2, 70, 30);
		QuantTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(QuantTxt);
		
		JLabel atLbl = new JLabel("Price");
		atLbl.setBounds(570, y1, 50, 30);
		atLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(atLbl);
		
		sellTxt = new JTextField();
		sellTxt.setBounds(550, y2, 100, 30);
		sellTxt.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(sellTxt);
				
		SubTotalLbl = new JLabel("Sub Total");
		SubTotalLbl.setBounds(680, y1, 100, 30);
		SubTotalLbl.setFont(new Font("Dialog", Font.BOLD, 15));
		add(SubTotalLbl);
		
		SubTotal= new JLabel();
		SubTotal.setBounds(680, y2, 100, 30);
		SubTotal.setFont(new Font("Dialog", Font.PLAIN, 15));
		add(SubTotal);
		
			
		msmtLbl = new JLabel("Measure");
		msmtLbl.setBounds(210, y5, 70, 30);
		msmtLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
				
		msmtBx = new JComboBox(measurements);
		msmtBx.setBounds(190, y6, 100, 30);
		msmtBx.addActionListener(this);
		
		
		lLbl = new JLabel("Length");
		lLbl.setBounds(330, y5, 50, 30);
		lLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		lTxt = new JTextField();
		lTxt.setBounds(320, y6, 60, 30);
		lTxt.setFont(new Font("Dialog", Font.PLAIN, 14));

		wLbl = new JLabel("Width");
		wLbl.setBounds(420, y5, 50, 30);
		wLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		wTxt = new JTextField();
		wTxt.setBounds(410, y6, 60, 30);
		wTxt.setFont(new Font("Dialog", Font.PLAIN, 14));

		pcsLbl =  new JLabel("Pieces");
		pcsLbl.setBounds(510, y5, 50, 30);
		pcsLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		pcsTxt = new JTextField();
		pcsTxt.setBounds(500, y6, 60, 30);
		pcsTxt.setFont(new Font("Dialog", Font.PLAIN, 14));
		
		addPc = new JButton("Add piece");
		addPc.setBounds(680, y4, 80, 30);
		addPc.addActionListener(this);
		
		
		AddItem = new JButton();
		AddItem.setBounds(680, 590, 100, 30);
		AddItem.setText("Add Item");
		AddItem.addActionListener(this);
		add(AddItem);

		
		clrBtn = new JButton("Clear");
		clrBtn.setBounds(680, 630, 70, 30);
		clrBtn.addActionListener(this);
		add(clrBtn);
		
		
		comboModel = new DefaultComboBoxModel();
		comboModel.addAll(units);
		unitBx.setModel(comboModel);


		rNameTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	getNames(rNameTxt.getText());
	                }
	            });
	        }
		});
		rContactTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
		                getContacts(rContactTxt.getText());
	                }
	            });
	        }
		});
		
		ItemTxt.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
	            SwingUtilities.invokeLater(new Runnable() {
	                public void run() {
	                	getItems(ItemTxt.getText());
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
	        	
	    		quoteTtl.setText("KSH "+String.valueOf(Total));
		    	
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
	        	quoteTtl.setText("KSH "+String.valueOf(Total));
			}
		} else if(e.getSource() == cnBox) {
    		int index = cnBox.getSelectedIndex();
    		if(index != 0) {
    			
    			rName = cnBox.getSelectedItem().toString();
    			rContact = customers.get(index-1).getContact();
    			custID = customers.get(index-1).getID();
    			
    			rNameTxt.setText(rName);
    			
    		} else {
    			custID = "0";
    			rContact= "";

    		}
			rContactTxt.setText(rContact);    			

    	} else if(e.getSource() == cpBox) {
    		int i = cnBox.getSelectedIndex();
    		if(i != 0) {
    			rContact= cpBox.getSelectedItem().toString();
    			rName = customers.get(i-1).getName();
    			custID = customers.get(i-1).getID();

    			rContactTxt.setText(rContact);    			
    			
    		} else {
    			custID= "0";
    			Name = "";
    			
    		}
			rNameTxt.setText(Name);
			
		} else if(e.getSource() == submit) {
			rName = rNameTxt.getText().toString().trim();
			rContact = rContactTxt.getText().toString().trim();
			proj = projTxt.getText().toString().trim();
			
			if(model.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "No Items to Check Out");
			} else if(rName.isEmpty() | rContact.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Input All Client Details");
			}  else {
				submit();
			}
		} else if(e.getSource() == clrBtn) {
			clearInput();
		}
		
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
		
	}
	
	private void submit(){
		writeData();
		
		clear();
		TellerDashboard.refreshData();

		if(JOptionPane.showConfirmDialog(null, 
				"\n" + "Generate Quotation Pdf? "+ "\n" 
				+  "\n") == 0) {
			
			String Date = String.valueOf(date);
			String total = String.valueOf(Total);
			String qNo = String.valueOf(QID);

			new GenerateQuotation(items, rName, rContact, teller, 
					Date,  qNo, total, "Quote");			
		}
		
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
			
		
		SaleItem i = new SaleItem(String.valueOf(Code), Name, Desc, Unit, Quantity, dimens, Sell, String.valueOf(ItemSubTotal));
		items.add(i);
		
		Object[] obj = { quant, Name + " "+ dimens,  Unit, Sell, ItemSubTotal, "- x -"};
		
		model.addRow(obj);
		model.fireTableDataChanged();
		
		Name = i.getName();

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

	
	public void drawTable() {
		model = new DefaultTableModel(columnNames, 0);
		table.setModel(model);
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(JLabel.CENTER);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		
		TableColumn column = null;
		for (int i = 0; i < 6; i++) {
		    column = table.getColumnModel().getColumn(i);
		    
		    if(i == 0) {
		    	column.setMaxWidth(70);
		    	column.setCellRenderer(centerRenderer); 

		    }else if(i == 1) {
		    	column.setMaxWidth(400);
		    }else if(i == 2 | i == 5) {
		    	column.setMaxWidth(100);
		    	column.setCellRenderer(centerRenderer); 
		    } else {
		    	column.setMaxWidth(70);
		    }
		}

		table.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent me) {
	            if (me.getClickCount() == 2) {     // to detect double click events
	               JTable target = (JTable)me.getSource();
	               int row = target.getSelectedRow(); // select a row
	               int col = target.getSelectedColumn();
	               if(col == 7) { 
	            	   removeItm(row);
	               }
	            }
	         }	
	      });
		
		
	}
	
	public void removeItm(int row) {
  	   
		items.remove(row);
		Total = 0;
		for(int j =0; j < items.size(); j++) {
			Total = Total + Float.valueOf(items.get(j).getTotal());
		}
		
   		quoteTtl.setText("KSH "+String.valueOf(Total));
   
		drawTable();
			
		for(int j = 0; j < items.size(); j++) {
			String name = items.get(j).getName();
			String id = items.get(j).getCode();
			String quant = items.get(j).getQuantity();
			String unit = items.get(j).getUnit();
			String p = items.get(j).getSellp();
			String tt = items.get(j).getTotal();
				
			Object[] obj = { quant, name, unit, p, tt, "_x_"};
			model.addRow(obj);
			model.fireTableDataChanged();
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
		
	void getNames(String text){
		names = new ArrayList<>();
		names = CustomerData.getNames(text);
		customers = CustomerData.getCustomers();
		
		if (names.size() > 1) {
			cnBox.setModel(
					new DefaultComboBoxModel(names.toArray()));
			
			cnBox.setSelectedItem(text);
			cnBox.showPopup();
		} else {
			cnBox.hidePopup();
	    }
		
		
	}
	
	void getContacts(String text){
		contacts = new ArrayList<>();
		contacts = CustomerData.getContacts(text);
		customers = CustomerData.getCustomers();
		
		if (contacts.size() > 1) {
			cpBox.setModel(
					new DefaultComboBoxModel(contacts.toArray()));
			cpBox.setSelectedItem(text);
			cpBox.showPopup();
		} else {
			cpBox.hidePopup();
	    }
		
	}
	
	
	private void writeData() {
		int cId = Integer.valueOf(custID);

		Total = 0;
		QID = 0;
		
		for(int j =0; j < items.size(); j++) {
			Total = Total + Float.valueOf(items.get(j).getTotal());
		}
	   
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                            + "user=sqluser&password=sqluserpw");
            statement = connect.createStatement();
            
         // Check if Customer exist if so Get Customer ID

            if (cId == 0) {
//          Check if Customer exist if so Get Customer ID
 	           resultSet = statement
 	                   .executeQuery("select cId FROM `hardware`.`customers` "
 	                   		+ "WHERE cContact = '"+ rContact+"' ");
 	           
 	           while (resultSet.next()) {
 	           	cId = resultSet.getInt("cId");
 	           }
 		   }
            
            if (cId == 0) {
            	//write to Customers
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`customers` (`cName`, `cContact`) "
                        		+ "VALUES (?, ?)");
                preparedStatement.setString(1, rName);
                preparedStatement.setString(2, rContact);
                preparedStatement.executeUpdate();
                
             // Get Customer ID
                resultSet = statement
                        .executeQuery("select cId FROM `hardware`.`customers` "
                        		+ "WHERE cContact = '"+rContact+"' ");
                while (resultSet.next()) {
                	custID = resultSet.getString("cId");
                }
            }
            
    		millis = System.currentTimeMillis(); 
    		date = new Date(millis);
    		tellerId = Statics.getUSERID();

    		float qTotal = 0, rBal = 0;
            //Get Quote Id
            resultSet = statement
                    .executeQuery("SELECT qId, qTotal FROM hardware.quotations WHERE qProject = '"
                    		+ proj + "' AND cId = '"+ cId +"' AND qDate = '" +date +"' "
                    		+ " ORDER BY qId DESC LIMIT 1; ");
        
            while (resultSet.next()) {
            	QID = resultSet.getInt("qId");
            	qTotal = resultSet.getFloat("qTotal");
            	
            }
            if (QID == 0) {
           		//Write to Quotations
                preparedStatement = connect
                        .prepareStatement("INSERT INTO `hardware`.`quotations` (`qProject`, `qDate`, `qTotal`, `cId`, `uId`)"
                        		+ " VALUES (?, ?, ?, ?, ?); ");
                preparedStatement.setString(1, proj);
                preparedStatement.setDate(2, date);
                preparedStatement.setFloat(3, Total);
                preparedStatement.setInt(4, cId);
                preparedStatement.setInt(5, tellerId);
                
                preparedStatement.executeUpdate();
                
              //Get Quote Id
                resultSet = statement
                        .executeQuery("SELECT qId FROM hardware.quotations WHERE qProject = '"
                        		+ proj + "' AND cId = '"+ cId +"' AND qDate = '" +date +"' "
                        		+ " ORDER BY qId DESC LIMIT 1; ");
                
                while (resultSet.next()) {
                	QID = resultSet.getInt("qId");
                }
                	
            } else {
            	Total = Total + qTotal;
            	
            	preparedStatement = connect
	           			.prepareStatement("UPDATE `hardware`.`quotations` SET `qTotal` = '"+ Total +"' "
	                       		+ "WHERE (`qId` = '"+ QID +"'); ");
           		preparedStatement.executeUpdate();
       			
            }
            
            
            //Write Quote items
            int itms = items.size();
            for(int i = 0; i < itms ; i++) {
            	preparedStatement = connect
                    .prepareStatement("INSERT INTO `hardware`.`receipt_items` (`itSellP`, `itQuantity`, `itTotal`, `itDimens`, `prId`, `qId`) "
                    		+ "VALUES (?, ?, ?, ?, ?, ?); ");
            	preparedStatement.setString(1, items.get(i).getSellp());            	
            	preparedStatement.setString(2, items.get(i).getQuantity());
            	preparedStatement.setString(3, items.get(i).getTotal());
            	preparedStatement.setString(4, items.get(i).getDimens());
            	preparedStatement.setString(5, items.get(i).getCode());
            	preparedStatement.setInt(6, QID);
            	
            	preparedStatement.executeUpdate();
            	
            }
            
            preparedStatement = connect
                    .prepareStatement( "SELECT uName FROM hardware.users WHERE uID = "+ tellerId);
			
            resultSet = preparedStatement.executeQuery();
			
    		while (resultSet.next()) {
    			teller = resultSet.getString("uName");
			}
            
		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
	        close();
	        
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
		rName  = ""; rContact = "";
		proj  = "";
		
		productNames = new ArrayList<>();
	    products = new ArrayList<>();
	    unitMeasure = new ArrayList<>();
	    pricingLst =  new ArrayList<>();
	    items = new ArrayList<>();
	    
	    rNameTxt.setText(rName);		rContactTxt.setText(rContact);
		projTxt.setText(proj);
		
		ItemTxt.setText(Name);
		pDescLbl.setText(Desc);
		QuantTxt.setText(Quantity);
		quoteTtl.setText("KSH 0.0");
		sellTxt.setText("");
		SubTotal.setText("");
		
		comboModel = new DefaultComboBoxModel();
		
		productBx.setModel(comboModel);
		
		comboModel.addAll(units);
		unitBx.setModel(comboModel);		

		TellerDashboard.refreshData();

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
