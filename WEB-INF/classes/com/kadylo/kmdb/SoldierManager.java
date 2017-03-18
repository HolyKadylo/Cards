/** @author Illya Piven
 * This class is used as the main for Solider users
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SoldierManager extends HttpServlet{
	private static final long serialVersionUID = 2L;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing soldier POST");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		
		// bouncing back unauthorized session
		try{
			if(session.getAttribute("authorized").equals("false")){
				response.sendRedirect("login.html?msg=una");
			}
		} catch (NullPointerException npe) {
			response.sendRedirect("login.html?msg=una");
		}
		DataBase db = DataBase.access();		
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing soldier GET");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		
		// bouncing back unauthorized session
		try{
			if(session.getAttribute("authorized").equals("false")){
				response.sendRedirect("login.html?msg=una");
			}
		} catch (NullPointerException npe) {
			response.sendRedirect("login.html?msg=una");
		}
		DataBase db = DataBase.access();
	}
	
	public static void main(String[] args){
	
	}
}