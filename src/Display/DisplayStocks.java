
package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class Stock {
    private Integer stockId;
    private Integer productId;
    private String productName;
    private Integer supplierId;
    private String supplierName;
    private Date dateAdded;
    private Integer quantityAdded;
    

    public Stock(Integer stockId, Integer productId, String productName, Integer supplierId, String supplierName, Date dateAdded, Integer quantityAdded) {
    	this.stockId=stockId;
    	this.productId=productId;
    	this.productName=productName;
    	this.supplierId=supplierId;
    	this.supplierName=supplierName;
    	this.dateAdded=dateAdded;
    	this.quantityAdded=quantityAdded;
    }

    public Integer getStockId() {
        return stockId;
    }
    
    public Integer getProductId() {
        return productId;
    }
    
    public String getProductName() {
        return productName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public Date getDateAdded() {
        return dateAdded;
    }
    
    public Integer getQuantityAdded() {
    	return quantityAdded;
    }
    
   
}


public class DisplayStocks {
	public List<Stock> displayEmployees(Connection conn){
		List<Stock> stocks = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Get_Stocks(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
            	int stockId = rs.getInt("StockID");
                int productId= rs.getInt("ProductID");
                String productName= rs.getString("ProductName");
                int supplierId= rs.getInt("SupplierID");
                String supplierName= rs.getString("SupplierName");
                Date dateAdded= rs.getDate("DateAdded");
                int quantityAdded= rs.getInt("QuantityAdded");
                

                stocks.add(new Stock(stockId, productId, productName, supplierId, supplierName, dateAdded, quantityAdded));
            }
            
            rs.close();
            stmt.close();
			
		
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return stocks;
		
	}

}
