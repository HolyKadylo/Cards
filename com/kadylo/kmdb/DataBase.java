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
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DataBase{
	
	// non UTF-8
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	//private static final String PATH_TO_DB = PATH.substring(0, PATH.indexOf("com/kadylo/kmdb")) + "/resources/KMDB.accdb";
	private static String PATH_TO_DB = "resources/KMDB.accdb";
	private static final String PROPFILE = "resources/DBProps.properties";
	private static final String password = "PsSwRd";
	private static final String EXPECTED_POOL_SIGNATURE = "tH15_is=15_THthee_eexXxqeCcded==p00L-51gn1tuR";

	// "Thinking in Java" by Bruce Eckel p.160 
	private static DataBase database = new DataBase();
	public static DataBase access(){
		return database;
	}
	
	/*Constructors*/
	private DataBase(){
		try{
			PATH_TO_DB = java.net.URLDecoder.decode(PATH, "UTF-8") + PATH_TO_DB;
			//System.out.println("PDB " + PATH_TO_DB);
		} catch (UnsupportedEncodingException e){
			System.out.println("DB issue: " + e.toString());
		}
	};
	
	private DataBase(String user, String password){
		
	}

	/*Getters*/
	Card getCard(String id) throws NoSuchElementException{
			
	//private String id;+						
	//private Commander chiefController;+
	//private Date created;+			
	//private Date directive;+				
	//private Date closed;+			
	//private Signature closedSign;--
	//private String task;+					
	//private Document document;+
	//private Soldier primaryExecutor;+	
	//private HashMap <Soldier, String> secondaryExecutors;		
	//private HashMap <Commander, HashMap<Signature, String>> secondaryControllers;	
	//private HashMap <Commander, Boolean> pushed; 
		Card card = null;
		try (Connection connection = Pool.getConnection()){
			String sentense = "SELECT chiefController, created, directive, closed, task, primaryExecutor, document FROM Cards WHERE archived = FALSE AND  id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				card = new Card(id, 
					DataBase
						.access()
						.getCommander(rs.getInt("chiefController")), 
					rs.getDate("created"), 
					rs.getDate("directive"), 
					rs.getDate("closed"), 
					rs.getString("task"), 
					DataBase
						.access()
						.getSoldier(rs.getInt("primaryExecutor")), 
					DataBase
						.access()
						.getDocument(rs.getInt("document"))
						.get(0));
			};

			sen
		} catch (SQLException e){
			if (e.toString().contains("ResultSet is empty"))
				throw new NoSuchElementException("Failed to get document from DB because provided id was not found");
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}
		return card;
	}

	// returns an ArrayList of documents with different dates of creation
	//TODO add scans or remove them at all?
	//TODO ensure that FIRST document will be THE MOST FRESH
	ArrayList<Document> getDocument(int number) throws NoSuchElementException{
		ArrayList<Document > documents = new ArrayList<Document>();
		try (Connection connection = Pool.getConnection()){
			int department = 0;
			Date date;
			String title = null;
			int producer, star;
			String sentense = "SELECT department, created, title, producer, star FROM Documents WHERE number = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, number);
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
					
				// here we are using the following constructor of the Document
				//Document (int dep, int num, Date date, Commander comm, Soldier sol, String title)
				Document doc = new Document(
					rs.getInt("department"), 
					number, 
					rs.getDate("created"), 
					DataBase.access().getCommander(rs.getInt("producer")), 
					DataBase.access().getSoldier(rs.getInt("star")), 
					rs.getString("title"));
				documents.add(doc);	
			};
		} catch (SQLException e){
			if (e.toString().contains("ResultSet is empty"))
				throw new NoSuchElementException("Failed to get document from DB because provided id was not found");
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}
		return documents;
	}

	Soldier getSoldier(int id) throws NoSuchElementException{			//TODO make separate method for repeating parts of the code?
		Soldier sold = new Soldier ();
		try (Connection connection = Pool.getConnection())
		{ 

			// now this was added to try-with-resources
			// Connection connection = Pool.getConnection();
			String firstName = new String();
			String lastName = new String();
			int department = 0;
			String sentense = "SELECT firstName, lastName, department FROM Employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				firstName = rs.getString("firstName");
				lastName= rs.getString("lastName");	
				department = rs.getInt("department");				
			};
			sold.setFirstName(firstName);
			sold.setLastName(lastName);
			sold.setDepartment(department);
			
			// making cards to execute
			TreeSet<String> cardsToExecute =  new TreeSet<String>();
			sentense = "SELECT id FROM Cards WHERE (primaryExecutor = ? OR secondaryExecutors IN (SELECT bunchOfExecutors FROM secondaryExecutors WHERE ids = ?)) AND archived = FALSE";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			statement.setInt(2, id);
			rs = statement.executeQuery();
			while (rs.next()){
				cardsToExecute.add(rs.getString("id"));				
			}
			sold.setCardsToExecute(cardsToExecute);

			// making directSlaves
			TreeSet<Integer> directSlaves =  new TreeSet<Integer>();
			sentense = "SELECT slaves FROM Departments WHERE master = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				directSlaves.add(rs.getInt("slaves"));		
			}
			sold.setDirectSlaves(directSlaves);

			// making producedDocuments
			// making starredDocuments
			TreeSet<Integer> producedDocuments = new TreeSet<Integer>();
			TreeSet<Integer> starredDocuments = new TreeSet<Integer>();
			sentense = "SELECT number, producer, star FROM Documents WHERE producer = ? OR star = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			statement.setInt(2, id);
			rs = statement.executeQuery();	
			while (rs.next()){
				if (rs.getInt("producer") == id)
					producedDocuments.add(rs.getInt("number"));	
				if (rs.getInt("star") == id)
					starredDocuments.add(rs.getInt("number"));		
			}
			sold.setProducedDocuments(producedDocuments);
			sold.setStarredDocuments(starredDocuments);			
		} catch (SQLException e){
			if (e.toString().contains("ResultSet is empty"))
				throw new NoSuchElementException("Failed to get soldier from DB because provided id was not found");
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}
		return sold;
	}

	Commander getCommander(int id) throws NoSuchElementException{
		Commander com = new Commander ();
		try (Connection connection = Pool.getConnection())
		{ 
			String firstName = new String();
			String sentense = "SELECT firstName FROM Employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				firstName = rs.getString("firstName");			
			};
			com.setFirstName(firstName);

			String lastName = new String();
			sentense = "SELECT lastName FROM Employees WHERE id = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				lastName= rs.getString("lastName");				
			};
			com.setLastName(lastName);

			String password= new String();
			sentense = "SELECT password FROM Employees WHERE id = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				password = rs.getString("password");				
			};
			com.setPassword(password);

			int department = 0;
			sentense = "SELECT department FROM Employees WHERE id = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				department = rs.getInt("department");				
			};
			com.setDepartment(department);
			
			// making cards to controll & may sign
			TreeSet<String> cardsToControll =  new TreeSet<String>();
			TreeSet<String> maySign =  new TreeSet<String>();
			sentense = "SELECT id, chiefController FROM Cards WHERE (chiefController = ? OR secondaryControlers IN (SELECT bunchOfControllers FROM secondaryControllers WHERE ids = ?)) AND archived = FALSE";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			statement.setInt(2, id);
			rs = statement.executeQuery();
			while (rs.next()){
				cardsToControll.add(rs.getString("id"));
				if (rs.getInt("chiefController") == id){
					maySign.add(rs.getString("id"));
				}			
			}
			com.setCardsToControll(cardsToControll);

			// making directSlaves
			TreeSet<Integer> directSlaves =  new TreeSet<Integer>();
			sentense = "SELECT slaves FROM Departments WHERE master = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				directSlaves.add(rs.getInt("slaves"));		
			}
			com.setDirectSlaves(directSlaves);

			// making producedDocuments
			TreeSet<Integer> producedDocuments = new TreeSet<Integer>();
			sentense = "SELECT number FROM Documents WHERE producer = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();	
			while (rs.next()){
				producedDocuments.add(rs.getInt("number"));			
			}
			com.setProducedDocuments(producedDocuments);

			// making starredDocuments
			TreeSet<Integer> starredDocuments = new TreeSet<Integer>();
			sentense = "SELECT number FROM Documents WHERE star = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();	
			while (rs.next()){
				starredDocuments.add(rs.getInt("number"));			
			}
			com.setStarredDocuments(starredDocuments);
		} catch (SQLException e){
			if (e.toString().contains("ResultSet is empty"))
				throw new NoSuchElementException("Failed to get commander from DB because provided id was not found");
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}
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
		System.out.println("=========Testing DataBase.class=========\n");
		System.out.println("p" + PATH);
		System.out.println("pdb " + PATH_TO_DB);
		System.out.println("fir: " + DataBase.access().getCommander(1202).getFirstName() + "\n");
		System.out.println("fir: " + DataBase.access().getCommander(60).getFirstName() + "\n");
		System.out.println("fir: " + DataBase.access().getCommander(3).getFirstName() + "\n");
		System.out.println("fir: " + DataBase.access().getCommander(2).getFirstName() + "\n");
		System.out.println("fir: " + DataBase.access().getCommander(1703).getFirstName() + "\n");
		System.out.println("Getting documents: ");
		DataBase.access().getDocument(10);
		DataBase.access().getDocument(20);
		System.out.println("Getting cards: ");
		DataBase.access().getCard("11");
		/*Based on example Withoud JNDI
		 * commons-dbcp2-2.1.1 apidocs
		 * Package org.apache.commons.dbcp2.datasources
		 * ?
 		 */
		System.out.println("=========DataBase.class tested=========\n");
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
	
	// TODO: too weak 
	private static final String classSignature = "tH15_is=15_THthee_eexXxqeCcded==p00L-51gn1tuR";
	static { 

		String PROPFILE = DataBase.access().getPropfile();
		String PASSWORD = DataBase.access().getPassword(classSignature);
		String PATH_TO_DB = DataBase.access().getPathToDB();
		
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