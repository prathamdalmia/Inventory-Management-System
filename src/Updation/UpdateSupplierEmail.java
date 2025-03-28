package Updation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateSupplierEmail {
	public String update(Connection conn,int supplier_id,String email) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Supplier_Email(?, ?, ?)}");
			stmt.setInt(1,supplier_id);
			stmt.setString(2, email);
			stmt.registerOutParameter(3, Types.INTEGER);
			
			stmt.execute();
			
			int status = stmt.getInt(3);
			
			if(status==0) {
				return "Error: Supplier with ID "+supplier_id+" not found!";
			}
			else if(status == -1) {
				return "Unexpected Error Occured!";
			}
			else {
				return "Updated Email of Supplier with ID "+supplier_id;
			}
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "An Error Has Occured";
		}
		
		
	}

}
