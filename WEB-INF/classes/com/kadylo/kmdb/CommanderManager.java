/** @author Illya Piven
 * This class is used as the main for Commander users
 * Needs to be placed in path without spaces.
 */
package com.kadylo.kmdb;
це через пост-гет
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.sql.SQLException;

public class CommanderManager extends HttpServlet{
	private static final long serialVersionUID = 3L;
	private static final String PATH = DataBase.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	private static final String PATH_TO_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/commanderTemplate.html";
	private static final String PATH_TO_CONTENT_PREVIEW_TEMPLATE = PATH.substring(0, PATH.indexOf("classes")).replace("%20", " ") + "html/commanderCardPreview.txt";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
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

			// means that it was selected
			if (executionFocus.equals(db.getCard(tag).getId())){
				Card card = db.getCard(tag);
				
				// this executor will be deleted;
				String deleteEx;
				{deleteEx = request.getParameter("deleteEx");}
				try{
					if (deleteEx != null){
						card.removeExecutor(db.getSoldier(Integer.parseInt(deleteEx)));

						// putting card back
						try{
							db.addCard(card);
						} catch (SQLException e){
							//TODO alert
						}
					}
				} catch (NoSuchElementException nsee) {

					//nothing 
				}
				try{
					cardsToExecuteList = cardsToExecuteList + "<li><b><a href = \"?executionFocus=" + card.getId() + "\">" + card.getTask().substring(0, 20) + "...</a></b></li>";
				} catch (StringIndexOutOfBoundsException sioobe){
					cardsToExecuteList = cardsToExecuteList + "<li><b><a href = \"?executionFocus=" + card.getId() + "\">" + card.getTask() + "</a></b></li>";
				}

				// printing selected card
				File contentFile = new File(PATH_TO_CONTENT_PREVIEW_TEMPLATE);
				String content = FileUtils.readFileToString(contentFile, "windows-1251");
				content = content.replace("$cardCode", card.getId());
				try{
					content = content.replace("$cardName", card.getTask().substring(0, 20) + "...");
				} catch (StringIndexOutOfBoundsException sioobe){
					content = content.replace("$cardName", card.getTask());
				}
				content = content.replace("$chiefController", card.getChiefController().getLastName());
				content = content.replace("$directiveDate", dateFormat.format(card.getDirective()));
				Date now = new Date();
				content = content.replace("$daysRemain", String.valueOf(1 + java.lang.Math.round((card.getDirective().getTime() - now.getTime())/1000/60/60/24)));
				content = content.replace("$docCode", card.getDocument().getNumber() + "/" + card.getDocument().getDepartment());
				content = content.replace("$docTag", card.getDocument().getTitle());
				content = content.replace("$docStar", card.getDocument().getStar().getLastName());
				content = content.replace("$dStarDepartment", String.valueOf(card.getDocument().getStar().getDepartment()));
				content = content.replace("$cardTask", card.getTask());

				// forming $listOfSecondaryExecutors
				String listOfSecondaryExecutors = "";

				// this executor will be modified;
				String changeEx;
				Soldier soldierToModify = null;
				{
					changeEx = request.getParameter("changeEx");
				}
				try{
					if (changeEx != null){
						soldierToModify = db.getSoldier(Integer.parseInt(changeEx));
					}
				} catch (NoSuchElementException nsee) {

					//nothing 
				}

				// this will be the new task
				String newTask = null;
				{newTask = request.getParameter("newTask");
				System.out.println("newTask: " + newTask);
				}
				try{
					if (newTask != null){
						card.changeTask(db.getSoldier(Integer.parseInt(deleteEx)), newTask);
						// putting card back
						try{
							db.addCard(card);
						} catch (SQLException e){
							//TODO alert
						}
					}
				} catch (NoSuchElementException nsee) {

					//nothing 
				}
				for(Soldier secondExecutor : card.getSecondaryExecutors().keySet()){
					if (!secondExecutor.equals(soldierToModify))
						listOfSecondaryExecutors = listOfSecondaryExecutors + "<li>" + secondExecutor.getLastName() + ": " + card.getSecondaryExecutors().get(secondExecutor) + "<br><a href=\"?executionFocus=" + card.getId() + "&deleteEx=" + String.valueOf(secondExecutor.getId()) + "\">[Убрать]</a><a href=\"?executionFocus=" + card.getId() + "&changeEx=" + String.valueOf(secondExecutor.getId()) + "\">[Изменить формулировку]</a><br>." + "</li>";
					if (secondExecutor.equals(soldierToModify))
						listOfSecondaryExecutors = listOfSecondaryExecutors + "<li>" + secondExecutor.getLastName() + ": <form method=\"POST\"><input type=\"text\" size=\"25\" name=\"newTask\" placeholder=\"" + card.getSecondaryExecutors().get(secondExecutor) + "\"><br><a href=\"?executionFocus=" + card.getId() + "&deleteEx=" + String.valueOf(secondExecutor.getId()) + "\">[Убрать]</a><input type=\"submit\" value=\"[OK]\"><br>." + "</form></li>";
				}
				content = content.replace("$listOfSecondaryExecutors", listOfSecondaryExecutors);
				commanderString = commanderString.replace("$content", content);
			
			// means that it wasn't selected
			} else {
				try{
					cardsToExecuteList = cardsToExecuteList + "<li><a href = \"?executionFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask().substring(0, 20) + "...</a></li>";
				} catch (StringIndexOutOfBoundsException sioobe){
					cardsToExecuteList = cardsToExecuteList + "<li><a href = \"?executionFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></li>";
				}
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
				Card card = db.getCard(tag);
				try{
					cardsToControllList = cardsToControllList + "<li><b><a href = \"?controllFocus=" + card.getId() + "\">" + card.getTask().substring(0, 20) + "...</a></b></li>";
				} catch (StringIndexOutOfBoundsException sioobe){
					cardsToControllList = cardsToControllList + "<li><b><a href = \"?controllFocus=" + card.getId() + "\">" + card.getTask() + "</a></b></li>";
				}

				// printing selected card
				File contentFile = new File(PATH_TO_CONTENT_PREVIEW_TEMPLATE);
				String content = FileUtils.readFileToString(contentFile, "windows-1251");
				//content = content.replace("$username", commander.getFirstName());
				commanderString = commanderString.replace("$content", content);
			} else {
				try{
					cardsToControllList = cardsToControllList + "<li><a href = \"?controllFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask().substring(0, 20) + "...</a></li>";
				} catch (StringIndexOutOfBoundsException sioobe){
					cardsToControllList = cardsToControllList + "<li><a href = \"?controllFocus=" + db.getCard(tag).getId() + "\">" + db.getCard(tag).getTask() + "</a></li>";
				}
			};
		}
		commanderString = commanderString.replace("$listOfCardsToControll", cardsToControllList);

		// if $content was not modified, replacing will default value
		try {
			commanderString = commanderString.replace("$content", "Здесь будут отображаться ваши карты");
		} catch (Exception e){
			System.out.println("Exception when modifying $content: " + e.toString());
		}
		out.println(commanderString);
	}

	public static void main(String[] args){
	
	}
}