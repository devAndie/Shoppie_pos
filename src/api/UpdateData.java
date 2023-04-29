package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Customer;

public class UpdateData {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
    public static void updateCutomer(Customer c) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");

			preparedStatement = connect.prepareStatement(    
					"UPDATE `hardware`.`customers` SET `cName` = '"+c.getName()+"', `cContact` = '"+c.getContact()+"' "
							+ "WHERE (`cId` = '"+c.getID()+" ');" );
            preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }
    
    public static void updatePrice(String itId, String price, String total) {

		try {
			connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");

			preparedStatement = connect.prepareStatement("UPDATE `hardware`.`receipt_items` "
					+ "SET `itSellP` = '"+ price +"', `itTotal` = '"+ total +"' WHERE (`itId` = '"+ itId +"'); ");
            preparedStatement.executeUpdate();
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
		
    }
    
    public static void updateQuantity(String itId, String quant, String total) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement("UPDATE `hardware`.`receipt_items` "
					+ "SET `itQuantity` = '"+ quant +"', `itTotal` = '"+ total +"' WHERE (`itId` = '"+ itId +"'); ");
            preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }
    
    public static void updateReceiptTotal(String total, String bal, String InvNo) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement(    
					"UPDATE `hardware`.`receipts` SET `rTotal` = '"+ total +"', rBal = '"+ bal +"' WHERE (`rId` = '"+ InvNo +"'); ");
            preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }
    
    public static void updateQuoteTotal(float total, String quoteNo) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement(    
					"UPDATE `hardware`.`quotations` SET `qTotal` = '"+total+"' "
							+ "WHERE (`qId` = '"+quoteNo+" ');" );
            preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }
    
    public static void removeItem(String itID) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement(
					"DELETE FROM `hardware`.`receipt_items` WHERE (`itId` = '"+itID+"'); ");
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    	
    }
    
    public static void deleteProduct(String pId) {
		try {
			connect = DriverManager
			        .getConnection("jdbc:mysql://localhost/hardware?"
			                + "user=sqluser&password=sqluserpw");
			preparedStatement = connect
                    .prepareStatement("DELETE FROM hardware.products "
			+ "WHERE (pId = '"+pId+"' );");
	
			preparedStatement.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
            close();
        }
	}
        
    public static void deleteReceipt(String rId) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement(
					"DELETE FROM `hardware`.`receipts` WHERE (`rId` = '"+rId+"'); ");
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
    }

    public static void deleteQuote(String rId) {
    	try {
    		connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    		
			preparedStatement = connect.prepareStatement(
					"DELETE FROM `hardware`.`quotations` WHERE (`qId` = '"+rId+"'); ");
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            close();
        }
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
