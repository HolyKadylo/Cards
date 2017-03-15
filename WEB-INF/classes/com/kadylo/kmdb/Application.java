/** @author Illya Piven
 * This class is used to run the whole application
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.NoSuchElementException;

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
	// ../auth
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		DataBase db = DataBase.access();		

		// print session info
		out.println("<!DOCTYPE html><html><head><title>CARDS</title></head><body>");
		out.println("ID " + session.getId());
		String login = request.getParameter("login");
		String password = request.getParameter("pass");
		Soldier soldier;
		Commander commander;
		try{
			try{
				System.out.println("Trying integer login");				

				// true when login is tabel number
				int log = Integer.parseInt(login);
				soldier = db.getSoldier(log);
				commander = db.getCommander(log);	
			
				// should have password!
				System.out.println("PROV_PASS: " + password);
				System.out.println("COMM_PASS: " + commander.getPassword());
				if (!password.equals(commander.getPassword())){
					System.out.println("Throwing \"wrong password\"");
					throw new IllegalArgumentException ("Wrong password");
				}
				System.out.println("Successfully tried integer login");				
			} catch (NumberFormatException nfe){
				System.out.println("Catching NumberFormatException");				

				// means that name was inserted
				// therefore retreiving only soldier
				soldier = 	db.getSoldier(Integer.parseInt(password));
				System.out.println("LOGIN: " + login);
				System.out.println("ACTUAL: " + soldier.getFirstName() + " " + soldier.getLastName());
				if (!login.equals(soldier.getFirstName() + " " + soldier.getLastName())){
					System.out.println("Throwing \"wrong login or password\"");
					throw new IllegalArgumentException("Wrong login or password");	
				}
			}

		} catch (NoSuchElementException nsee){
			
			//means that there is no such login
			response.sendRedirect("login.html?msg=lgn");
		} catch (IllegalArgumentException iae){

			// means wrong password or wrong password and login
			if(iae.toString().equals("Wrong password"))
				response.sendRedirect("login.html?msg=psw");
			if(iae.toString().equals("Wrong login or password"))
				response.sendRedirect("login.html?msg=pswlgn");
		} catch (Exception e){

			// means something unexpected
			e.printStackTrace();
			System.out.println(e.toString());
			response.sendRedirect("error.html?msg=err");
		}
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