/** @author Illya Piven
 * This class is about card, which contains content for executing one particular task
 */
package com.kadylo.kmdb;

import java.util.Date;
import java.util.HashMap;

public abstract class MasterCard{
	String id;						// GH16, KKK9, PQ17 and so on
	Commander chiefController;				// controller like J'Adanne
	Date created;					// generated in constructor 
	Date closed;					// passed in sign() method
	String task;					// strictly formulated task
	Document document;				// scan + metadata of directive

	Soldier primaryExecutor;				// head of department that 
							// executes the task

	HashMap <Soldier, String> secondaryExecutors;		// those, who were enwritten 
							// to perform the task by head of 
							// department 
							// Soldier -- executor
							// String -- task

	HashMap <Commander, HashMap<Signature, String>> 
		secondaryControllers;			// heads of departments who can
							// assist chiefController.
							// May be added later by chief.
							// Commander -- controller
							// Signature -- visa (created empty)
							// String -- what to controll

	HashMap <Commander, Boolean> pushed; 		// pushed flags. Constructs with all 
							// falses. Encorporates 
							// chiefController + SecControllers

	/* constructors*/
	MasterCard(Commander chief, 
		Soldier primaryExecutor, 
		Document document, 
		String content){};
	MasterCard(Commander chief, 
		Soldier primaryExecutor, 
		Document document, 
		String content, 
		HashMap <Commander, HashMap<Signature, String>> 
			secondaryControllers){};

	/* methods that may be invoked by chiefController 
	 * or author of Document of the Card
	 */
	abstract void addController(Commander controller, 
		String pointToConroll);
	abstract void removeController(Commander controller);
	abstract void changeTask(Commander controller, 
		String newTask);
	
	/*methods that may be invoked by primaryExecutor*/
	abstract void addExecutor(Soldier executor, String task);
	abstract void removeExecutor(Soldier executor);
	abstract void changeTask(Soldier executor, String newTask);

	/* getter methods*/
	abstract Commander getChiefController();
	abstract Date getCreated();
	abstract Date getClosed();
	abstract Document getDocument();
	abstract String getTask();
	abstract Soldier getPrimaryExecutor();
	
	/*status methods & actions*/
	abstract  boolean isController(Commander controller);	// looks through ALL controllers
	abstract  boolean isExecutor(Soldier executor);		// looks through ALL executors
	abstract  void sign (Commander comm, String pass); 	// adds Date closed as well
	abstract  void vise (Commander controller, 
		String pass);				// modifies HashMap secondaryControllers
	abstract  boolean isChief(Commander candidate);
	abstract  boolean isPrimaryExecutor(Soldier candidate);
	abstract  void push(Commander commander);		// changes pushed of the commander.
							// Can be invoked by primary executor

	abstract  void reject(Commander commander);		// changes pushed of the commander.
							// Can be invoked by primaryExecutor or someone
							// from pushed HashMap
}