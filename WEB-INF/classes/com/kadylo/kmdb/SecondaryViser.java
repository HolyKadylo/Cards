/** @author Illya Piven
 * This class is used for commander that wants to vise the card
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.SQLException;
import javax.print.attribute.UnmodifiableSetException;

public class SecondaryViser extends HttpServlet{
	private static final long serialVersionUID = 9L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing secondaryViser POST");
		HttpSession session = request.getSession(true);
		DataBase db = DataBase.access();
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
	System.out.println("Secondary viser commander num " +(Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		String cardTag = (String) session.getAttribute("cardFocus");
		Card card = db.getCard(cardTag);
	System.out.println("Secondary viser cardTag " +cardTag);
		String comment;
		try{
			comment = request.getParameter("comment");
		} catch (NullPointerException npe){
			comment = "";
		}
		String password = request.getParameter("comVise");
		

		// bouncing back unauthorized session
		try{
			if(session.getAttribute("authorized").equals("false")){
				response.sendRedirect("login.html?msg=una");
			}
		} catch (NullPointerException npe) {
			response.sendRedirect("login.html?msg=una");
			return;
		}
		
		//returinig if wrong password
		if (!password.equals(commander.getPassword())){
			System.out.println("DOESNT EQUAL");
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"Неверный пароль!\")</script></head><body></body></html>");
			response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag + "&msg=wrnps");
			return;
		}
		
		//vising
		try{
			card.vise(commander, password, comment);
		} catch (IllegalArgumentException iae){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"" + iae.toString() + "\")</script></head><body></body></html>");
			response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag + "&msg=unk");
			return;
		} catch (UnmodifiableSetException use){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"" + use.toString() + "\")</script></head><body></body></html>");
			response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag  + "&msg=unk");
			return;
		}

		try{
			db.addCard(card);
		} catch (SQLException e){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"Данные не были записаны!\")</script></head><body></body></html>");
			response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag + "&msg=ntwerttn");
			return;
		}
		PrintWriter out = response.getWriter();
		//out.println("<!DOCTYPE html><html><head><script>alert(\"Карта успешно подписана\")</script></head><body></body></html>");
		response.sendRedirect("/cards/dashboard?controllFocus=" + cardTag  + "&msg=crdsgn");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing secondaryViser GET");
	}

	public static void main(String[] args){
	
	}
}