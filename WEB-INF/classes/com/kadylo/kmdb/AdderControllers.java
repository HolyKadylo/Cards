/** @author Illya Piven
 * This class is used as the request processor when adding
 * controllers to the card
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class AdderControllers extends HttpServlet{
	private static final long serialVersionUID = 17L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	private static final String PATH_TO_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/addercontrollersTemplate.html";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing adder controllers POST");
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

		for (int directSlaveID : commander.getDirectSlaves()){
			Commander directSlave = db.getCommander(directSlaveID);

			// skipping already involved commander
			if(card.getSecondaryControllers().containsKey(directSlave))
				continue;
			String task = request.getParameter("coex" + String.valueOf(directSlaveID));
			System.out.println("TASK: " + task);
			if (!task.equals("") && !task.equals(" ")){
				card.addController(db.getCommander(directSlaveID), task);
				System.out.println("added");
			}
		};
		try{
			db.addCard(card);
		} catch (SQLException e){
			PrintWriter out = response.getWriter();
			//out.println("<!DOCTYPE html><html><head><script>alert(\"Данные не были записаны!\")</script></head><body></body></html>");
			response.sendRedirect("/dashboard?executionFocus=" + cardTag + "&msg=ntwerttn");
			return;
		}
		response.sendRedirect("/cards/dashboard?executionFocus=" + cardTag + "&msg=coadd");
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing adder controllers GET");
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
		File adderControllersFile= new File(PATH_TO_TEMPLATE);

		// reading the template
		String addControllersString = FileUtils.readFileToString(adderControllersFile, "windows-1251");
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		addControllersString = addControllersString.replace("$username", commander.getFirstName());
		String cardTag = request.getParameter("focus");
		Card card = db.getCard(cardTag);
		addControllersString = addControllersString.replace("$editingCardNum", card.getId());
		addControllersString = addControllersString.replace("$editingCardTask", card.getTask());

		// making free soldiers list
		//private HashMap <Soldier, String> secondaryExecutors;
		String content = "";
		for (int directSlaveID : commander.getDirectSlaves()){
			Commander directSlave = db.getCommander(directSlaveID);

			// skipping already involved commander
			if(card.getSecondaryControllers().containsKey(directSlave))
				continue;
			content = content + "<li><b>" + directSlave.getFirstName() + " " + directSlave.getLastName() + ": <br></b>.<input type=\"text\" size = \"45\" name=\"coex" + String.valueOf(directSlaveID) + "\"></li>";
		};
		if (content.equals(""))
			content = "К сожалению, все ваши подчиненные уже заняты в выполнении этой карты";
		addControllersString = addControllersString.replace("$coExecutorsList", content);
		addControllersString = addControllersString.replace("$submitFocus", cardTag);
		out.println(addControllersString);
	}

	public static void main(String[] args){
	
	}
}