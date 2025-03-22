package Insertion;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;



public class InsertProduct  {
	
	public void insert(Connection conn,int id,String name,String category,long quantity,double price) throws SQLException {
		
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
		System.out.println("Product added successfully: " + name + " | Category: " + category + " | Qty: " + quantity + " | Price: $" + price);
		
		//closing the statement
		stmt.close();
			
			
			
			
	}
		
		
}
	
	

	
	
