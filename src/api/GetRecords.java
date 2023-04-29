package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.InvoiceModel;
import models.SaleItem;

public class GetRecords {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
    
    
    public static ArrayList<InvoiceModel> getInvoices(String query) {
    	ArrayList<InvoiceModel> invoices = new ArrayList<>();
		
    	try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
	    	resultSet = statement
	                .executeQuery(query);
	    	
	    	String cName = "", payment = "", status = "", 
	    			cContact = "", teller = null, date = null,
	    					total = "", bal = "", invNo = "";
	    	int SN = 0, itemsNo = 0 , counter = 0;
	    
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
	    		
	    		InvoiceModel inv = new InvoiceModel(invNo, cName, cContact, total, bal, teller, date);
				invoices.add(inv );
	    	}
    	} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            close();
        }
	    
    	return invoices;
    	
    }
    
    
    public static ArrayList<SaleItem> fetchInvItems(String query){
    	ArrayList<SaleItem> items = new ArrayList<>();
    	
    	try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			resultSet = statement.executeQuery(query);
			
			String pName = "", unit = "", Desc = "", dimens = "", code, total, price, quantity;
	    	
			while (resultSet.next()) {
				
				code = resultSet.getString("itId");
				pName = resultSet.getString("pName");
				dimens = resultSet.getString("itDimens");
				Desc = resultSet.getString("pDescription");
				quantity = resultSet.getString("itQuantity");
				unit = resultSet.getString("unit");
				price = resultSet.getString("itSellP");
				total = resultSet.getString("itTotal");
				
				SaleItem item = new SaleItem(
						code, pName, Desc, unit, quantity, dimens, price, total);
				
				
				items.add(item);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		return items;
		
    }
    
    public static ArrayList<SaleItem> fetchQuoteItems(String qId){
    	ArrayList<SaleItem> items = new ArrayList<>();
    	String query = "SELECT itId, p.pId, p.pName, p.pDescription, i.itQuantity, unit, "
        		+ "itSellP, i.itTotal, i.itDimens "
        		+ "FROM hardware.receipt_items i "
        		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
        		+ "JOIN hardware.products p ON sh.pId = p.pId "
        		+ "WHERE i.qId = '"+ qId +"' ";

//    	String query = "SELECT itId, itQuantity, pName, pDescription, "
//				+ "itDimens, unit, itSellP,  itTotal, p.pId "
//		+ "FROM hardware.receipt_items i "
//		+ "JOIN hardware.pricing sh ON i.prId = sh.prId "
//		+ "JOIN hardware.products p ON sh.pId = p.pId "
//		+ "WHERE i.qId = "+qId ;
		
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			
			statement = connect.createStatement();
			resultSet = statement
	                .executeQuery(query);
			
			String pName = "", unit = "", Desc = "", dimens = "", code, total, price, quantity;
	    	
			while (resultSet.next()) {
				
				code = resultSet.getString("itId");
				pName = resultSet.getString("pName");
				dimens = resultSet.getString("itDimens");
				Desc = resultSet.getString("pDescription");
				quantity = resultSet.getString("itQuantity");
				unit = resultSet.getString("unit");
				price = resultSet.getString("itSellP");
				total = resultSet.getString("itTotal");
				
				SaleItem item = new SaleItem(
						code, pName, Desc, unit, quantity, dimens, price, total);
				
				
				items.add(item);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
			
		
    	
    	return items;
    }
    
    
    private static void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
            
            if (preparedStatement != null) {
            	preparedStatement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }
}
