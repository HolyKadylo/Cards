/** @author Illya Piven
 * This class is used to run the whole application
 */
package com.kadylo.kmdb;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Application extends HttpServlet{
	private static final long serialVersionUID = 1L;

	// FN is for FileName
	private static final String loginFN = "login page.html";
	private static final String stylesFN = "styles.css";
	private static final String DOMAIN = "localhost";
	private static final String SESSION_COOKIE_NAME = "cardSession";

	// cookieValue + whenToDelete
	private static HashMap <String, Date> validCookies = new HashMap <String, Date>();

	// sends login page to user
	public void sendLogin(){

	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if ( !cookie.getName().equals(SESSION_COOKIE_NAME) )
				continue;
			if ( !cookie.getDomain().equals(DOMAIN) )
				continue;
			String value = c.getValue();
			if (validCookies.contains(value))
				if(validCookies.get(value).after*************)
		}

		response.setContentType("text/html");
		try (PrintWriter writer = response.getWriter()) {
			writer.println("<!DOCTYPE html><html>");
			writer.println("<head>");
			writer.println("<meta charset=\"UTF-8\" />");
			writer.println("<title>this worked</title>");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("<h1>This worked</h1>");
			writer.println("<p>");
			writer.println("this worked <br>");
			writer.println("this worked.");
			writer.println("</p>");
			writer.println("</body>");
			writer.println("</html>");
		}
	}

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