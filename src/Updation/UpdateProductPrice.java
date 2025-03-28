package Updation;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class UpdateProductPrice {
	public String update(Connection conn,int product_id,double product_price) {
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Update_Product_Price(?, ?)}");
			stmt.setInt(1,product_id);
			stmt.setDouble(2, product_price);
			
			stmt.execute();
			
			return "Updated price of Product with ID "+product_id;
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "An Error Has Occured";
		}
		
		
	}

}
