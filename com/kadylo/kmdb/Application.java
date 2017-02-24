/** @author Illya Piven
 * This class is used to run the whole application
 */
package com.kadylo.kmdb;

public class Application{
	public static void main(String[] args){
		System.out.println("=========Testing Application.class=========\n");
		DataBase db = DataBase.access();
		Soldier sol = db.getSoldier(1703);
		System.out.println(sol.getFirstName() + " " + sol.getLastName() + " " + sol.getDepartment());
		System.out.println("=========Application.class tested=========\n");
	}
}