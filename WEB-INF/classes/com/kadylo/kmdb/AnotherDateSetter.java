/** @author Illya Piven
 * This class is used when some task is outdated
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;

public class AnotherDateSetter extends HttpServlet{
	private static final long serialVersionUID = 16L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();

	// this is date format used in google chrome date picker
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing changing date POST");
		HttpSession session = request.getSession(true);
		DataBase db = DataBase.access();
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		String cardTag = (String) session.getAttribute("cardFocus");
		Card card = db.getCard(cardTag);
		
		// bouncing back unauthorized session
		try{
			if(session.getAttribute("authorized").equals("false")){
				response.sendRedirect("login.html?msg=una");
			}
		} catch (NullPointerException npe) {
			response.sendRedirect("login.html?msg=una");
			return;
		}
		
		try{
			Date date = dateFormat.parse(request.getParameter("anotherDate"));
			card.setDirective(date);
		} catch (ParseException pe){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"Данные не были записаны!\")</script></head><body></body></html>");
			response.sendRedirect("/dashboard?executionFocus=" + cardTag + "&msg=ntwerttn");
			return;
		}
		
		try{
			db.addCard(card);
		} catch (SQLException e){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"Данные не были записаны!\")</script></head><body></body></html>");
			response.sendRedirect("/dashboard?executionFocus=" + cardTag + "&msg=ntwerttn");
			return;
		}
		response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag + "&msg=date");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing changing date GET");
	}

	public static void main(String[] args){
	
	}
}