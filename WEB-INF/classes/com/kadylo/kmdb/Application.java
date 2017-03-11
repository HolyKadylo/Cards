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
		response.setContentType("text/html; charset=UTF-8");
		//response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);

		// print session info
		Date created = new Date(session.getCreationTime());
		Date accessed = new Date(session.getLastAccessedTime());
		out.println("<!DOCTYPE html><html><head><title>CARDS</title></head><body>");
		out.println("ID " + session.getId());
		out.println(" Created: " + created);
		out.println(" Last Accessed: " + accessed);
		
		System.out.println("HELLO");
		try{
			DataBase db = DataBase.access();
			Soldier sol = db.getSoldier(1703);
			
			//http://stackoverflow.com/questions/8278287/how-to-print-utf-8-in-jsp
			//new String(query.getBytes(),"UTF-8")
			// ISO-8859 -- Windows encoding
			//out.println(new String(sol.getFirstName().getBytes("windows-1251"),"UTF-8") + " " + new String(sol.getLastName().getBytes("windows-1251"), "UTF-8") + " " + sol.getDepartment());
			out.println(sol.getFirstName() + " " + sol.getLastName() + " " + sol.getDepartment());
		} catch (Exception e){
			e.printStackTrace();
			System.out.println(e.toString());
		}
		
		out.println(" LOG: " + request.getParameter("login"));
		out.println(" PASS: " + request.getParameter("pass"));
		out.println("</body></html>");

		// set session info if needed
		String dataName = request.getParameter("dataName");
		if (dataName != null && dataName.length() > 0) {
			String dataValue = request.getParameter("dataValue");
			session.setAttribute(dataName, dataValue);
		}
	}

	/* Test */
	public static void main(String[] args){
		System.out.println("=========Testing Application.class=========\n");
		DataBase db = DataBase.access();
		Soldier sol = db.getSoldier(1703);
		System.out.println(sol.getFirstName() + " " + sol.getLastName() + " " + sol.getDepartment());
		try{
			System.out.println("testing encoding: ");
			System.out.println(new String(sol.getFirstName().getBytes(),"UTF-8") + " " + new String(sol.getLastName().getBytes(), "UTF-8") + " " + sol.getDepartment());
		} catch (UnsupportedEncodingException e){
			System.out.println("Unsupported encoding");
		}
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