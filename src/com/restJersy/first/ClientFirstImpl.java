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
 * ClientFirstImpl.java
 * Author : Bivas,Balaji.A
 * Date   : 16-Oct-2015
 **/

package com.restJersy.first;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class contains the Rest methods to call remote web services to retrieve the video servers.
 */

@Path("/sdncoe")
public class ClientFirstImpl {

    public static ArrayList<String> notifyDown = new ArrayList<>();
    public static ArrayList<String> notifyRestore = new ArrayList<>();
    public static ArrayList<String> congestionOccured = new ArrayList<>();
    public static ArrayList<String> congestionAvoided = new ArrayList<>();

    private static final String LINK_DOWN = "LINK_DOWN";
    private static final String LINK_RESTORED = "LINK_RESTORED";
    private static final String CONGESTION_OCCURED = "CONGESTION_OCCURED";
    private static final String CONGESTION_AVOIDED = "CONGESTION_AVOIDED";

    @Path("/test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayXMLHello(@Context HttpServletRequest request) {
        HttpSession mySess = request.getSession();
        String sessionId = mySess.getId();
        System.out.println("Session ID: " + sessionId);
        System.out.println("ClientFirstImpl.sayXMLHello"+ mySess.getAttribute("clientIp"));
        return "Hi welcome";
    }

    @POST
    @Path("/postChannelDetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postChannelDetails(@Context HttpServletRequest request, JSONObject inputJsonObj) {
        String finalVideoUrl;
        try {
            String clientIP = request.getRemoteAddr();
            System.out.println();
            HttpSession mySess = request.getSession();
            String sessionId = mySess.getId();
            System.out.println("Session ID: " + sessionId + "client ip : " + clientIP);
            String remoteServiceURL = inputJsonObj.getString("webServiceURL");
            inputJsonObj.put("clientIP", clientIP);
            inputJsonObj.put("queueID", "1");
            inputJsonObj.put("sessionId", sessionId);
            String url = remoteServiceURL;

            Client client = Client.create();
            WebResource webResource = client.resource(url);
            ClientResponse response = webResource.accept("application/json")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .post(ClientResponse.class, inputJsonObj);
            if (response.getStatus() != 200) {
                throw new RuntimeException();
            }
            String RESToutput = response.getEntity(String.class);
            System.out.println("REST output of " + url + " is: " + RESToutput);

            JSONObject jObj = new JSONObject(RESToutput);
            String videoServer = jObj.getString("chnlServer");
            System.out.println("videoServer: " + videoServer);

            if (videoServer.startsWith("http")) {
                finalVideoUrl = videoServer;
            } else {
                finalVideoUrl = "Please check the Remote Webservice";
            }

        } catch (RuntimeException e1) {
            System.out.println("Exception occurred in remote webservice: " + e1.toString());
            finalVideoUrl = "Please check the Remote Webservice";
        } catch (Exception e) {
            System.out.println("Exception occurred in webservice: " + e.toString());
            finalVideoUrl = "Please check the Remote Webservice";
        }
        return finalVideoUrl;
    }

    @POST
    @Path("/getSdnVideo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getSdnVideo(@Context HttpServletRequest request, JSONObject chnl) throws JSONException {
        System.out.println("ClientFirstImpl.getSdnVideo"+ chnl.toString());
        String result;
        try {
                String clientIP = request.getRemoteAddr();
                System.out.println("client ip : " + clientIP);
                String sessionId = request.getSession().getId();
                HttpSession sesId = request.getSession();
                String chName = chnl.getString("chnlName");
                System.out.println("chnlName: " + chName);
                System.out.println("ClientFirstImpl.getSdnVideo"+ sesId.getAttribute("clientIp"));
                String remoteServiceURL = chnl.getString("webServiceURL");
                JSONArray servArray = new JSONArray();
                String serv = (String) chnl.get("serverURL");
                servArray.put(serv);
                chnl.put("serverURL", servArray);
                chnl.put("clientIP", clientIP);
                chnl.put("queueID", "0");
                chnl.put("sessionId", sessionId);
                String url = remoteServiceURL;

                Client client = Client.create();
                WebResource webResource = client.resource(url);
                ClientResponse response = webResource.accept("application/json")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .post(ClientResponse.class, chnl);

                if (response.getStatus() != 200) {
                    throw new RuntimeException();
                }
            String RESToutput = response.getEntity(String.class);
                System.out.println("REST output of " + url + " is: " + RESToutput);
                result = "success";
        } catch (RuntimeException e1) {
            System.out.println("Exception occurred in remote webservice: " + e1.toString());
            result = "failure";
        } catch (Exception e) {
            System.out.println("Exception occurred in webservice: " + e.toString());
            result = "failure";
        }
        return result;
    }

    @POST
    @Path("/deleteSession")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteSession(@Context HttpServletRequest request, JSONObject inputObj) throws JSONException {
        System.out.println("ClientFirstImpl.deleteSession"+ inputObj.toString());
        String result;
        HttpSession session = request.getSession();
        try {
            String clientIP = request.getRemoteAddr();
            System.out.println("client ip : " + clientIP);
            String sessionId = request.getSession().getId();
            HttpSession sesId = request.getSession();
            String remoteServiceURL = inputObj.getString("remoteWebServiceURLDeleteIntent");
            inputObj.put("clientIP", clientIP);
            inputObj.put("sessionId", sessionId);
            String url = remoteServiceURL;
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            ClientResponse response = webResource.accept("application/json")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .post(ClientResponse.class, inputObj);
            System.out.println("RESPONSE: " + response.toString());
            if (response.getStatus() != 200) {
                throw new RuntimeException();
            }
            String RESToutput = response.getEntity(String.class);
            System.out.println("REST output of " + url + " is: " + RESToutput);
            session.invalidate();
            result = "success";
        } catch (RuntimeException e1) {
            System.out.println("Exception occurred in remote webservice: " + e1.toString());
            result = "failure";
        } catch (Exception e) {
            System.out.println("Exception occurred in webservice: " + e.toString());
            result = "failure";
        }

        return result;
    }

    @POST
    @Path("/notifyEvent")
    @Consumes(MediaType.TEXT_PLAIN)
    public synchronized String notifyEvent(String inputStr) throws JSONException {
        JSONObject jObj = new JSONObject(inputStr);
        String ip = jObj.get("clientIP").toString();
        String notification = jObj.get("event").toString().trim();
        System.out.println("CAPTURED EVENT IS : " + notification);
        switch (notification) {
            case LINK_DOWN:
                notifyDown.add(ip);
                break;
            case LINK_RESTORED:
                notifyRestore.add(ip);
                break;
            case CONGESTION_OCCURED:
                congestionOccured.add(ip);
                break;
            case CONGESTION_AVOIDED:
                congestionAvoided.add(ip);
                break;
        }
        return "success";
    }

    @GET
    @Path("/getChannelList")
    public String postChannelServerProperties(@Context HttpServletRequest request) throws IOException {
        StringBuilder responseStrBuilder = new StringBuilder();
        try {
            InputStream inputStream =  request.getServletContext().getResourceAsStream("/resources/ChannelServer.properties");
            if (inputStream != null) {
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String inputStr = "";
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
            }
            return responseStrBuilder.toString();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Property file is not found");
        }
        catch(IOException ex) {
            System.out.println("Error while reading property file , input is null"+ex.toString());
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}