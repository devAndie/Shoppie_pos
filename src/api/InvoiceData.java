package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InvoiceData {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
    public static String getInvDiscount(String rId) {

		String Discount = "";
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT rDiscount FROM hardware.receipts WHERE"
			        		+ "`rId` = '"+ rId +"' ");
			resultSet = preparedStatement.executeQuery();
						
			while (resultSet.next()) {
				Discount = resultSet.getString("rDiscount");
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }
    
		return Discount;	
	}
    
	public static String getTotalBal(String rId) {
		String bal = "";
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT SUM(rBal) AS balance FROM hardware.receipts WHERE"
			        		+ " cId = (SELECT cId FROM hardware.receipts WHERE rId = '"+ rId +"') ");
			resultSet = preparedStatement.executeQuery();
						
			while (resultSet.next()) {
				bal = resultSet.getString("balance");
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }		
		
		return bal;
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
