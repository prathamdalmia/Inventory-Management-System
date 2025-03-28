package Updation;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.CallableStatement;



public class UpdateProductQuantity {
	public String update(Connection conn,int product_id,int product_quantity) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Product_Quantity(?, ?, ?)}");
			stmt.setInt(1,product_id);
			stmt.setInt(2, product_quantity);
			
			stmt.registerOutParameter(3, Types.INTEGER);
			
			stmt.execute();
			int status = stmt.getInt(3);
			stmt.close();
			if(status == 0) {
				return "Error: Product with ID "+product_id+" Not Found!";
			}
			else if(status == -1) {
				return "Unexpected Error Occured!";
			}
			else {
				return "Updated Quantity of Product with ID "+product_id;
			}
			
			
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "An Error Has Occured";
		}
	}

}
