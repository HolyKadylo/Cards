/** @author Illya Piven
 * This class is used for workers i.e. for people that may perform task specified in cards, ie
 * directors of sectors, departments, simple workers, and sometimes even chief contsructors and so on
 */
package com.kadylo.kmdb;

import java.util.Random;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.NoSuchElementException;

public class Soldier implements Commanders, Soldiers {
	private static final int RANGE = 10000;		// Range for creating random id
	private int id;					// tabel number
							// is the same number that
							// int id for commanders
	
	private String firstName;				// Serhiy Petrowitsch
	private String lastName;				// Swynya
	private int department;
	private TreeSet<Card> cardsToExecute;
	//private Set<Card> cardsToControll;
	private TreeSet<Integer> directSlaves;			// int IDs of direct slaves
							// no matter they are soldiers
							// or commanders themselves
	private TreeSet<Document> producedDocuments;
	private TreeSet<Document> starredDocuments;		// Documents where this commander
							// was a star (executor)

	/*Constructors*/

	/* Default */
	Soldier(){
		Random rand = new Random();
		id = rand.nextInt(RANGE);			// Used only for tests, so overlapping 
							// will never happen
		firstName = "John";
		lastName = "Doe";
		department = 10;
		cardsToExecute = new TreeSet<Card>();
		directSlaves = new TreeSet<Integer>();
		producedDocuments = new TreeSet<Document>();
		starredDocuments = new TreeSet<Document>();
	};

	/* Full */
	Soldier(int id, 
		String firstName, 
		String lastName, 
		int dept, 
		TreeSet<Card> cardsToExecute, 
		TreeSet<Integer> directSlaves, 
		TreeSet<Document> producedDocuments, 
		TreeSet<Document> starredDocuments){

		if(firstName == null 
			|| lastName == null 
			|| cardsToExecute == null 
			|| directSlaves == null 
			|| producedDocuments == null 
			|| starredDocuments == null)
			throw new NullPointerException(
				"Failed to fully construct soldier due to some null arg"
			);
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		department = dept;
		this.cardsToExecute = cardsToExecute;
		this.directSlaves = directSlaves;
		this.producedDocuments = producedDocuments;
		this.starredDocuments = starredDocuments;
	};

	/* The most likely one, retreiving from DB */
	Soldier (int id, DataBase db){
		if (db == null)
			throw new NullPointerException(
				"Failed to construct soldier from db due to some null arg"
			);
		this.id = id;
		Soldier sol = new Soldier();				// Saving here soldier from DB
								// for a while
		sol = db.getSoldier(id);
		this.firstName = sol.getFirstName();
		this.lastName = sol.getLastName();
		this.department = sol.getDepartment();
		this.cardsToExecute = sol.getCardsToExecute();
		this.directSlaves = sol.getDirectSlaves();
		this.producedDocuments = sol.getProducedDocuments();
		this.starredDocuments = sol.getStarredDocuments();
	}

	/* Implemented from Commanders interface*/	
	public boolean equals(Commander com){
		System.out.println(
			"TODO FIND PROPER EXCEPTION HERE"
		);
		return false;
	};

	// TODO REFACTOR THIS. NO MULTIPLE IMPLEMENTATIONS
	public Card createCard(){
		/*No creating cards for soldier*/
		return null;
	};

	public void sign(Card card, String pass){
		/*No signing cards for soldier*/		
	};

	public void vise(Card card, String providedPass, String comment){
		/*No vising cards for soldier*/
	};

	public void removeController(Card card, Commander controller){
		/*No removig controller for soldier*/
	};

	public void changeTask(Card card, Commander controller, String newTask){
		/*No changing task for soldier*/
	};

	public void addController(Card card, Commander controller, String task){
		/*No adding controller for soldier*/
	};
	
	public void reject(Card card){
		/*No rejecting for soldier*/
	};

	public void riseAlert(Commander comm, Card card){
		/*No rising alert with this signature of method for soldier*/
	};

	public void changePasswordSequence(ArrayList<String> newSequence){
		/*No passwords for soldier*/
	};

	@Override
	public boolean equals(Object candidate){
		if (candidate == null)
			return false;
		if (!(candidate instanceof Soldier))
			return false;
		Soldier recruit = (Soldier) candidate;
		if (recruit.hashCode() != this.hashCode())
			return false;
		if (recruit.getId() == id)
			return true;
		return false;
	};

	@Override
	public int hashCode(){
		int result = 42;
		result = 9*result + id;
		return result;
	}

	/* Implemented from Soldiers interface*/
	public void push(Card card, Commander commander){
		if (card == null)
			throw new NullPointerException(
				"Trying to push card with some null args"
			);
		if (!card.isController(commander))
			throw new NoSuchElementException(
				"Trying to push card to non-affected commander"
			);
		if (!card.isExecutor(this))
			throw new IllegalArgumentException(
				"Trying to push card while not being an executor"
			);
		card.push(commander);
	};

	public void addSecondaryExecutor(
		Card card, 
		Soldier soldier, 
		String pointToControll){
		
		if(card == null)
			throw new NullPointerException(
				"Trying to add secondary executor to the null card"
			);
		if(!card.isExecutor(this))
			throw new IllegalArgumentException(
				"Trying to add executor to the card while not being an executor!"
			);
		if (!directSlaves.contains(soldier)){
			throw new IllegalArgumentException(
				"Trying to enslave a free man"
			);
		}
		card.addExecutor(soldier, pointToControll);
	};

	public void removeSecondaryExecutor(Card card, Soldier soldier){
		if(card == null)
			throw new NullPointerException(
				"Trying to remove secondary executor to the null card"
			);
		if(!card.isExecutor(this))
			throw new IllegalArgumentException(
				"Trying to remove executor from the card while not being an executor!"
			);
		if (!directSlaves.contains(soldier)){
			throw new IllegalArgumentException(
				"Trying to free an alien slave"
			);
		}
		card.removeExecutor(soldier);
	};
	
	public void changeTask(Card card, Soldier soldier, String newTask){
		if(card == null)
			throw new NullPointerException(
				"Trying to remove secondary executor to the null card"
			);
		if(!card.isExecutor(this))
			throw new IllegalArgumentException(
				"Trying to change task for subexecutor from the card while not being an executor!"
			);
		if (!directSlaves.contains(soldier)){
			throw new IllegalArgumentException(
				"Trying to command an alien slave"
			);
		}
		card.changeTask(soldier, newTask);
	};

	/*Setters*/
	public void setFirstName(String name){
		this.firstName = name;
	};
	
	public void setLastName(String name){
		this.lastName = name;
	};
	
	public void setDepartment(int department){
		this.department = department;
	};

	/*Getters*/
	public String getFirstName(){
		return firstName;
	}

	public String getLastName(){
		return lastName;
	}

	public int getDepartment(){
		return department;
	}

	public TreeSet<Card> getCardsToExecute(){
		return cardsToExecute;
	}

	public TreeSet<Integer> getDirectSlaves(){
		return directSlaves;
	}

	public TreeSet<Document> getProducedDocuments(){
		return producedDocuments;
	}

	public TreeSet<Document> getStarredDocuments(){
		return starredDocuments;
	}
	
	public int getId(){
		return id;
	}
	
	//Test
	public static void main(String[] args){
		System.out.println("==========Testing Soldier.class=========\n");
		System.out.println("Creating two different soldiers");
		Soldier ssmann = new Soldier (1, DataBase.access());
		Soldier ssmann2 = new Soldier (2, DataBase.access());
		System.out.println("One soldier is equal to another: " + ssmann.equals(ssmann2));
		System.out.println("Soldier\'s 1 hashCode()=" + ssmann.hashCode());
		System.out.println("Soldier\'s 2 hashCode()=" + ssmann2.hashCode());
		System.out.println(ssmann.getFirstName());
		System.out.println(ssmann2.getFirstName());
		System.out.println(ssmann.equals(ssmann2));
		Soldier ssmann3 = new Soldier (1, DataBase.access());
		System.out.println(ssmann.equals(ssmann3));
		System.out.println(ssmann3.equals(ssmann));
		System.out.println("=========Soldier.class tested=========\n");
	}
}