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

		Card card = DataBase.access().getCard("12");
		System.out.println("OUT-CHI: " + card.getChiefController().getId());
		System.out.println("OUT-PRI: " + card.getPrimaryExecutor().getId());
		//card.setTask("Создать 123");
		card.setClosed(1);
		card.addController(db.getCommander(90), "NINTU");
		card.addController(db.getCommander(1000), "TAUSAND");
		try{
			DataBase.access().addCard(card);
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("=========Application.class tested=========\n");
	}
}