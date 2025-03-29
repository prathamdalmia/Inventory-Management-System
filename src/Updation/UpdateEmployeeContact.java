package Updation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateEmployeeContact {
	public String update(Connection conn,int employee_id,String contact) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Employee_Contact(?, ?, ?)}");
			stmt.setInt(1,employee_id);
			stmt.setString(2, contact);
			stmt.registerOutParameter(3, Types.INTEGER);
			
			stmt.execute();
			
			int status = stmt.getInt(3);
			stmt.close();
			if(status==0) {
				return "Error: Employee with ID "+employee_id+" not found!";
			}
			else if(status == -1) {
				return "Unexpected Error Occured!";
			}
			else {
				return "Updated Contact of Employee with ID "+employee_id;
			}
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "An Error Has Occured!";
		}
		
		
	}

}
