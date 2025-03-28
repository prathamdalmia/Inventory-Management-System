package Updation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class UpdateProductPrice {
	public String update(Connection conn,int product_id,double product_price) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Product_Price(?, ?, ?)}");
			stmt.setInt(1,product_id);
			stmt.setDouble(2, product_price);
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
				return "Updated price of Product with ID "+product_id;
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "An Error Has Occured";
		}
		
		
	}

}
