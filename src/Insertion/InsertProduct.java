package Insertion;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class InsertProduct  {
	
	public String insert(Connection conn,String name,String category,long quantity,double price) {
		
		
		Statement stmt1;
		try {
			stmt1 = conn.createStatement();
		 
		
		
			int id = 0;
			String query = "SELECT MAX(Product_ID) AS M_ORDER FROM Products";
			ResultSet rs = stmt1.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
			id = id+1;
			
			
			
			
			//creating a statement to call the procedure to add new product in products table 
			CallableStatement stmt = conn.prepareCall("{ CALL Add_Product(?, ?, ?, ?, ?) }");
			
			//setting the parameters
			stmt.setInt(1, id);
			stmt.setString(2,name);
			stmt.setString(3, category);
			stmt.setLong(4, quantity);
			stmt.setDouble(5, price);
			
			//execute the statement
			stmt.execute();
			
			//closing the statement
			stmt.close();
			stmt.close();
			return ("Product added successfully: " + name + " | Category: " + category + " | Qty: " + quantity + " | Price: $" + price);
		
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "Unexpected Error Occured!";
		}
		
			
			
	}
		
		
}
	
	

	
	
