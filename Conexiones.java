package examen;

import java.sql.*;

public class Conexiones {

	public Connection getOracle(String usuario, String clave) {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:XE", usuario,
					clave);
 
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Connection getMariadb(String bd, String usuario, String clave) {

		try {
			Class.forName ("org.mariadb.jdbc.Driver");	
			Connection conexion = DriverManager.getConnection  
			        ("jdbc:mariadb://localhost:3306/"+bd, usuario, clave); 
 
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Connection getMysql(String bd, String usuario, String clave) {

		try {
			Class.forName ("com.mysql.jdbc.Driver");	
			Connection conexion = DriverManager.getConnection  
			        ("jdbc:mysql://localhost:3307/"+bd, usuario, clave); 
 
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Connection getSqlite(String bd) {
		try {
			Class.forName("org.sqlite.JDBC");
			// Establecemos la conexion con la BD
			//recibe el camino completo de la BD
			//.\\Misbd\\sqlite\\ejemplo.db
			Connection conexion = DriverManager.getConnection("jdbc:sqlite:"+ bd);
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Connection getDerby(String bd) {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");	
			//bd ./Misbd/derby/ejemplo
			Connection conexion =  DriverManager.getConnection                        
			                       ("jdbc:derby:."+bd);  
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public Connection getHSQldb(String bd) {
		try {
			Class.forName("org.hsqldb.jdbcDriver" );
			// bd /Misbd/hsqldb/ejemplo
			Connection conexion = DriverManager.getConnection
			                    ("jdbc:hsqldb:file:"+bd);  
			return conexion;
			
		} catch (ClassNotFoundException cn) {
			cn.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
