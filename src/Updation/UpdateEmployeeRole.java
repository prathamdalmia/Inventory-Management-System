package Updation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateEmployeeRole {
	public String update(Connection conn,int employee_id,String role) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Employee_Role(?, ?, ?)}");
			stmt.setInt(1,employee_id);
			stmt.setString(2, role);
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
				return "Updated Role of Employee with ID "+employee_id;
			}
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "An Error Has Occured!";
		}
		
		
	}

}
