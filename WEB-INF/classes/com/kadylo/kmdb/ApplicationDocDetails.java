/** @author Illya Piven
 * This servlet is used when user wants view the directive document
 */
package com.kadylo.kmdb;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ApplicationDocDetails extends HttpServlet{
	private static final long serialVersionUID = 3L;

	/*@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException {
	
	}*/

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
		System.out.println("Doing doc details GET");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html><html><head><title>��������</title></head><body>��������� ��������<br>������� ����� ��� backspace<script>alert('����� ����� ������������ ������������� �������� � ��������������� ����')</script></body></html>");
	}

	public static void main(String[] args){
	
	}
}