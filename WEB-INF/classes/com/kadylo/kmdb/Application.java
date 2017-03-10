/** @author Illya Piven
 * This class is used to run the whole application
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Application extends HttpServlet{
	private static final long serialVersionUID = 1L;

	// FN is for FileName
	private static final String loginFN = "login page.html";
	private static final String stylesFN = "styles.css";

	// cookieValue + whenToDelete
	private static HashMap <String, Date> validCookies = new HashMap <String, Date>();

	// sends login page to user
	public void sendLogin(){

	}
	
	/*http://localhost:8080/examples/servlets/sessions.html */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);

		// print session info
		Date created = new Date(session.getCreationTime());
		Date accessed = new Date(session.getLastAccessedTime());
		out.println("ID " + session.getId());
		out.println(" Created: " + created);
		out.println(" Last Accessed: " + accessed);
		out.println(" LOG: " + request.getParameter("login"));
		out.println(" PASS: " + request.getParameter("pass"));
		Commander com = DataBase.access().getCommander(Integer.parseInt(request.getParameter("login")));
		out.println(" NAME: " + com.getFirstName());
		

		// set session info if needed
		String dataName = request.getParameter("dataName");
		if (dataName != null && dataName.length() > 0) {
			String dataValue = request.getParameter("dataValue");
			session.setAttribute(dataName, dataValue);
		}

		// print session contents
		/*Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String)e.nextElement();
			String value = session.getAttribute(name).toString();
			out.println(name + " = " + value);
		}*/
	}

	/* Test */
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