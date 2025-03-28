
package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class Supplier {
    private Integer supplierId;
    private String supplierName;
    private String contactNumber;
    private String email;
    private String address;
    

    public Supplier(Integer supplierId,String supplierName, String contactNumber, String email ,String address) {
    	this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
    
    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
    
   
}


public class DisplaySuppliers {
	public List<Supplier> displaySuppliers(Connection conn){
		List<Supplier> suppliers = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Get_Suppliers(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
            	int supplierId = rs.getInt("Supplier_ID");
                String supplierName = rs.getString("Supplier_Name");
                String contactNumber = rs.getString("Contact_Number");
                String email = rs.getString("Email");
                String address = rs.getString("Address");

                suppliers.add(new Supplier(supplierId, supplierName, contactNumber, email, address));
            }
            
            rs.close();
            stmt.close();
			
		
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return suppliers;
		
	}

}
