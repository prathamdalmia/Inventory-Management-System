
package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class Employee {
    private Integer employeeId;
    private String employeeName;
    private String role;
    private String contact;
    

    public Employee(Integer employeeId,String employeeName, String role, String contact) {
    	this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.role = role;
        this.contact = contact;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }

    
    public String getRole() {
        return role;
    }
    
    public String getContact() {
        return contact;
    }

    
    
   
}


public class DisplayEmployees {
	public List<Employee> displayEmployees(Connection conn){
		List<Employee> employees = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Get_Employees(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
            	int employeeId = rs.getInt("Employee_ID");
                String employeeName = rs.getString("Employee_Name");
                String role = rs.getString("Role");
                String contact = rs.getString("Contact");
                

                employees.add(new Employee(employeeId, employeeName, role, contact));
            }
            
            rs.close();
            stmt.close();
			
		
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return employees;
		
	}

}
