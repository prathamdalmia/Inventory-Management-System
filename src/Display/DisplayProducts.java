
package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class Product {
    private Integer productId;
    private String productName;
    private String category;
    private Integer quantity;
    private Double price;
    

    public Product(Integer productId,String productName, String category, Integer quantity ,Double price) {
    	this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }
    
    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }
    
   
}


public class DisplayProducts {
	public List<Product> displayProducts(Connection conn){
		List<Product> products = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Get_Products(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
            	int productId = rs.getInt("Product_ID");
                String productName = rs.getString("Product_Name");
                String category = rs.getString("Category");
                int quantity = rs.getInt("Quantity");
                double price = rs.getDouble("Price");

                products.add(new Product(productId, productName, category, quantity, price));
            }
            
            rs.close();
            stmt.close();
			
		
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return products;
		
	}

}
