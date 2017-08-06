/*
 * Copyright 2016 WIPRO Technologies Limited
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
 * File Name    : EventPollServlet.java.
 * Author       : Ravi
 * Date         : 05-May-2016
 */

package com.restJersy.first;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EventPollServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        for(int inc = 0; inc < 10; inc++){
            if (ClientFirstImpl.notifyRestore.contains(request.getRemoteAddr())) {
                writer.write("data:"+ "LINK_RESTORED" +"\n\n");
                writer.flush();
                ClientFirstImpl.notifyRestore.remove(request.getRemoteAddr());
                ClientFirstImpl.notifyDown.remove(request.getRemoteAddr());
            }
            if (ClientFirstImpl.notifyDown.contains(request.getRemoteAddr())) {
                writer.write("data:"+ "LINK_DOWN" +"\n\n");
                writer.flush();
                ClientFirstImpl.notifyDown.remove(request.getRemoteAddr());
            }
            if (ClientFirstImpl.congestionAvoided.contains(request.getRemoteAddr())) {
                writer.write("data:"+ "CONGESTION_AVOIDED" +"\n\n");
                writer.flush();
                ClientFirstImpl.congestionAvoided.remove(request.getRemoteAddr());
                ClientFirstImpl.congestionOccured.remove(request.getRemoteAddr());
            }
            if (ClientFirstImpl.congestionOccured.contains(request.getRemoteAddr())) {
                writer.write("data:"+ "CONGESTION_OCCURED" +"\n\n");
                writer.flush();
                ClientFirstImpl.congestionOccured.remove(request.getRemoteAddr());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(inc == 9) {
                inc = 0;
            }
        }
        writer.close();
    }
}