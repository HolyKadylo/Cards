/** @author Illya Piven
 * This class is used for directors i.e. for people that can create and enwrite cards -- directors of departments,
 * sectors, chief contsructors and so on
 */
package com.kadylo.kmdb;

import java.util.Scanner;
import java.util.Random;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.IOException;

public class Commander implements Commanders, Soldiers {
	private static final int RANGE = 10000;		// Range for creating random id
	private int id;					// tabel number
							// is the same number that
							// int id for soldiers
	private String password;				// former passwordSequence
	private String firstName;				// Iwan Iwanowitsch
	private String lastName;				// Iwanchuk
	private int department;
	//private Set<Card> cardsToExecute;
	private Set<Card> cardsToControll;
	//private HashMap<Card, Boolean> maySign;		// Maps allowments to close the 
							// specific card
	private Set<Integer> directSlaves;			// int IDs of direct slaves
							// no matter they are soldiers
							// or commanders themselves
	private Set<String> maySign;			// String IDs of cards that this 
							// particular master is allowed to close
	private Set<Document> producedDocuments;
	private Set<Document> starredDocuments;		// Documents where this commander
							// was a star (executor)

	//private ArrayList<String> colorOrder;		// Deprecated since there is no need in password sequence	
							// 9 HEX colors with indexes like 
							// buttons in the cell phone
							// used for password
							// generates when changing or setting
							// password

	//private ArrayList<String> passwordSequence;	// Strict combination of colors
							// used for generating password for signature
	//private HashMap<String, Integer> 			// Deprecated since there is no need in password sequence
		//lastHundredPasswords;			// Actual last 100 passwords that 
							// successfully passed through
							// system. Insuccess password rise
							// alert and are not added to this Map.
							// String -- color
							// Integer -- interval in ms

	/*Constructors*/
	Commander(){
		Random rand = new Random();
		id = rand.nextInt(RANGE);			// Used only for tests, so overlapping 
							// will never happen
		password = "HelloPassword";
		firstName = "Big";
		lastName = "Chief";
		department = 100;
		cardsToControll = new TreeSet<Card>();
		directSlaves = new TreeSet<Integer>();
		producedDocuments = new TreeSet<Document>();
		starredDocuments = new TreeSet<Document>();
		maySign = new TreeSet<String>();
		
	}

	Commander(int id, String firstName, String lastName, int department, Set<Integer> directSlaves){
		this(id, firstName, lastName, directSlaves);
		/*if (department == 0)
			throw new NullPointerException(
				"Trying to create commander with null department"
			);*/
		this.setDepartment(department);
	}	

	Commander(int id, String firstName, String lastName, Set<Integer> directSlaves){
		if (firstName == null 
			|| lastName == null 
			|| directSlaves == null)
			throw new NullPointerException(
				"Trying to create commander with null args"
			);
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.directSlaves = directSlaves;
	}	

	/* next two with passwords at once*/
	Commander(int id, String firstName, String lastName, int department, Set<Integer> directSlaves, String password){
		this(id, firstName, lastName, department, directSlaves);
		if (password == null || password.equals(""))
			throw new NullPointerException(
				"Trying to create commander with null password"
			);
		this.setPassword(password);
	}	

	Commander(int id, String firstName, String lastName, Set<Integer> directSlaves, String password){
		this(id, firstName, lastName, directSlaves);
		if (password == null || password.equals(""))
			throw new NullPointerException(
				"Trying to create commander with null password"
			);
		this.setPassword(password);
	}	

	/* The most likely option, retreiving from DB */
	Commander(int id, DataBase db){
		if (db == null)
			throw new NullPointerException(
				"Failed to construct commander from db due to some null arg"
			);
		this.id = id;
		Commander com = new Commander();		// Saving here soldier from DB
								// for a while
		com = db.getCommander(id);
		this.firstName = com.getFirstName();
		this.password = com.getPassword();
		this.lastName = com.getLastName();
		this.department = com.getDepartment();
		this.cardsToControll = com.getCardsToControll();
		this.maySign = com.getMaySign();
		this.directSlaves = com.getDirectSlaves();
		this.producedDocuments = com.getProducedDocuments();
		this.starredDocuments = com.getStarredDocuments();
													//TODO MAKE IT GETTERS HERE AND IN SOLDIERS
	}



	/* Implemented from Commanders interface */
	public Card createCard(){
		return null;
	};

	@Override
	public boolean equals(Object o){
		if (o == null) 
			return false;
		if (!(o instanceof Commander))
			return false;	
		Commander com = (Commander) o;
		if (o.hashCode() != this.hashCode())
			return false;	
		if (com.getId() == id)
			return true;
		return false;
	};

	@Override
	public int hashCode(){
		int result = 499;
		result = 9*result + id;
		return result;
	};

	public Card createCard(Commander chief, Soldier primary, Document document, String content, Date dir){
		if (document == null)
			throw new NullPointerException("Commander is trying to create card with null document");
		
		/*Only authors can create cards*/
		if ( !(document.getProducer().equals(this) || document.getStar().getId() == this.getId()) )
			throw new IllegalArgumentException("Commander is trying to create a card while not being an author");
		
		/*Creating at least*/
		Card card = new Card(chief, primary, document, content, dir);
		return card;	
	};
	
	/*TODO erase here duplication. Tried to make cascade init, but HashMap secondaryControllers here is not iterable*/
	public Card createCard(Commander chief, Soldier primary, Document document, String content, Date dir, HashMap <Commander, HashMap<Signature, String>> secondaryControllers){
		if (document == null)
			throw new NullPointerException("Commander is trying to create card with null document");
		
		/*Only authors can create cards*/
		if ( !(document.getProducer().equals(this) || document.getStar().getId() == this.getId()) )
			throw new IllegalArgumentException("Commander is trying to create a card while not being an author");
		
		/*Creating at least*/
		Card card = new Card(chief, primary, document, content, dir, secondaryControllers);
		return card;
	}

	/*Only chief controllers and their masters are allowed to close the card*/
	public void sign(Card card, String providedPass){
		if(!maySign.contains(card.getId()))
			throw new IllegalArgumentException(
				"Trying to sign card while not being able to"
			);
		card.sign(this, providedPass);
	};
	public void vise(Card card, String providedPass, String comment){
		if (card == null || providedPass == null)
			throw new NullPointerException(
				"Commander is trying to vise with null args"
			);	
		if (comment.equals("") || comment == null)
			card.vise(this, providedPass);
		else
			card.vise(this, providedPass, comment);
	};
	public void removeController(Card card, Commander controller){
		if (card == null)
			throw new NullPointerException (
				"Commander is trying to remove controller from null card"
			);
		
		/*Only master can dismiss controllers*/
		if (!directSlaves.contains(controller.getId()))
			throw new IllegalArgumentException(
				"Trying to dismiss controller, while not being his master"
			);
		card.removeController(controller);
	};
	public void addController(Card card, Commander controller, String task){
		if (card == null)
			throw new NullPointerException (
				"Commander is trying to add controller to null card"
			);

		/*Only master can add controllers*/
		if (!directSlaves.contains(controller.getId()))
			throw new IllegalArgumentException(
				"Trying to add controller, while not being his master"
			);
		card.addController(controller, task);
	};
	public void reject(Card card){
		card.reject(this);
	};
	public void riseAlert(Commander comm, Card card){
		//TODO after admin panel is ready
	};
	public void riseAlert(Commander comm, String message){
		//TODO after admin panel is ready
	};
	public void changePassword(String oldPassword, String newPassword){
		if (oldPassword == null 
			|| newPassword == null 
			|| newPassword.equals(""))
			throw new NullPointerException("Trying to change password with null args");
		if (!oldPassword.equals(password)){
			riseAlert(this, "Changing password: provided old password doesn't match actual");
			throw new IllegalArgumentException(
				"Trying to change password with wrong old one"
			);
		}
		password = newPassword;
	};
	public void changeTask(Card card, Commander commander, String newTask){
		if (card == null)
			throw new NullPointerException(
				"Trying to change task in null card"
			);
		/*Only master can change tasks*/
		if (!directSlaves.contains(commander.getId()))
			throw new IllegalArgumentException(
				"Trying to change task, while not being a master"
			);
		card.changeTask(commander, newTask);
	};
	public void addMaySign(String id){
		maySign.add(id);
	};

	/*Getters & Setters*/
	public void setFirstName(String s){
		this.firstName = s;
	}

	public void setDepartment(int dep){
		this.department = dep;
	}	

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public String getFirstName(){
		return firstName;
	}

	public int getDepartment(){
		return department;
	}

	public Set<Card> getCardsToControll(){
		return cardsToControll;
	}
	
	public Set<String> getMaySign(){
		return maySign;
	}

	public Set<Document> getProducedDocuments(){
		return producedDocuments;
	}

	public Set<Document> getStarredDocuments(){
		return starredDocuments;
	}

	public String getLastName(){
		return lastName;
	}

	public int getId(){
		return id;
	}

	public Set<Integer> getDirectSlaves(){
		return directSlaves;
	}

	public Commander getMaster(){
		DataBase db = new DataBase();
		Set<Commander> commanders = db.getAllMasters();
		for (Commander comm : commanders){
			if (comm.getDirectSlaves().contains(this.getId())){
				return comm;
			}
		}
		return null;
	}

	/* Implemented from Soldiers interface */
	public boolean equals(Soldier candidate){return false;};
	public void push(Card card, Commander commander){};
	public void addSecondaryExecutor(
		Card card, 
		Soldier Soldier, 
		String pointToControll){};
	
	public void removeSecondaryExecutor(
		Card card, 
		Soldier soldier){};

	public void changeTask(
		Card card, 
		Soldier soldier,
		String newTask){};

	//Test
	public static void main(String[] args){
		System.out.println("=========Testing Commander.class=========\n");
		System.out.println("Testing empty constructors");
		Commander em1 = new Commander();
		Commander em2 = new Commander();
		System.out.println("Two commanders are the same: " + em1.equals(em2) + " " +em2.equals(em1));
		System.out.println("em1.hashCode()=" + em1.hashCode() + "\nem2.hashCode()=" + em2.hashCode());
		System.out.println("em1.getPassword()=" + em1.getPassword() + "\nem2.getPassword()=" + em2.getPassword());
		System.out.println("em1.getLastName()=" + em1.getLastName() + "\nem2.getLastName()=" + em2.getLastName());
		System.out.println("em1.getFirstName()=" + em1.getFirstName() + "\nem2.getFirstName()=" + em2.getFirstName());
		System.out.println("em1.getDepartment()=" + em1.getDepartment() + "\nem2.getDepartment()=" + em2.getDepartment());
		System.out.println("Testing constructors");
		
		// First generation slaves
		Set<Integer> FGslaves = new TreeSet<Integer>();
		FGslaves.add(2);
		FGslaves.add(3);
		FGslaves.add(4);
		
		// second
		Set<Integer> SGslaves = new TreeSet<Integer>();
		SGslaves.add(20);
		SGslaves.add(30);
		SGslaves.add(40);	

		// third
		Set<Integer> TGslaves = new TreeSet<Integer>();
		TGslaves.add(200);
		TGslaves.add(300);
		TGslaves.add(400);
	
		//(int id, String firstName, String lastName, int department, Set<Integer> directSlaves
		Commander con = new Commander(1, "Bogdan", "Khmelnitsky", 0, FGslaves);

		//(int id, String firstName, String lastName, Set<Integer> directSlaves)
		Commander con2 = new Commander(2, "Iwan", "Bogun", SGslaves);

		//(int id, String firstName, String lastName, int department, Set<Integer> directSlaves, String password)
		Commander con3 = new Commander(3, "Maksym", "Kryvonis", 0, SGslaves, "HELLO_PASSWORD");
		
		//(int id, String firstName, String lastName, Set<Integer> directSlaves, String password)
		Commander con4 = new Commander(4, "Taras", "Tryasylo", SGslaves, "ANOTHER_PASS");		
		System.out.println(" ");
		System.out.println(con == con2);
		System.out.println(con2 == con3);
		System.out.println(con3 == con4);
		System.out.println(con4 == con);
		System.out.println(con.equals(con2));
		System.out.println(con2.equals(con3));
		System.out.println(con3.equals(con4));
		System.out.println(con4.equals(con));

		System.out.println("=========Commander.class tested=========\n");
	}
}