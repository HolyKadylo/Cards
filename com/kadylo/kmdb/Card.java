/** @author Illya Piven
 * This class is used to represent cards
 */
package com.kadylo.kmdb;

import java.util.Random;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.io.File;
import java.util.NoSuchElementException;
import javax.print.attribute.UnmodifiableSetException;

public class Card extends MasterCard{
	private static final int RANGE = 10000;			// Range for creating random id
	private String id;						// GH16, KKK9, PQ17 and so on
	private Commander chiefController;				// controller like J'Adanne
	private Date created;					// generated in constructor 
	private Date directive;					// when it SHOULD be closed
	private Date closed;					// passed in sign() method
	private Signature closedSign;
	private String task;					// strictly formulated task
	private Document document;				// scan + metadata of directive

	private Soldier primaryExecutor;				// head of department that 
								// executes the task

	private HashMap <Soldier, String> secondaryExecutors;		// those, who were enwritten 
								// to perform the task by head of 
								// department 
								// Soldier -- executor
								// String -- task

	private HashMap <Commander, HashMap<Signature, String>> 
		secondaryControllers;				// heads of departments who can
								// assist chiefController.
								// May be added later by chief.
								// Commander -- controller
								// Signature -- visa (created empty)
								// String -- what to controll

	private HashMap <Commander, Boolean> pushed; 		// pushed flags. Constructs with all 
								// falses. Encorporates 
								// chiefController + SecControllers

	/* constructors*/
	//default
	Card(){

	}

	// for database where card is created from its id
	// TODO Merge
	Card(String id, Commander chief, Date created, Date directive, Date closed, String task, Soldier primaryExecutor, Document document){
		if (chief == null 
			|| created == null 
			|| directive == null 
			|| task == null 
			|| primaryExecutor == null 
			|| document == null) 
				throw new NullPointerException(
					"Failed to construct Card without secondary controllers"
				);
		this.id = id;
		chiefController = chief;
		this.created = created;
		this.directive = directive;
		this.closed = closed;
		this.task = task;
		this.primaryExecutor = primaryExecutor;
		this.document = document;

		closedSign = new Signature (chiefController);
		
		/*Here they are created void*/
		secondaryExecutors = new HashMap  <Soldier, String>();
		secondaryControllers = new HashMap <Commander, HashMap<Signature, String>>();
		pushed = new HashMap <Commander, Boolean>();

		/*Except for this one, we need it 'cause there is nowhere else to change it*/
		pushed.put(chiefController, false);
	}

	Card(Commander chief, 
		Soldier primaryExecutor, 
		Document document, 
		String content, 
		Date directive){
		
		super(chief, primaryExecutor, document, content);
		if (chief == null 
			|| primaryExecutor == null 
			|| document == null 
			|| content == null
			|| directive == null) 
				throw new NullPointerException(
					"Failed to construct Card without secondary controllers"
				);
		chiefController = chief;
		created = new Date();			// Card has actual creation date as well
							// as Signature
		closed = null;				// Creates without Signature so with
							// null closed value
		closedSign = new Signature (chiefController);
		this.primaryExecutor = primaryExecutor;
		this.document = document;
		task = content;
		id = generateID(created); 

		/*When card is created, its allowed to close only to chief and his chiefs*/
		chief.addMaySign(this.getId());
		Commander chiefsMaster;
		try{
			chiefsMaster = chief.getMaster();
		} catch (NullPointerException npe){
			chiefsMaster = null;
		}
		while (chiefsMaster != null){
			chiefsMaster.addMaySign(this.getId());
			chiefsMaster = chiefsMaster.getMaster();
		}

		/*Here they are created void*/
		secondaryExecutors = new HashMap  <Soldier, String>();
		secondaryControllers = new HashMap <Commander, HashMap<Signature, String>>();
		pushed = new HashMap <Commander, Boolean>();
		
		/*Except for this one, we need it 'cause there is nowhere else to change it*/
		pushed.put(chiefController, false);
		this.directive = directive;
	};

	// With secondary controllers
	Card(Commander chief, 
		Soldier primaryExecutor, 
		Document document, 
		String content, 
		Date directive,
		HashMap <Commander, HashMap<Signature, String>> 
			secondaryControllers){

		this(chief, primaryExecutor, document, content, directive);			
		this.secondaryControllers = secondaryControllers;
		
		Set <Commander> commanders = secondaryControllers.keySet();
		for (Commander commander : commanders){
			pushed.put(commander, false);
		}
	};

	@Override
	public boolean equals(Object candidate){
		if (!(candidate instanceof Card))
			return false;
		if (candidate == null)
			return false;
		if (candidate.hashCode() != this.hashCode())
			return false;
		Card c = (Card)candidate;
		if (c.getId().equals(id))
			return true;
		else
			return false;
	}

	@Override
	public int hashCode(){
		int result = 41;
		result = 10 * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	/* methods that may be invoked by chiefController 
	 * or author of Document of the Card
	 */
	void addController(Commander controller, 
		String pointToControll) throws UnmodifiableSetException{		
			if (controller == null 
				|| pointToControll == null)
					throw new NullPointerException(
						"Failed to add controller due to null args"	
					);
			if (secondaryControllers.containsKey(controller))
				throw new UnmodifiableSetException(
					"Trying to add controller that already exists. Use changeTask() instead"
				);
			/* Creating signature placeholder (void signature)*/ 
			Signature signature = new Signature (controller);

			/* Creating attributes placeholder -- the only hashmap*/
			HashMap<Signature, String> hm = new HashMap<Signature, String>();
			hm.put(signature, pointToControll);
			secondaryControllers.put(controller, hm);
			
			/* Adds pushed entry as well*/
			pushed.put(controller, false);
	};

	void removeController(Commander controller) throws NoSuchElementException{
		if (controller == null)
			throw new NullPointerException(
				"Trying to remove controller with null arg"
			);
		if(!secondaryControllers.containsKey(controller)){
			throw new NoSuchElementException(
				"Trying to remove absent controller"
			);
		};
		secondaryControllers.remove(controller);

		/*removes pushed entry as well*/
		pushed.remove(controller);
	};
	
	/* Changes signature to empty placeholder if any
	 * applied
	 */
	void changeTask(Commander controller, 
		String newTask) throws NoSuchElementException{
		if (controller == null || newTask == null)
			throw new NullPointerException(
				"Trying to change task with some null arg"
			);
		if(!secondaryControllers.containsKey(controller)){
			throw new NoSuchElementException(
				"Trying to change task of absent controller"
			);
		};
		Signature sign = new Signature (controller);
		HashMap<Signature, String> hm = new HashMap<Signature, String>();
		hm.put(sign, newTask);
		secondaryControllers.put(controller, hm);
	};
	
	/* methods that may be invoked by primaryExecutor*/
	/* or not only primary?*/

	void addExecutor(Soldier executor, String task) throws UnmodifiableSetException{
		if (executor == null || task == null)
			throw new NullPointerException(
				"Trying to add executor with some null arg"
			);
		if (secondaryExecutors.containsKey(executor)){
			throw new UnmodifiableSetException(
				"Trying to add executor that already exists. Use changeTask() instead"
			);
		}
		secondaryExecutors.put(executor, task);
	};

	void removeExecutor(Soldier executor) throws NoSuchElementException{
		if (executor == null)
			throw new NullPointerException(
				"Trying to remove null executor"
			);
		if (!secondaryExecutors.containsKey(executor))
			throw new NoSuchElementException(
				"Trying to remove non-existent executor"
			);
		secondaryExecutors.remove(executor);
	};

	void changeTask(Soldier executor, String newTask) throws NoSuchElementException{
		if (executor == null
			|| newTask == null)
			throw new NullPointerException(
				"Trying to change executor's task with some null args"
			);
		if (!secondaryExecutors.containsKey(executor))
			throw new NoSuchElementException(
				"Trying to change task of non-existent executor"
			);
		secondaryExecutors.put(executor, newTask);
	};

	/* getter methods*/
	String getId(){
		return id;
	}

	Commander getChiefController(){
		return chiefController;
	};

	Date getCreated(){
		return created;
	};

	Date getClosed(){
		return closed;
	};

	Document getDocument(){
		return document;
	};

	String getTask(){
		return task;
	};

	Soldier getPrimaryExecutor(){
		return primaryExecutor;
	};
	
	/*status methods & actions*/
	String generateID(Date date){
		Random rand = new Random();
		int base = rand.nextInt(RANGE);
		return date.toString() + String.valueOf(base);// TODO
	};
	
	boolean isController(Commander candidate){	// looks through ALL controllers
		//Commander chiefController;	// TODO because of equals
		//HashMap <Commander, 
		//HashMap<Signature, String>> 
		//secondaryControllers;
		if (candidate.equals(chiefController))
			return true;
		if (secondaryControllers.containsKey(candidate))
			return true;
		return false;
	};

	boolean isExecutor(Soldier executor){		// looks through ALL executors
		if (executor.equals(primaryExecutor))	// TODO because of equals
			return true;
		if (secondaryExecutors.containsKey(executor))
			return true;
		return false;			
		
	};
	
	void sign (Commander commander, String pass) 
		throws IllegalArgumentException, 
		UnmodifiableSetException {		// adds Date closed as well
		if (commander == null
			|| pass == null)
			throw new NullPointerException(
				"Trying to sign card with null commander"
			);

		/*Deprecated because of MaySign in Commander*/
		/*if (!chiefController.equals(commander))
			throw new IllegalArgumentException(
				"Trying to close card when not being chief or his master"
			);*/
		if (!chiefController.getPassword().equals(commander.getPassword()))
			throw new IllegalArgumentException(	//TODO another exception??
				"Trying to apply signature while providing incorrect password"
			);
		if (closedSign.doesExist())
			throw new UnmodifiableSetException(
				"Trying to sign card while signature already exists"
			);
		closedSign = new Signature (chiefController);
		closedSign.apply(pass);
		
	};

	/*Vise with comment*/
	void vise (Commander controller, 		// modifies HashMap secondaryControllers
		String pass,
		String comment) throws IllegalArgumentException, 
		IllegalArgumentException, 
		UnmodifiableSetException {
		if (controller == null
			|| pass == null
			|| comment == null)
			throw new NullPointerException(
				"Trying to vise card with null args"
			);
		if (chiefController == controller)		// TODO equals
			throw new IllegalArgumentException(
				"Trying to vise card when being chief"
			);
		if (!secondaryControllers.containsKey(controller))
			throw new IllegalArgumentException(
				"Trying to vise card when not being a controller"
			);
		/*Checking password*/
		if (!controller.getPassword().equals(pass))
			throw new IllegalArgumentException(
				"Trying to vise card with wrong password"
			);
		
		/* Now we have to check if card was vised by controller or not*/
		HashMap<Signature, String> hm = secondaryControllers.get(controller);
		Set<Signature> set = hm.keySet();
		for (Signature signature : set){
			try{
				if (pass.equals(signature.getPassword()))			//Here equals legal
					throw new UnmodifiableSetException(
						"Trying to vise vised card"
					);
			} catch (NullPointerException npe){
				continue;
			}
		}

		/* Now we know that arguments are legal
		 * Devided from previous loop to be more obvious
		*/
		//HashMap <Commander, HashMap<Signature, String>> 
		//secondaryControllers;
		//HashMap<Signature, String> hm
		//Set<Signature> set
		Signature signatureToInsert = null;
		for (Signature signature : set){
			try{
				if (signature.isOwner(controller)){				//TODO EQUALS
					signatureToInsert = signature;			// тут проблема бо ми модифікуємо не секондарі контролерс, а тупо якусь викладку з нього
											// or not?
					signatureToInsert.apply(pass);
				}
			} catch (NullPointerException npe){
				continue;
			}
		}
		hm.put(signatureToInsert, comment);					//Here where the comment goes
		secondaryControllers.put(controller, hm);
	};

	/*Vise without comment*/
	void vise (Commander controller, 		// modifies HashMap secondaryControllers
		String pass){
	
		String comment = "";
		vise(controller, pass, comment);
	};
	
	boolean isChief(Commander candidate){
		if (candidate.equals(chiefController))
			return true;
		else
			return false;
	};

	boolean isPrimaryExecutor(Soldier candidate){
		if (candidate.equals(primaryExecutor))
			return true;
		else
			return false;
	};

	void push(Commander commander) throws 
		IllegalArgumentException{		// changes pushed of the commander.
						// Can be invoked by primary executor
		if (commander == null)
			throw new NullPointerException(
				"Trying to push to the null commander"
			);
		/*removed because of higher check with isController()*/
		/*if (!secondaryControllers.containsKey(commander) 
		 *	&& !chiefController.equals(commander))
		 *	throw new IllegalArgumentException(
		 *		"Trying to push to non affected commander"
		 *	);
		 */
		pushed.put(commander, true);
	};		

	void reject(Commander commander)throws 
		IllegalArgumentException{		// changes pushed of the commander.
						// Can be invoked by primaryExecutor or someone
						// from pushed HashMap
		if (commander == null)
			throw new NullPointerException(
				"Trying to reject to the null commander"
			);
		if (!secondaryControllers.containsKey(commander) 
			&& !chiefController.equals(commander))
			throw new IllegalArgumentException(
				"Trying to reject to non affected commander"
			);
		pushed.put(commander, false);
	};	
	
	boolean getPushed(Commander commander) throws 
		IllegalArgumentException{
		if (commander == null)
			throw new NullPointerException(
				"Trying to get pushed of the null commander"
			);
		if (!secondaryControllers.containsKey(commander) 
			&& !chiefController.equals(commander))
			throw new IllegalArgumentException(
				"Trying to get pushed of non affected commander"
			);
		return pushed.get(commander);
	}

	//Test
	public static void main(String[] args){
		System.out.println("===========Testing Card.class===========\n");
		System.out.println("Creating cards");
		Commander c1 = new Commander();
		Date dd1 = new Date();
		File f1 = new File("Compile.bat");
		Soldier s1 = new Soldier();
		Document d1 = new Document(45, 6000, dd1, c1, s1, "title", f1);
		String ss1 = "hello";
		Card card = new Card(c1, s1, d1, ss1, dd1);
		Card caaaaard = new Card(c1, s1, d1, "HULLO", dd1);
		System.out.println("Card\'s 1 hashCode()=" + card.hashCode());
		System.out.println("Card\'s 2 hashCode()=" + caaaaard.hashCode());
		System.out.println("Two different cards are the same: " + card.equals(caaaaard));
		System.out.println("Card created\n");
		
		System.out.println("Testing manipulationg controller methods:");
		// void addController
		System.out.println("Adding legal controller");
		card.addController(c1, "Control this");
		System.out.println("Controller added");
		System.out.println("Adding controller with null args, assuming error");
		try{
			card.addController(null, null);
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		};
		System.out.println("Adding controller with null arg, assuming error");
		try{
			card.addController(c1, null);
		} catch(NullPointerException npe){
			System.out.println(npe.toString());
		}
		System.out.println("Adding controller that already exists, assuming error");
		try{
			card.addController(c1, "");
		} catch (UnmodifiableSetException e){
			System.out.println(e.toString());
		};
		System.out.println("Adding another legal controller");
		Commander c2 = new Commander();
		card.addController(c2, "Control this too");
		System.out.println("Controllers were added successfully");
		
		//void removeController
		System.out.println("Trying to remove legal controller");
		card.removeController(c1);
		System.out.println("Removed");
		System.out.println("Trying to remove null controller, assuming error");
		try{		
			card.removeController(null);
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		}
		System.out.println("Trying to remove non-existent controller, assuming error");
		Commander c0 = new Commander ();
		try{
			card.removeController(c0);
		} catch (NoSuchElementException e){
			System.out.println(e.toString());
		}

		//void changeTask(Commander controller, String newTask);
		System.out.println("Legally changing tasks");
		card.changeTask(c2, "");
		System.out.println("Tasks were changed");
		System.out.println("Trying to change task with null arg, assuming error");
		try{
			card.changeTask(c0,null);
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		};
		System.out.println("Trying to change task of previously deleted controller, assuming error");
		try{
			card.changeTask(c1, "new Task");
		} catch(NoSuchElementException e){
			System.out.println(e.toString());
		}	
		System.out.println("Trying to change task of non-existent controller, assuming error\n");
		try{
			card.changeTask(c0, "hello task");
		} catch(NoSuchElementException e){
			System.out.println(e.toString());
		}

		System.out.println("Testing manipulationg controller methods:");

		//void addExecutor(Soldier executor, String task){
		System.out.println("Adding legal executor");
		Soldier sol1 = new Soldier();
		Soldier sol2 = new Soldier();
		card.addExecutor(sol1, "task");
		System.out.println("Added legal executor");
		System.out.println("Adding executor with null value, assuming error");
		try{
			card.addExecutor(sol2, null);
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		};
		System.out.println("Adding same executor for the second time, assuming error");
		try{
			card.addExecutor(sol1, "hello");
		} catch (UnmodifiableSetException e){
			System.out.println(e.toString());
		}

		//void removeExecutor(Soldier executor){
		System.out.println("Legally removing the executor");
		card.removeExecutor(sol1);
		System.out.println("Success");
		System.out.println("Removing non-existent executor, assuming error");
		try{
			card.removeExecutor(sol1);
		} catch (NoSuchElementException e){
			System.out.println(e.toString());
		}
		System.out.println("Removing null executor, assuming error");
		try{
			card.removeExecutor(null);
		} catch (NullPointerException e){
			System.out.println(e.toString());
		}
		
		//void changeTask(Soldier executor, String newTask){
		System.out.println("Legally changing task");
		card.addExecutor(sol1, "hello task");
		card.addExecutor(sol2, "hello there task");
		card.changeTask(sol1, "newTask");
		card.changeTask(sol2, "newer one");
		System.out.println("Success");
		System.out.println("Changing task with null arg, assuming error");
		try{
			card.changeTask(sol1, null);
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		}
		System.out.println("Changing task of non-existent executor, assuming error");
		card.removeExecutor(sol2);
		try{
			card.changeTask(sol2, "no task");
		} catch (NoSuchElementException e){
			System.out.println(e.toString());
		}
		
		System.out.println("Testing signing by chief");
		
		//void sign (Commander commander, String pass)
		card.sign(c1, "12333123123");
		System.out.println("Signed succesfully");

		/*Deprecated due to MaySign*/
		/*System.out.println("Trying to sign while not being chief, assuming error");
		try{
			card.sign(c2, "123");
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}*/
		System.out.println("Trying to sign being chef ones more, assuming error");
		try{
			card.sign(c1,"hello again");
		} catch (UnmodifiableSetException e){
			System.out.println(e.toString());
		}
		System.out.println("Trying to sign with null args, assuming error");
		try {
			card.sign(null, "hey");
		} catch (NullPointerException e){
			System.out.println(e.toString());
		}

		System.out.println("\nTesting vises: ");
		System.out.println("Creating new card with new secondary controllers");
		Commander c10 = new Commander ();
		Commander c11 = new Commander ();
		HashMap <Commander, HashMap<Signature, String>> sc = new HashMap <Commander, HashMap<Signature, String>>();
		HashMap <Signature, String> sc2 = new HashMap <Signature, String>();
		sc2.put(new Signature(c10), "Control this");
		sc.put(c10, sc2);
		sc2.clear();
		sc2.put(new Signature(c11), "Control that");
		sc.put(c11, sc2);
		Card card2 = new Card(c1, s1, d1, ss1, dd1, sc);
		System.out.println("Created successfully");
		System.out.println("Vising legally with comment");
		card2.vise(c10,"HelloPassword", "done!");
		System.out.println("Vised successfully");
		System.out.println("Vising legally withot comment");
		card2.vise(c11, "HelloPassword");
		System.out.println("Vised successfully");
		System.out.println("Vising for the second time with comment, assuming error");
		try{
			card2.vise(c10,"HelloPassword", "done");
		} catch (UnmodifiableSetException e){
			System.out.println(e.toString());
		}
		System.out.println("Vising for the second time without comment, assuming error");
		try{
			card2.vise(c11, "HelloPassword");
		} catch (UnmodifiableSetException e){
			System.out.println(e.toString());
		}
		System.out.println("Vising with null args, assuming error");
		try{
			card2.vise(null, "HelloPassword");
		} catch (NullPointerException npe){
			System.out.println(npe.toString());
		}
		System.out.println("Trying to vise while being a chief, assuming error");
		try{
			card2.vise(c1, "HelloPassword");
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}
		System.out.println("Trying to vise while not being a controller, assuming error");
		try{
			Commander c12 = new Commander ();
			card2.vise(c12, "HelloPassword", "DON!");
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}

		Card card3 = new Card(c1, s1, d1, ss1, dd1, sc);
		System.out.println("Vising with comment but with wrong password, assuming error");
		try{
			card3.vise(c10,"222", "done!");
		} catch (IllegalArgumentException ie){
			System.out.println(ie.toString());
		}
		System.out.println("Vising with wrong password without comment");
		try{
			card3.vise(c11, "664");
		} catch (IllegalArgumentException ie){
			System.out.println(ie.toString());
		}
		
		
		System.out.println("\nTesting pushing");
		System.out.println("Legally pushing to the chief for two cards");
		Commander ch1 = card.getChiefController();
		card.push(ch1);
		System.out.println("getPushed() for the first card: " + card.getPushed(ch1));
		Commander ch2 = card2.getChiefController();
		card2.push(ch2);
		System.out.println("getPushed() for the second card: " + card2.getPushed(ch2));
		System.out.println("Now rejecting");
		card.reject(ch1);
		card2.reject(ch2);
		System.out.println("getPushed() for the first card: " + card.getPushed(ch1));
		System.out.println("getPushed() for the second card: " + card2.getPushed(ch2));
		System.out.println("Legally pushing to the secondary controlers");
		card.push(c2);
		System.out.println("getPushed() for the first card: " + card.getPushed(c2));
		System.out.println("Now rejecting");
		card.reject(c2);
		System.out.println("getPushed() for the first card: " + card.getPushed(c2));
		System.out.println("Pushing to non-existent controller, assuming error");
		try{		
			card.push(c0);
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}
		System.out.println("Rejecting to non-existent controller, assuming error");
		try{		
			card.reject(c0);
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}
		System.out.println("Getting pushed of non-existent controller, assuming error");
		try{		
			card.getPushed(c0);
		} catch (IllegalArgumentException e){
			System.out.println(e.toString());
		}
		

		System.out.println("");
		System.out.println("===========Card.class tested===========\n");
	};	
}