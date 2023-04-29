package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Customer;

public class CustomerData {

	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
    static ArrayList<Customer> customers;
	
    public static ArrayList<String> getNames(String text) {
		ArrayList<String> names = new ArrayList<>();
		customers = new ArrayList<>();
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.customers WHERE "
			        		+ "`cName` LIKE '%"+ text +"%' GROUP BY cContact ");
			resultSet = preparedStatement.executeQuery();
			
			names.add("");
			
			while (resultSet.next()) {
				String Name = resultSet.getString("cName");
				
				names.add(Name);
				
				String id = resultSet.getString("cId");
				String contact = resultSet.getString("cContact");
				
				Customer c = new Customer(id, Name, contact);
		
				customers.add(c);
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }
		return names;
	}
    public static ArrayList<Customer> getCustomers(){
    	return customers;
    }
	
	public static ArrayList<String> getContacts(String text) {
		ArrayList<String> contacts = new ArrayList<>();
		customers = new ArrayList<>();
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.customers WHERE "
			        		+ "`cContact` LIKE '%"+ text +"%'");
			resultSet = preparedStatement.executeQuery();
			
			contacts.add("");
			
			while (resultSet.next()) {
				String contact = resultSet.getString("cContact");
				
				contacts.add(contact);
				
				String id = resultSet.getString("cId");
				String name = resultSet.getString("cName");
				
				Customer c = new Customer(id, name, contact);
		
				
				customers.add(c);


			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }
		
		return contacts;
		
	}
	
	public static Customer getCustomer(String cId) {
		Customer c = new Customer("", "", "");
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.customers WHERE"
			        		+ "`cId` = '"+ cId +"' ");
			resultSet = preparedStatement.executeQuery();
						
			while (resultSet.next()) {
				String id = resultSet.getString("cId");
				String name = resultSet.getString("cName");
				String contact = resultSet.getString("cContact");
				
				c = new Customer(id, name, contact);
				
				
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }
		

		return c;
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
