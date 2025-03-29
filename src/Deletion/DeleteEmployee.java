package Deletion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteEmployee {
	public String delete(Connection conn,int employee_id) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Delete_Employee(?)}");
			stmt.setInt(1,employee_id);
			
			
			stmt.execute();
			stmt.close();
			//System.out.println("Deleted Product with ID "+product_id);
			return "Deleted Employee with ID "+employee_id;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Unexpected Error Occured!";
		}
		
	}

}
