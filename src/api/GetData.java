package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Customer;
import models.Product;
import models.Pricing;

public class GetData {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
	
	public static ArrayList<String> getYears(){
		ArrayList<String> years = new ArrayList<>();
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT DISTINCT YEAR(rDate)  AS year "
			        		+ "FROM hardware.receipts ORDER BY year DESC ");
			resultSet = preparedStatement.executeQuery();
				
			while (resultSet.next()) {
				
				String year = resultSet.getString("year");
				
				years.add(year);
			}
	        
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		        close();
		        
		    }	
		
		return years;
	}
	
	public static ArrayList<String> getMonths(String year){
		ArrayList<String> months = new ArrayList<>();
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT DISTINCT MONTH(rDate) AS month "
			        		+ "FROM hardware.receipts WHERE rDate LIKE '"+ year+"%' ORDER BY month DESC");
			resultSet = preparedStatement.executeQuery();
				
			while (resultSet.next()) {
				
				String month = resultSet.getString("month");
				
				months.add(month);
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }	
			
		return months;
	}
	
	public static ArrayList<String> getDays(String yearMonth){
		ArrayList<String> days = new ArrayList<>();
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT DISTINCT DAY(rDate)  AS day "
			        		+ "FROM hardware.receipts WHERE rDate LIKE '"+ yearMonth+"%' ORDER BY day DESC ");
			resultSet = preparedStatement.executeQuery();
				
			while (resultSet.next()) {
				
				String day = resultSet.getString("day");
				
				days.add(day);
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }	
		
		
		return days;
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
