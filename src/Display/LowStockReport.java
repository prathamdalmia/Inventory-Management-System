package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class LowStockProduct {
    private Integer productId;
    private String productName;
    private Integer quantity;

    public LowStockProduct(Integer productId, String productName, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}


public class LowStockReport {
	public List<LowStockProduct> lowStockReport(Connection conn){
		List<LowStockProduct> productList = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Low_Stock_Report(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
                int productId = rs.getInt("Product_ID");
                String productName = rs.getString("Product_Name");
                int quantity = rs.getInt("Quantity");

                productList.add(new LowStockProduct(productId, productName, quantity));
            }
            
            rs.close();
            stmt.close();
			
			
			
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return productList;
		
	}

}
