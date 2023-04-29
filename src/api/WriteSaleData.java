package api;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.SaleItem;
import singletons.Statics;

public class WriteSaleData {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
	
   public static int writeProduct(String name, String unit, String sell) {
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
			        		+ "VALUES ('"+ unit +"', '"+ sell+"', '"+ pId +"'); ");
			preparedStatement.executeUpdate();
			
			preparedStatement = connect
			        .prepareStatement("SELECT prId  FROM hardware.pricing "
			        		+ "WHERE unit ='"+unit +"' AND pId ='"+ pId+"' ; ");

			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {      		
				pId = resultSet.getInt("prId");
			}
			        		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	        close();
	    }
		
		return pId;
	}
    
   
   public static void writeReceipt(ArrayList<SaleItem> items,  
		   String cId, String cName, String cPhone, 
		   String recMode, float csPay, String payMode){
	   
	   float total = 0;
	   String teller = "", rType = "";
	   int recID = 0, custID = 0, recIDnew = 0;
	   int tellerId = Statics.getUSERID();
	   
	   long millis = System.currentTimeMillis(); 
	   Date date = new Date(millis);
	   custID = Integer.valueOf(cId);
	   
	   for(int j =0; j < items.size(); j++) {
			total = total + Float.valueOf(items.get(j).getTotal());
		}
		
	   try {
           connect = DriverManager.getConnection("jdbc:mysql://localhost/hardware?"
                           + "user=sqluser&password=sqluserpw");
           statement = connect.createStatement();
        
           if (custID == 0) {
//         Check if Customer exist if so Get Customer ID
	           resultSet = statement
	                   .executeQuery("select cId FROM `hardware`.`customers` "
	                   		+ "WHERE cContact = '"+cPhone+"' ");
	           
	           while (resultSet.next()) {
	           	custID = resultSet.getInt("cId");
	           }
		   }
           
           if (custID == 0) {
//        	   write to Customers
               preparedStatement = connect.prepareStatement("INSERT INTO `hardware`.`customers` "
                       		+ "(`cName`, `cContact`) VALUES (?, ?)");
               
               preparedStatement.setString(1, cName);
               preparedStatement.setString(2, cPhone);
               preparedStatement.executeUpdate();
               
            // Get Customer ID
               resultSet = statement
                       .executeQuery("select cId FROM `hardware`.`customers` WHERE cName = '"
                       		+ cName +"'");
               while (resultSet.next()) {
               	custID = resultSet.getInt("cId");
               }
           }
           
           preparedStatement = connect.prepareStatement( "SELECT uName FROM hardware.users WHERE uID = "+ tellerId);
           resultSet = preparedStatement.executeQuery();
           
           while (resultSet.next()) {
        	   teller = resultSet.getString("uName");
           }
   		
	   		//Get Receipt Id
           float rTotal = 0, rBal = 0;
           resultSet = statement
                   .executeQuery("select rId, rTotal, rBal, rType FROM `hardware`.`receipts` WHERE rDate = '"
                   		+ date+"' AND cId = " + custID +"  ORDER BY rId Desc LIMIT 1");
           
           while (resultSet.next()) {
           	recID = resultSet.getInt("rId");
           	rTotal = resultSet.getFloat("rTotal");
           	rBal = resultSet.getFloat("rBal");
           	rType = resultSet.getString("rType");
           	
           }
           if(recID ==0) {   
           	// write to Receipts
		       	if(recMode == "cash") {
		           	preparedStatement = connect
	                        .prepareStatement("INSERT INTO `hardware`.`receipts` (`rTotal`, `rDate`, `rBal`, `rType`, `uID`, `cId`)"
	                        		+ " VALUES (?, ?, ?, ?, ?, ?)");
		            preparedStatement.setFloat(1, total);
		            preparedStatement.setDate(2, date);
		            preparedStatement.setFloat(3, 0);
		            preparedStatement.setString(4, recMode);
		            preparedStatement.setInt(5, tellerId);
		            preparedStatement.setInt(6, custID);
		            
		            preparedStatement.executeUpdate();
		                
		       	} else if (recMode == "invoice") {
		       		float Bal;
		       		
		           	if(csPay != 0) {
		       			Bal = total - csPay;
		       			
		           	} else {
		           		Bal = total;
		           	}
		           	
		           	preparedStatement = connect
		                        .prepareStatement("INSERT INTO `hardware`.`receipts` (`rTotal`, `rDate`, `rBal`, `rType`, `uID`, `cId`)"
		                        		+ " VALUES (?, ?, ?, ?, ?, ?)");
		            preparedStatement.setFloat(1, total);
		            preparedStatement.setDate(2, date);
		            preparedStatement.setFloat(3, Bal);
		            preparedStatement.setString(4, recMode);
		            preparedStatement.setInt(5, tellerId);
		            preparedStatement.setInt(6, custID);
		            
		            preparedStatement.executeUpdate();
		   		}  
               //Get Receipt Id
               resultSet = statement
                       .executeQuery("select rId FROM `hardware`.`receipts` WHERE rDate = '"
                       		+ date+"' AND cId = " + custID +"  ORDER BY rId Desc LIMIT 1");
               
               while (resultSet.next()) {
               	recIDnew = resultSet.getInt("rId");
               }
               
           } else {
	           	rTotal = rTotal + total;
	           	
	           	float Bal;
           		if(csPay != 0) {
           			Bal = total - csPay;
               	} else {
               		Bal = total;
               	}
           		rBal = rBal + Bal;
           		
           		if (rType == "invoice") {
           			preparedStatement = connect
		           			.prepareStatement("UPDATE `hardware`.`receipts` SET `rTotal` = '"+ rTotal +"', `rBal` = '"+ rBal +"' "
		                       		+ "WHERE (`rId` = '"+ recID +"'); ");
               		preparedStatement.executeUpdate();
           			
           		} else {
           			preparedStatement = connect
		           			.prepareStatement("UPDATE `hardware`.`receipts` "
		           					+ "SET `rTotal` = '"+ rTotal +"', `rBal` = '"+ rBal +"', `rType` = '"+ recMode +"' "
		                       		+ "WHERE (`rId` = '"+recID+"'); ");
               		preparedStatement.executeUpdate();
               		
           		}
	           	
	       }
           
           if(recID ==0) { 
           	recID = recIDnew;
           }
           if(csPay != 0 ) {                
//                Write to payments
               preparedStatement = connect
                         .prepareStatement("INSERT INTO `hardware`.`payments` (`cash`, `payDate`, `payMode`, `rId`, `uId`) "
                         		+ "VALUES ( ?, ?, ?, ?, ?)");
               preparedStatement.setFloat(1, csPay);
               preparedStatement.setDate(2, date);
               preparedStatement.setString(3, payMode);
               preparedStatement.setInt(4, recID);
               preparedStatement.setInt(5, tellerId);
               
               preparedStatement.executeUpdate();
           }
//         Write to Receipt items for each item
           int itemsNo = items.size(); // model.getRowCount();

           String stock, upStock, itmQuant, itTotal, prID, prodID, sp, dimens;
           for(int i = 0; i < itemsNo; i++) {
	           	           	
	           	preparedStatement = connect
	                       .prepareStatement("INSERT INTO `hardware`.`receipt_items` (`itSellP`, `itQuantity`, `itTotal`, `rId`, `prId`, `itDimens`)"
	                       		+ " VALUES (?, ?, ?, ?, ?, ?) ");
	           	
	           	sp = items.get(i).getSellp();
	           	itmQuant = items.get(i).getQuantity();
	           	prID = items.get(i).getCode();
	           	itTotal = items.get(i).getTotal();
	           	dimens = items.get(i).getDimens();
	           	            	
	           	preparedStatement.setString(1, sp);
	           	preparedStatement.setString(2, itmQuant);
	           	preparedStatement.setString(3, itTotal);
	           	preparedStatement.setInt(4, recID);
	           	preparedStatement.setString(5, prID);
	           	preparedStatement.setString(6, dimens);
	           	
	           	preparedStatement.executeUpdate();
           }
           	
//           	GET STOCK
//           	resultSet = statement
//                       .executeQuery("SELECT p.pId, pStock FROM hardware.pricing pr "
//                       		+ "JOIN hardware.products p ON pr.pId = p.pId WHERE prId = "+ prID );
//           	while (resultSet.next()) {
//           		stock = resultSet.getFloat("pStock");
//           		prodID = resultSet.getInt("pId");
//               }
           	//UPDATE STOCK
//           	upStock = stock - itmQuant;
//           	preparedStatement = connect
//                       .prepareStatement("UPDATE `hardware`.`products` SET `pStock` = '"+ upStock
//                       		+"' WHERE (`pId` = '"+ prodID +"')");
//           	preparedStatement.executeUpdate();

           } catch (Exception e) {
        	   e.printStackTrace();
           } finally {
        	   close();
           }
	}


    static void close() {
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
