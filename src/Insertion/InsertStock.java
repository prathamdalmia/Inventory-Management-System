package Insertion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InsertStock {
	
	public String  insert(Connection conn,int id,int prod_id, int supplier_id,int quantity_added) throws SQLException{
		
		//creating a statement to call the procedure to add new product in products table 
		CallableStatement stmt = conn.prepareCall("{ CALL Add_Stock(?, ?, ?, ?) }");
		
		//setting the parameters
		stmt.setInt(1, id);
		stmt.setInt(2, prod_id);
		stmt.setInt(3, supplier_id);
		stmt.setInt(4, quantity_added);
		
		
		//execute the statement
		stmt.execute();
		//System.out.println("Stock added successfully with ID " + id);
		
		//closing the statement
		stmt.close();
		return "Stock added successfully with ID " + id;
			
			
			
	} 
		
		
}
	


