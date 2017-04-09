/** @author Illya Piven
 * This class is used to add cards to the database
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class AdderCards extends HttpServlet{
	private static final int CHIEFS_DEPARTMENT = 999;
	private static final long serialVersionUID = 19L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	private static final String PATH_TO_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/adderCardsTemplate.html";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
	
	// contains single line of salt that is added to the "ids"
	private static final String PATH_TO_SALT = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "classes/resources/salt.txt";
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing adder cards POST");
		HttpSession session = request.getSession(true);
		DataBase db = DataBase.access();
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		String cardTag = request.getParameter("focus");
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
			db.addCard(card);
		} catch (SQLException e){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"������ �� ���� ��������!\")</script></head><body></body></html>");
			response.sendRedirect("/dashboard?executionFocus=" + cardTag + "&msg=ntwerttn");
			return;
		}
		response.sendRedirect("/cards/dashboard?executionFocus=" + cardTag + "&msg=coadd");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing adder cards GET");
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
			return;
		}
		DataBase db = DataBase.access();

		// http://stackoverflow.com/questions/5936003/write-html-file-using-java
		File adderCardsFile = new File(PATH_TO_TEMPLATE);

		// reading the template
		String adderCardsString = FileUtils.readFileToString(adderCardsFile, "windows-1251");
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		adderCardsString = adderCardsString.replace("$username", commander.getFirstName());
		adderCardsString = adderCardsString.replace("$cardCode", generateCode(commander));
		
		// Forming chief controllers list
		String chiefControllers = "";
		
		// adding salt
		File saltFile = new File(PATH_TO_SALT);
		String salt = FileUtils.readFileToString(saltFile, "windows-1251");
		for (Commander can : db.getCommanders(CHIEFS_DEPARTMENT)){
			chiefControllers = chiefControllers + "<option value = \"" + String.valueOf(String.valueOf(can.getId() + salt).hashCode()) + "\">" + can.getLastName() + "</option>";
		}
		adderCardsString = adderCardsString.replace("$chiefControllers", chiefControllers);
		
		// forming primary executors list (heads of departments)
		String primaryExecutors = "";
		for (Soldier sol : db.getSoldiers()){
			primaryExecutors = primaryExecutors + "<option value = \"" + String.valueOf(String.valueOf(sol.getId() + salt).hashCode()) + "\">" + sol.getLastName() + "</option>";
		}
		adderCardsString = adderCardsString.replace("$primaryExecutors", primaryExecutors);
		out.println(adderCardsString);
	}

	// generating codes for card using 36 position numeration system
	private String generateCode(Commander commander){
		//generating string:
		String code = commander.getLastName() + String.valueOf(new Date().getTime()); 
		int iCode = code.hashCode();
		if (iCode<=0)
			iCode = -1 * iCode;
		Number36 num = new Number36();
		return num.getValue(iCode);
	}
	
	private class Number36{
		String digits = "";
		private String getValue(int decimal){
			while (decimal >= 36) {
				digits = digits + correlate(decimal%36);
				decimal = (decimal - (decimal%36)) / 36;
			}
			return digits;
		}

		private String correlate(int i){
			switch (i){
				case 0: return "0";
				case 1: return "1";
				case 2: return "2";
				case 3: return "3";
				case 4: return "4";
				case 5: return "5";
				case 6: return "6";
				case 7: return "7";
				case 8: return "8";
				case 9: return "9";
				case 10: return "A";
				case 11: return "B";
				case 12: return "C";
				case 13: return "d";
				case 14: return "E";
				case 15: return "F";
				case 16: return "G";
				case 17: return "H";
				case 18: return "i";
				case 19: return "J";
				case 20: return "K";
				case 21: return "L";
				case 22: return "M";
				case 23: return "N";
				case 24: return "o";
				case 25: return "P";
				case 26: return "Q";
				case 27: return "R";
				case 28: return "S";
				case 29: return "T";
				case 30: return "U";
				case 31: return "V";
				case 32: return "W";
				case 33: return "X";
				case 34: return "Y";
				case 35: return "Z";
				case 36: return "-";
				default: return "=";
			}
		}
	}

	public static void main(String[] args){
	
	}
}