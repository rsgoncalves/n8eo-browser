/*******************************************************************************
 * This file is part of N8EO Browser.
 * 
 * N8EO Browser is licensed under a Creative Commons Attribution 3.0 Unported License.
 * 
 * Copyright 2013, The University of Manchester
 * 
 * To view a copy of the license, visit http://creativecommons.org/licenses/by/3.0/deed.en_US
 ******************************************************************************/
package uk.ac.manchester.cs.n8eo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class N8EO_Browser
 */
@WebServlet("/N8EO_Browser")
public class N8EO_Browser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public N8EO_Browser() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
}
