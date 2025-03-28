package Updation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;



public class UpdateProductQuantity {
	public String update(Connection conn,int product_id,int product_quantity) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Product_Quantity(?, ?)}");
			stmt.setInt(1,product_id);
			stmt.setInt(2, product_quantity);
			
			stmt.execute();
			
			
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Updated Product with ID "+product_id;
	}

}
