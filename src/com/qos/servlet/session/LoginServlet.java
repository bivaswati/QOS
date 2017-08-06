/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * File Name    : LoginServlet.java.
 * Author       : Bivas,Balaji A.
 * Date         : 05-May-2016
 */

package com.qos.servlet.session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *  
 * Servlet implementation class LoginServlet
 * 
 * 29 Jan 2016 Balaji Alagarsamy  -created for creating a servlet session id
 * 
 */
 @WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // get request parameters for userID and password
        HttpSession session = request.getSession();
        String clientIP = request.getRemoteAddr();
        System.out.println("Client ip in loginservlet: " + clientIP);
        session.setAttribute("clientIp", clientIP);
        response.sendRedirect("sdnhome.html");
        System.out.println("session id in login servlet: " + session.getId());
    }
}