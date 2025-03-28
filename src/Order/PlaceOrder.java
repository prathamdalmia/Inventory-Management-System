package Order;

import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceOrder {
	public String placeOrder(Connection conn,String customer_name, int product_id, int product_quantity) {
		
		
		try {
			Statement stmt1 = conn.createStatement();
			String query = "SELECT QUANTITY FROM PRODUCTS WHERE PRODUCT_ID = "+product_id;
			
			ResultSet rs = stmt1.executeQuery(query);
			
			int quantity_available = 0;
			while(rs.next()) {
				quantity_available = rs.getInt("QUANTITY");
			}
			if((quantity_available - product_quantity) < 0) {
				//System.out.println("Not Enough product available");
				return "Not Enough product available";
			}
			
			int order_id = 0;
			query = "SELECT MAX(ORDER_ID) AS M_ORDER FROM ORDERS";
			rs = stmt1.executeQuery(query);
			while(rs.next()) {
				order_id = rs.getInt(1);
			}
			order_id = order_id+1;
			
			
			CallableStatement stmt3 = conn.prepareCall("{CALL Place_Order(?, ?, ?, ?)}");
			
			stmt3.setInt(1, order_id);
			stmt3.setString(2, customer_name);
			stmt3.setInt(3, product_id);
			stmt3.setInt(4, product_quantity);
			
			stmt3.execute();
			stmt1.close();
			stmt3.close();
			return "Placed Order with ID "+ order_id;
			
			//System.out.println("Placed Order with ID "+ order_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error has Occured";
		}
		
		
		
	}

}
