package Insertion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertEmployee {
	public String insert(Connection conn,String name, String role, String contact){
		
		try {	
			Statement stmt1 = conn.createStatement();
			
			
			int id = 0;
			String query = "SELECT MAX(Employee_ID) AS M_ORDER FROM Employees";
			ResultSet rs = stmt1.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt(1);
			}
			id = id+1;	
			
			//creating a statement to call the procedure to add new product in products table 
			CallableStatement stmt2 = conn.prepareCall("{ CALL Add_Employee(?, ?, ?, ?) }");
				
			//setting the parameters
			stmt2.setInt(1, id);
			stmt2.setString(2,name);
			stmt2.setString(3, role);
			stmt2.setString(4, contact);
			
			
			//execute the statement
			stmt2.execute();
			//System.out.println("Supplier added successfully: " + name + " | Contact: " + contact + " | Email: " + email + " | address: " + address);
			
			//closing the statement
			stmt2.close();
			stmt1.close();
			return "Employee added successfully: " + name + " | Contact: " + contact + " | Role: " + role ;
		
		
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error has Occured";
		}
		
		
		
} 

}
