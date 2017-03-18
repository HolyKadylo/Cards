/** @author Illya Piven
 * This class is used for http exchange
 */
package com.kadylo.kmdb;

import java.io.IOException;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AccessFilter implements Filter{

	@Override
	public void doFilter(ServletRequest req, ServletResponse response, FilterChain next)throws IOException, ServletException {
		System.out.println("Doing access filter");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession(true);
		HttpServletResponse resp = (HttpServletResponse) response;
		try{
			if (session.getAttribute("authorized").equals("false")){
				System.out.println("Unauthorized session");
				//RequestDispatcher dispatcher = request.getRequestDispatcher("login.html?msg=una");
				//dispatcher.forward(request, response);
				resp.sendRedirect("login.html?msg=una");
			}
			if (session.getAttribute("role").equals("commander") && session.getAttribute("authorized").equals("true")){
				System.out.println("Authorized filtering, commander");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/commandermanager");
				dispatcher.forward(request, response);
			}
			if (session.getAttribute("role").equals("soldier") && session.getAttribute("authorized").equals("true")){
				System.out.println("Authorized filtering, soldier");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/soldiermanager");
				dispatcher.forward(request, response);
			}
		} catch (NullPointerException npe){
			System.out.println("Unauthorized session");
			//RequestDispatcher dispatcher = request.getRequestDispatcher("login.html?msg=una");
			//dispatcher.forward(request, response);
			resp.sendRedirect("login.html?msg=una");
		}
		//next.doFilter(request, response);
	}

	@Override
	public void destroy() {
	
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
	
	public static void main(String[] args){
	
	}
}