/** @author Illya Piven
 * This interface is used for directors i.e. for people that can create and enwrite cards -- directors 
 * of departments, sectors, chief contsructors and so on. In whole, commanders are those
 * who manage cards, but does not execute them
 */
package com.kadylo.kmdb;

import java.util.ArrayList;

public interface Commanders{
	
	public boolean equals(Object candidate);
	Card createCard();
	void sign(Card card, String pass);
	void vise(Card card, String pass, String comment);
	void removeController(Card card, Commander controller);
	void changeTask(Card card, Commander controller, String newTask);
	void addController(Card card, Commander controller, String task);
	void reject(Card card);
	void riseAlert(Commander comm, Card card);
	
	/*Deprecated*/
	//void changePasswordSequence(ArrayList<String> newSequence);
}