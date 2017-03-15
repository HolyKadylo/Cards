/** @author Illya Piven
 * This interface allows is used for accounts that can command
 * another accounts what to do and control it. Example -- directors
 * of departments and chief directors
 */
package com.kadylo.kmdb.interfaces;

interface Commanders{
	
	/**
	 * Creates card from statement for example in 
	 * the directive document
	 * @param chiefController is for person who signs directive document
	 * @param executor is for person who is to execute the statement
	 * @param term is for date untill which card should be executed
	 * @param document is for directive document which is taken from DB
	 * @param task is for point in that document containing strict task
	 * @return on success returns created card, on failure --
	 * @throws noSuchDocumentException when there is no such signed document
	 * in database
	 * @throws 
	 */
	Card createCard(Commander chiefController, Soldier executor, Date term, DirectiveDocument document, String task, Commander producer, Soldier) throws noSuchDocumentException;
	
	/**
	 * Signs card and marks it as executed
	 * @param card is for card to be executed
	 * @param date is for current date and time
	 * @param comment is for possible comment for the future
	 * @return true on success and false otherwise
	 * @throws
	 */
	boolean signCard(Card card, Date date, String comment);

	/**
	 * Signs card and marks
	 * @param card is for card to be vised
	 * @param date is for current date and time
	 * @param comment is for possible comment for higher controller
	 * @return true on success and false otherwise
	 * @throws
	 */
	boolean viseCard(Card card, Date date, String comment);
		
}