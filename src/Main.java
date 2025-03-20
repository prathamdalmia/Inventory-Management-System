import Insertion.*;
import java.sql.*;



public class Main {
	public static void main(String [] args) {
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		String username = "sys as sysdba";
		String pass = "Pr270903";
		
		try {
			Connection conn = DriverManager.getConnection(url, username, pass);
			System.out.println("Connected to database");
			
			InsertSupplier i = new InsertSupplier();
			//i.insertProduct(conn,102,"P1","c",100,10.5);
			i.insert(conn, 1, username, pass, "100001", "anywhere");
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
