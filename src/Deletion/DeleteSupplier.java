package Deletion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class DeleteSupplier {
	public String  delete(Connection conn,int supplier_id) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Delete_Supplier(?)}");
			stmt.setInt(1,supplier_id);
			
			
			stmt.execute();
			stmt.close();
			//System.out.println("Deleted Product with ID "+supplier_id);
			return "Delete;d Product with ID "+supplier_id;

			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return "Unexpected Error Occured";
		}
		
	}

}
