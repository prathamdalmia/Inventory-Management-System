package Insertion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InsertSupplier {
	
	public void insert(Connection conn,int id,String name,String contact,String email,String address) throws SQLException{
		
			//creating a statement to call the procedure to add new product in products table 
		CallableStatement stmt = conn.prepareCall("{ CALL Add_Supplier(?, ?, ?, ?, ?) }");
			
		//setting the parameters
		stmt.setInt(1, id);
		stmt.setString(2,name);
		stmt.setString(3, contact);
		stmt.setString(4, email);
		stmt.setString(5, address);
		
		//execute the statement
		stmt.execute();
		System.out.println("Supplier added successfully: " + name + " | Contact: " + contact + " | Email: " + email + " | address: " + address);
		
		//closing the statement
		stmt.close();
			
			
			
			
	} 
		
		
}


