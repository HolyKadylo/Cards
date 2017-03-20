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

public class CommanderManager extends HttpServlet{
	private static final long serialVersionUID = 3L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	private static final String PATH_TO_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/commanderTemplate.html";
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
		System.out.println("Doing commander POST");
		/* response.setContentType("text/html; charset=UTF-8");
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
		DataBase db = DataBase.access(); */		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing commander GET");
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
		File commanderFile = new File(PATH_TO_TEMPLATE);

		// reading the template
		String commanderString = FileUtils.readFileToString(commanderFile, "windows-1251");
		Commander commander = db.getCommander((Integer)session.getAttribute("tabelNumber"));
		Soldier soldier = db.getSoldier((Integer)session.getAttribute("tabelNumber"));
		commanderString = commanderString.replace("$username", commander.getFirstName());
		commanderString = commanderString.replace("$numCardsToExecute", String.valueOf(soldier.getCardsToExecute().size()));
		commanderString = commanderString.replace("$numCardsToControll", String.valueOf(commander.getCardsToControll().size()));
		

		// forming cards to execute
		String cardsToExecuteList = "";
		String executionFocus;
		{executionFocus = request.getParameter("executionFocus");}
		if ( executionFocus == null)
			executionFocus = " ";
		for (String tag : soldier.getCardsToExecute()){
			if (executionFocus.equals(db.getCard(tag).getId())){

				// means that it was selected
				cardsToExecuteList = cardsToExecuteList + "<li><b><a href = \"?executionFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></b></li>";
			} else {
				cardsToExecuteList = cardsToExecuteList + "<li><a href = \"?executionFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></li>";
			};
		}
		commanderString = commanderString.replace("$listOfCardsToExecute", cardsToExecuteList);

		// forming cards to controll
		String cardsToControllList = "";
		String controllFocus;
		{controllFocus = request.getParameter("controllFocus");}
		if (controllFocus == null)
			controllFocus = " ";
		for (String tag : commander.getCardsToControll()){
			if (controllFocus.equals(db.getCard(tag).getId())){
				cardsToControllList = cardsToControllList + "<li><b><a href = \"?controllFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></b></li>";
			} else {
				cardsToControllList = cardsToControllList + "<li><a href = \"?controllFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></li>";
			};
		}
		commanderString = commanderString.replace("$listOfCardsToControll", cardsToControllList);

		out.println(commanderString);
	}

	public static void main(String[] args){
	
	}
}