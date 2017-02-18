/** @author Illya Piven
 * This class is used to connect application to the SQL database
 */
// password: 1_HaTeeE_Th15-fyoUcK1ng-J0b_BicA*USA*[m05t_0v-myu=c0LLee(ii)gUezZ_Rr__-2--SStu5EEd-eVe9_com_par11Ng-2-en00MaaLS
// ^ will be. now mine
//TODO Crypt codec opener

package com.kadylo.kmdb;

import java.sql.*;

import net.ucanaccess.jdbc.UcanaccessDriver;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DataBase{
	private static final String PATH_TO_DB = "D:/J/jj/resources/KMDB.accdb";
	private static final String PROPFILE = "resources/DBProps.properties";
	private static final String password = "PsSwRd";
	private static final String EXPECTED_POOL_SIGNATURE = "tH15_is=15_THthee_eexXxqeCcded==p00L-51gn1tuR";

	/*Constructors*/
	DataBase(){
		
	};
	
	DataBase(String user, String password){
		
	}

	Soldier getSoldier(int id) throws NoSuchElementException{			//TODO
		Soldier sold = new Soldier ();
		try (Connection connection = Pool.getConnection())
		{ 
			// now this was added to try-with-resources
			// Connection connection = Pool.getConnection();
			String sentense = "SELECT firstName, lastName, department, password FROM Employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.getFetchSize() != 1)
				throw new NoSuchElementException("Specified ID was not found or occured more than once");
			sold.setFirstName(rs.getString("firstName"));
			sold.setLastName(rs.getString("lastName"));
			sold.setDepartment(rs.getInt("department"));
		} catch (SQLException e){
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}

		/*throw new NoSuchElementException(
		"Soldier with id " + id + " doesn't exist"
		);*/
		return sold;
	}

	Commander getCommander(int id) throws NoSuchElementException{		//TODO
		Commander com = new Commander ();
		com.setFirstName("Henrich");
		return com;
	}
		
	/*password may be transmitted only to valid Pool object
	 * Validity means that expected and actual class signature 
	 * of the Pool object are the same . 
	 */
	String getPassword(String classSignature){
		if (classSignature.equals(EXPECTED_POOL_SIGNATURE))
			return password;
		else
			return null;
	}
	
	String getPropfile(){
		return PROPFILE;
	}
		
	String getPathToDB(){
		return PATH_TO_DB;
	}
	
	Set<Commander> getAllMasters(){
		return null;
	}

	/*Test*/
	public static void main (String[] args){
	
		/*Based on example Withoud JNDI
		 * commons-dbcp2-2.1.1 apidocs
		 * Package org.apache.commons.dbcp2.datasources
		 * ?
 		 */
	}

	/*//////////////////GARBAGE////////////////////
	private static Connection getConnection() throws SQLException{
		Connection conn = null;
		
		conn = DriverManager.getConnection("jdbc:ucanaccess://" + PATH_TO_DB + ";" + pr);
		return conn;
	}
	*/
	/* Actually signature of this method could look like this:
	 * private Connection getConnection(String user, String password)
	 * but since it requires storing password to the db in the open file,
	 * I've changed it, so now password is stored in *.java
	 * and user -- in *.properties
	 * Assuming that invokation of this method occures only on correct
	 * property file

	private static Connection getConnection(String password) throws SQLException{
		Connection conn = null;
		Properties pr = new Properties();

		// Difference from another method is here
		pr.put("password", password);
		try{
			FileInputStream in = new FileInputStream(PROPFILE);
			pr.load(in);
			in.close();
		} catch (IOException ioe){
			System.out.println("DB issue: " + ioe.toString());
		}
		conn = DriverManager.getConnection("jdbc:ucanaccess://" + PATH_TO_DB + ";" + pr);
		return conn;
	} 
	*/
}

class Pool { 
	private static SharedPoolDataSource ds;
	private static final String classSignature = "tH15_is=15_THthee_eexXxqeCcded==p00L-51gn1tuR";
	static { 

		DataBase dtbs = new DataBase();
		String PROPFILE = dtbs.getPropfile();
		String PASSWORD = dtbs.getPassword(classSignature);
		String PATH_TO_DB = dtbs.getPathToDB();
		
		DriverAdapterCPDS cpds = new DriverAdapterCPDS(); 
		try{
			cpds.setDriver("net.ucanaccess.jdbc.UcanaccessDriver"); 
		} catch (ClassNotFoundException ee){
			System.out.println("DB issue: " + ee.toString());
		}
		try {
			Properties pr = new Properties();
			FileInputStream in = new FileInputStream(PROPFILE);
			pr.load(in);
			in.close();					//TODO PASSWORD
			cpds.setConnectionProperties(pr);
		} catch (IOException ioe){
			System.out.println("DB issue: " + ioe.toString());
		}
		cpds.setUrl("jdbc:ucanaccess://" + PATH_TO_DB); 
		cpds.setUser("foo"); 
		cpds.setPassword("pwd"); 
		SharedPoolDataSource tds = new SharedPoolDataSource(); 
		tds.setConnectionPoolDataSource(cpds); 
		tds.setMaxTotal(10); 
		tds.setMaxConnLifetimeMillis(5000); 
		ds = tds; 
	} 
	public static Connection getConnection() throws SQLException{
	 	return ds.getConnection(); 
	} 
}