package Deletion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteProduct {
	public String delete(Connection conn,int product_id) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Delete_Product(?)}");
			stmt.setInt(1,product_id);
			
			
			stmt.execute();
			stmt.close();
			//System.out.println("Deleted Product with ID "+product_id);
			return "Deleted Product with ID "+product_id;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Unexpected Error Occured";
		}
		
	}

}
