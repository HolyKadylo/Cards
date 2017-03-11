/** @author Illya Piven
 * This class is used to connect application to the SQL database
 */
// password: 1_HaTeeE_Th15-fyoUcK1ng-J0b_BicA*USA*[m05t_0v-myu=c0LLee(ii)gUezZ_Rr__-2--SStu5EEd-eVe9_com_par11Ng-2-en00MaaLS
// ^ will be
//TODO Crypt codec opener

package com.kadylo.kmdb;

import java.sql.*;

import net.ucanaccess.jdbc.UcanaccessDriver;
import org.apache.commons.dbcp2.datasources.SharedPoolDataSource;
import org.apache.commons.dbcp2.cpdsadapter.DriverAdapterCPDS;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	//temporary TODO fix
	private static final String PROPFILE = "D:\Program Files\Java\apache-tomcat-9.0.0.M17\webapps\cards\WEB-INF\classes\resources";
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

	/* Setters */
		
	// this method adds specified card and overrides it if needed
	public void addCard(Card card) throws SQLException{
		
		// here we are forced to DELETE before INSERTing because 
		// if we would insert fresh data with another incrementing key
		// and operate with it, we won't be able to behave this way when
		// creating a list of cards for a particular soldier 
		String deleteSentense1 = "DELETE FROM secondaryExecutors WHERE bunchOfExecutors = (SELECT secondaryExecutors FROM Cards WHERE id = ?)";
		String deleteSentense2 = "DELETE FROM secondaryControllers WHERE bunchOfControllers = (SELECT secondaryControlers FROM Cards WHERE id = ?)";
		String deleteSentense3 = "DELETE FROM Cards WHERE id = ?";
		
		//(id, chiefController, created, directive, closed, task, primaryExecutor, document, secondaryControlers, secondaryExecutors, archived, isPushedToChief)
		String sentense = "INSERT INTO Cards (id, chiefController, created, directive, closed, task, primaryExecutor, document, secondaryControlers, secondaryExecutors, archived, isPushedToChief, isSigned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT MAX(bunchOfControllers) FROM secondaryControllers) + 1, (SELECT MAX(bunchOfExecutors) FROM secondaryExecutors) + 1, FALSE, ?, ?)";
		
		// we have to use this form instead of try-with-resourses in order to allow rollback
		Connection connection = Pool.getConnection();
		try{
			connection.setAutoCommit(false);			

			// deleting old rows
			PreparedStatement statement = connection.prepareStatement(deleteSentense1);
			statement.setString(1, card.getId());
			//deleteStatement.setString(2, card.getId());	
			statement.executeUpdate();
			statement = connection.prepareStatement(deleteSentense2);
			statement.setString(1, card.getId());	
			//deleteStatement.setString(2, card.getId());	
			statement.executeUpdate();
			statement = connection.prepareStatement(deleteSentense3);
			statement.setString(1, card.getId());
			//deleteStatement.setString(2, card.getId());
			statement.executeUpdate();

			// inserting new rows
			statement = connection.prepareStatement(sentense);
			statement.setString(1, card.getId());
			statement.setInt(2, card.getChiefController().getId());
			statement.setDate(3, new java.sql.Date(card.getCreated().getTime()));
			statement.setDate(4, new java.sql.Date(card.getDirective().getTime()));
			statement.setDate(5, new java.sql.Date(card.getClosed().getTime()));
			statement.setString(6, card.getTask());
			statement.setInt(7, card.getPrimaryExecutor().getId());
			statement.setInt(8, card.getDocument().getNumber());

			//is pushed to chief
			statement.setBoolean(9, card.getPushed(card.getChiefController()));

			//is signed by chief
			statement.setBoolean(10, card.getClosedSign().doesExist());

			if (statement.executeUpdate() != 1)
				throw new SQLException ("Failed to insert values to Cards while addCard(Card card)");
				
			// TODO proper test this section
			if(card.hasSecondaryControllers()){
				sentense = "INSERT INTO secondaryControllers (Code, bunchOfControllers, ids, task, isPushed, isSigned, comment) VALUES ((SELECT MAX(Code) FROM secondaryControllers) + 1, (SELECT MAX(bunchOfControllers) FROM secondaryControllers) + ?, ?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(sentense);

				//TODO make it next()				
				HashMap <Commander, HashMap<Signature, String>> secondaryControllers = card.getSecondaryControllers();
				Set <Commander> coms = secondaryControllers.keySet();
				int i = 1;
				for(Commander commander : coms){

					// here we are preventing DB from incrementing bunchOfControllers
					if (i == 1)
						statement.setInt(1, 1);
					else
						statement.setInt(1, 0);
					i++;
					int comID = commander.getId();
					statement.setInt(2, comID);

					//Signature boolean isOwner(Commander candidate)
					Set<Signature> signatures = secondaryControllers.get(commander).keySet();
					for (Signature signature : signatures){
						if (signature.isOwner(commander)){
							String task = secondaryControllers.get(commander).get(signature);
							statement.setString(3, task);	
						}
					}
					
					// isPushed
					statement.setBoolean(4, card.getPushed(commander));
				
					//isSigned
					statement.setBoolean(5, card.getVised(commander));

					//comment, attached to signature
					statement.setString(6, card.getComment(commander));
					if (statement.executeUpdate() != 1)
						throw new SQLException ("Failed to insert values to Cards while addCard(Card card)");
				}
						
			}
			if(card.hasSecondaryExecutors()){
				sentense = "INSERT INTO secondaryExecutors (Code, bunchOfExecutors, ids, task) VALUES ((SELECT MAX(Code) FROM secondaryExecutors) + 1, (SELECT MAX(bunchOfExecutors) FROM secondaryExecutors) + ?, ?, ?)";
				statement = connection.prepareStatement(sentense);

				// private HashMap <Soldier, String> secondaryExecutors
				HashMap <Soldier, String> secondaryExecutors = card.getSecondaryExecutors();
				Set <Soldier> sols = secondaryExecutors.keySet();
				int i = 1;
				for (Soldier soldier : sols){
					
					// here we are preventing DB from incrementing bunchOfExecutors
					if (i == 1)
						statement.setInt(1, 1);
					else
						statement.setInt(1, 0);
					i++;
					statement.setInt(2, soldier.getId());
					statement.setString(3, secondaryExecutors.get(soldier));
					if (statement.executeUpdate() != 1)
						throw new SQLException ("Failed to insert values to Cards while addCard(Card card)");
				}
			}
			connection.commit();
		} catch (SQLException e){
			connection.rollback();
			e.printStackTrace();
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		} finally{
			connection.close();
		}
	}

	/* Getters */
	Card getCard(String id) throws NoSuchElementException{	
		//private Signature closedSign; // TODO delete?

		//private HashMap <Commander, Boolean> pushed; 

		Card card = null;
		try (Connection connection = Pool.getConnection()){		

			// here we are selecting the most resent card with specified ID
			String sentense = "SELECT chiefController, created, directive, closed, task, primaryExecutor, document, isPushedToChief, isSigned FROM Cards WHERE archived = FALSE AND  id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			rs.next();
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
			if (rs.getBoolean("isPushedToChief")){
				card.push(card.getChiefController());
			} else {

				// this method not only simply REJECTS, but also adds to map "commander-false" record
				card.reject(card.getChiefController());
			}
			if (rs.getBoolean("isSigned")){
				card.sign(card.getChiefController(), card.getChiefController().getPassword());
			}
			sentense = "SELECT ids, task FROM secondaryExecutors WHERE bunchOfExecutors = (SELECT secondaryExecutors FROM Cards WHERE archived = FALSE AND id = ?)";
			statement = connection.prepareStatement(sentense);
			statement.setString(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				card.addExecutor(DataBase.access().getSoldier(rs.getInt("ids")), rs.getString("task"));
			};

			sentense = "SELECT ids, task, isSigned, comment FROM secondaryControllers WHERE bunchOfControllers = (SELECT secondaryControlers FROM Cards WHERE archived = FALSE AND id = ?)";
			statement = connection.prepareStatement(sentense);
			statement.setString(1, id);
			rs = statement.executeQuery();
			while (rs.next()){
				Commander secondaryContr = DataBase.access().getCommander(rs.getInt("ids"));
				card.addController(secondaryContr, rs.getString("task"));
				if (rs.getBoolean("isSigned")){
					try{
						card.vise(secondaryContr, secondaryContr.getPassword(), rs.getString("comment") );
					} catch (Exception e){
						// vise without comment
						card.vise(secondaryContr, secondaryContr.getPassword());
					}
				}
			};

			sentense = "SELECT chiefController AS con, isPushedToChief AS isp FROM Cards WHERE id = ? AND archived = FALSE UNION SELECT ids AS con, isPushed AS isp FROM secondaryControllers WHERE bunchOfControllers = (SELECT secondaryControlers FROM Cards WHERE  id = ? AND archived = FALSE)";
			statement = connection.prepareStatement(sentense);
			statement.setString(1, id);
			statement.setString(2, id);
			rs = statement.executeQuery();
			while (rs.next()){
				if(rs.getBoolean("isp"))
					card.push(DataBase.access().getCommander(rs.getInt("con")));
			};
		} catch (SQLException e){
			if (e.toString().contains("ResultSet is empty"))
				throw new NoSuchElementException("Failed to get document from DB because provided id was not found");
			System.out.println("DB issue: " + e.toString());	
			System.exit(0);
		}
		return card;
	}

	// returns an ArrayList of documents with different dates of creation
	// TODO add scans or remove them at all?
	// TODO ensure that FIRST document will be THE MOST FRESH through years of service
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

			// Doing it for the first time in order to throw an exception if needed
			// TODO test
			rs.next();
			Document doc = new Document(
					rs.getInt("department"), 
					number, 
					rs.getDate("created"), 
					DataBase.access().getCommander(rs.getInt("producer")), 
					DataBase.access().getSoldier(rs.getInt("star")), 
					rs.getString("title"));
			documents.add(doc);	
			while (rs.next()){
					
				// here we are using the following constructor of the Document
				//Document (int dep, int num, Date date, Commander comm, Soldier sol, String title)
				doc = new Document(
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
		try (Connection connection = Pool.getConnection()){
			sold.setId(id);	
		
			// now this was added to try-with-resources
			// Connection connection = Pool.getConnection();
			String firstName = new String();
			String lastName = new String();
			int department = 0;
			String sentense = "SELECT firstName, lastName, department FROM Employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			rs.next();
			firstName = rs.getString("firstName");
			lastName= rs.getString("lastName");	
			department = rs.getInt("department");				
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
			while(rs.next()){
				cardsToExecute.add(rs.getString("id"));				
			};
			sold.setCardsToExecute(cardsToExecute);

			// making directSlaves
			TreeSet<Integer> directSlaves =  new TreeSet<Integer>();
			sentense = "SELECT slaves FROM Departments WHERE master = ?";
			statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			while(rs.next()){
				directSlaves.add(rs.getInt("slaves"));		
			};
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
			com.setId(id);
			String firstName = new String();
			String lastName = new String();
			String password= new String();
			int department = 0;
			String sentense = "SELECT firstName, lastName, password, department FROM Employees WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(sentense);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			rs.next();
			firstName = rs.getString("firstName");
			lastName= rs.getString("lastName");	
			password = rs.getString("password");	
			department = rs.getInt("department");			
			com.setFirstName(firstName);
			com.setLastName(lastName);
			com.setPassword(password);
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
		//DataBase.access().getCard("12");
		DataBase.access().getCard("13");
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