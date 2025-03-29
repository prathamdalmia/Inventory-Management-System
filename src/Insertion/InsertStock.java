package Insertion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertStock {
	
	public String  insert(Connection conn,int prod_id, int supplier_id,int quantity_added){
		
		
		Statement stmt1;
		try {
			stmt1 = conn.createStatement();
		
		
		
			int id = 0;
			String query = "SELECT MAX(Stock_ID) AS M_ORDER FROM Stock";
			ResultSet rs = stmt1.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
			id = id+1;
			
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
			stmt1.close();
			return "Stock added successfully with ID " + id;
		
		
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "Unexpected Error Occured!";
		}
			
			
	} 
		
		
}
	


