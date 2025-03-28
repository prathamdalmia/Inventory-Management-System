
package Display;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

class SaleReport {
    private Integer orderId;
    private String customerName;
    private String productName;
    private Integer quantityOrdered;
    private Double totalAmount;
    private Date orderDate;

    public SaleReport(Integer orderId,String customerName, String productName, Integer quantityOrdered ,Double totalAmount, Date orderDate) {
    	this.orderId=orderId;
        this.customerName=customerName;
        this.productName=productName;
        this.quantityOrdered=quantityOrdered;
        this.totalAmount=totalAmount;
        this.orderDate=orderDate;
    }

    public Integer getOrderId() {
        return orderId;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public String getProductName() {
        return productName;
    }
    
    public Integer getQuantityOrderd() {
        return quantityOrdered;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
}


public class GenerateSaleReport {
	public List<SaleReport> generateSaleReport(Connection conn){
		List<SaleReport> reportList = new ArrayList<>();
		
		try {
			CallableStatement stmt = conn.prepareCall("{CALL Sales_Report(?)}");
			
			stmt.registerOutParameter(1,Types.REF_CURSOR);
			
			stmt.execute();
			
			
			ResultSet rs = (ResultSet) stmt.getObject(1);

            // Process the result set
            while (rs.next()) {
            	int orderId = rs.getInt("Order_ID");
                String customerName = rs.getString("Customer_Name");
                String productName = rs.getString("Product_Name");
                int quantityOrdered = rs.getInt("Quantity_Ordered");
                double totalAmount = rs.getDouble("Total_Amount");
                Date orderDate = rs.getDate("Order_Date");

                reportList.add(new SaleReport(orderId, customerName, productName, quantityOrdered, totalAmount, orderDate));
            }
            
            rs.close();
            stmt.close();
			
		
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return reportList;
		
	}

}
