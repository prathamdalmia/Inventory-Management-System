//import Insertion.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import Updation.*;
import Order.PlaceOrder;



public class Main {
	public static void main(String [] args) {
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String username = "sys as sysdba";
		String pass = "Pr270903";
		
		try {
			Connection conn = DriverManager.getConnection(url, username, pass);
			System.out.println("Connected to database");
			
//			InsertSupplier i = new InsertSupplier();
//			//i.insertProduct(conn,102,"P1","c",100,10.5);
//			i.insert(conn, 1, username, pass, "100001", "anywhere");
//			conn.close();
			
//			UpdateProductQuantity u = new UpdateProductQuantity();
//			u.update(conn, 101, 10000);
			
			PlaceOrder po = new PlaceOrder() ;
			po.placeOrder(conn,"Pratham",101,100);
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
