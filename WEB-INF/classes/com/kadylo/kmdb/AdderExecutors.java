/** @author Illya Piven
 * This class is used as the main for Commander users
 * Needs to be placed in path without spaces.
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class AdderExecutors extends HttpServlet{
	private static final long serialVersionUID = 7L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	private static final String PATH_TO_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/adderexecutorsTemplate.html";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing adder executors POST");
		HttpSession session = request.getSession(true);
		DataBase db = DataBase.access();
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		String cardTag = request.getParameter("focus");
		Card card = db.getCard(cardTag);
		
		for (int directSlaveID : commander.getDirectSlaves()){
			Soldier directSlave = db.getSoldier(directSlaveID);

			// skipping already involved soldier
			if(card.getSecondaryExecutors().containsKey(directSlave))
				continue;
			String task = request.getParameter("coex" + String.valueOf(directSlaveID));
			if (!task.equals("") && !task.equals(" "))
				card.addExecutor(db.getSoldier(directSlaveID), task);
		};
		try{
			db.addCard(card);
		} catch (SQLException e){
			PrintWriter out = response.getWriter();
			out.println("<!DOCTYPE html><html><head><script>alert(\"Данные не были записаны!\")</script></head><body></body></html>");
			response.sendRedirect("/dashboard?executionFocus=" + cardTag);
		}
		response.sendRedirect("/cards/dashboard?executionFocus=" + cardTag);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing adder executors GET");
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

		//http://stackoverflow.com/questions/5936003/write-html-file-using-java
		File adderExecutorsFile= new File(PATH_TO_TEMPLATE);

		// reading the template
		String addExecutorsString = FileUtils.readFileToString(adderExecutorsFile, "windows-1251");
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		addExecutorsString = addExecutorsString.replace("$username", commander.getFirstName());
		String cardTag = request.getParameter("focus");
		Card card = db.getCard(cardTag);
		addExecutorsString = addExecutorsString.replace("$editingCardNum", card.getId());
		addExecutorsString = addExecutorsString.replace("$editingCardTask", card.getTask());

		// making free soldiers list
		//private HashMap <Soldier, String> secondaryExecutors;
		String content = "";
		for (int directSlaveID : commander.getDirectSlaves()){
			Soldier directSlave = db.getSoldier(directSlaveID);

			// skipping already involved soldier
			if(card.getSecondaryExecutors().containsKey(directSlave))
				continue;
			content = content + "<li><b>" + directSlave.getFirstName() + " " + directSlave.getLastName() + ": <br></b>.<input type=\"text\" size = \"45\" name=\"coex" + String.valueOf(directSlaveID) + "\"></li>";
		};
		if (content.equals(""))
			content = "К сожалению, все ваши подчиненные уже заняты в выполнении этой карты";
		addExecutorsString = addExecutorsString.replace("$coExecutorsList", content);
		addExecutorsString = addExecutorsString.replace("$submitFocus", cardTag);
		out.println(addExecutorsString);
	}

	public static void main(String[] args){
	
	}
}