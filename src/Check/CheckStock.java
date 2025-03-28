package Check;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class CheckStock {
	public int checkStock(Connection conn,int product_id) {
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Check_Stock(?, ?)}");
			stmt.setInt(1, product_id);
			stmt.registerOutParameter(2, Types.INTEGER); //type of output
			
			stmt.execute();
			
			int quantity = stmt.getInt(2);
			stmt.close();
			return quantity;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1; //unexpected error
		}
		

	
		
	}
}
