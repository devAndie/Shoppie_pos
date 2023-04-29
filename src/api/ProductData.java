package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Pricing;
import models.Product;

public class ProductData {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    
	static Product 	p = new Product(0, "", "");

	static Pricing pr = new Pricing(0, "", 0, 0);
	
	
	public static Product getProduct() {
		return p;
	}
	public static Pricing getPricing() {
		return pr;
	}

    static ArrayList<String> productNames;
	static ArrayList<Product> products;
	static ArrayList<String> unitMeasure;
	static ArrayList<Pricing> pricingLst;

	public static ArrayList<String> getProductNames() {
		return productNames;
	}

	public static ArrayList<Product> getProducts() {
		return products;
	}
	

	public static ArrayList<String> getUnitMeasure() {
		return unitMeasure;
	}

	public static ArrayList<Pricing> getPricingLst() {
		return pricingLst;
	}
	
	public static void getProducts(String enteredText) {
		productNames = new ArrayList<>();
		products = new ArrayList<>();
		int code = 0;
		float sellp, stock;
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
//				stock = resultSet.getFloat("pStock");
				
				Product p = new Product(code, iName, desc);
				products.add(p);
			}
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        close();
	        
	    }
	}
	
	
    	
	public static void getPricing(int prodID) {
		unitMeasure = new ArrayList<>();
		pricingLst = new ArrayList<>();
		
		String Unit;
		float Price;
		int PrID;
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT * FROM hardware.pricing WHERE pId ='"+ prodID+"'");
			resultSet = preparedStatement.executeQuery();
			
			unitMeasure.add("");
			
			while (resultSet.next()) {
				Unit = resultSet.getString("unit");
				PrID = resultSet.getInt("prId");
				Price = resultSet.getFloat("price");
				
				unitMeasure.add(Unit);

				Pricing pr = new Pricing(PrID, Unit, Price, prodID);
				pricingLst.add(pr);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	        close();
	    }
	}
	


	
	public static void getProductDetails(String itId) {
		
		try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                    + "user=sqluser&password=sqluserpw");
    
			preparedStatement = connect
			        .prepareStatement("SELECT p.pId, pName, pDescription, pr.prId, unit, price "
							+ "FROM hardware.products p "
							+ "RIGHT JOIN hardware.pricing pr ON pr.pId = p.pId "
							+ "WHERE pr.prId = (SELECT ri.prId FROM hardware.receipt_items ri WHERE ri.itId = '"+itId+"' )"
					);
			resultSet = preparedStatement.executeQuery();
				
			while (resultSet.next()) {
				int id = resultSet.getInt("p.pId");
				String name = resultSet.getString("pName");
				String desc = resultSet.getString("pDescription");
				
				String unit = resultSet.getString("unit");
				int prId = resultSet.getInt("prId");
				float price = resultSet.getFloat("price");
				
				pr = new Pricing(prId, unit, price, id);
				
				p = new Product(id, name, desc);
				
			}
			
	        
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
