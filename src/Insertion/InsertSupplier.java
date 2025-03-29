package Insertion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertSupplier {
	
	public String insert(Connection conn,String name,String contact,String email,String address){
		
		Statement stmt1;
		try {
			stmt1 = conn.createStatement();
			int id = 0;
			String query = "SELECT MAX(Supplier_ID) AS M_ORDER FROM Suppliers";
			ResultSet rs = stmt1.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
			id = id+1;
			
			
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
			//System.out.println("Supplier added successfully: " + name + " | Contact: " + contact + " | Email: " + email + " | address: " + address);
			
			//closing the statement
			stmt.close();
			stmt1.close();
			return "Supplier added successfully: " + name + " | Contact: " + contact + " | Email: " + email + " | address: " + address;
			
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return "Unexpected Error Occured!";
		}
			
			
	} 
		
		
}


