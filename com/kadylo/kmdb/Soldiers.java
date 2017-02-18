/** @author Illya Piven
 * This interface is used for executors i.e. for people that can perform the tasks -- directors of departments,
 * sectors, chief contsructors and simple workers. In whole, soldiers are those who obey orders
 */
package com.kadylo.kmdb;

public interface Soldiers{
	//public boolean equals(Soldier candidate);
	void push(Card card, Commander commander);
	
	void addSecondaryExecutor(
		Card card, 
		Soldier Soldier, 
		String pointToControll);
	
	void removeSecondaryExecutor(
		Card card, 
		Soldier soldier);

	void changeTask(
		Card card, 
		Soldier soldier,
		String newTask);
}