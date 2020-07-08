// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.text.Format;
import java.util.Date;
import com.google.gson.Gson;
import java.util.List;


@WebServlet("/home")
public class HomeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    UserService userService = UserServiceFactory.getUserService();

    if (userService.isUserLoggedIn()) {
        String nickname = getUserNickname(userService.getCurrentUser().getUserId());
        if (nickname == null) {
            response.sendRedirect("/nickname");
            return;
        }

        // User is logged in and has a nickname, so the request can proceed
        String logoutUrl = userService.createLogoutURL("/home");
        out.println("<h1>Hello " + nickname + "!</h1>");
        out.println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
        out.println("<p>Change your nickname <a href=\"/nickname\">here</a>.</p>");

        // comment form
        out.println("<p>Post a comment!</p>");
        out.println("<form method=\"POST\" action=\"/home\">");
        out.println("<textarea name=\"comment\"></textarea>");
        out.println("<br/>");
        out.println("<button>Submit</button>");
        out.println("</form>");
    } else {
        String loginUrl = userService.createLoginURL("/home");
        out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }

    // comment thread, visible by all
    out.println("<ul>");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      Key key_id = entity.getKey();
      String comment_id = Long.toString((long) entity.getProperty("comment_id"));
      String user_id = (String) entity.getProperty("user_id");

      String nickname = getUserNickname(user_id);
      out.println("<li><span style='color:blue;'>" + convertTime(timestamp) + 
                  "</span> <span style='color:green'>" + nickname + "</span>: " + 
                  comment + "<form method='POST' action='/delete'>" +
                                "<input type='submit' name='deleteBtn' value='Delete' />" +
                                "<input type='hidden' name='comment' value='" + comment_id + "'/>" +
                            "</form> </li>");
    } 
    out.println("</ul>");
    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // only logged in users can post comments
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/nickname");
      return;
    }

    // If user has not set a nickname, redirect to nickname page
    String nickname = getUserNickname(userService.getCurrentUser().getUserId());
    if (nickname == null) {
      response.sendRedirect("/nickname");
      return;
    }

    // Grab user and form data
    String id = userService.getCurrentUser().getUserId();
    String comment = request.getParameter("comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity messageEntity = new Entity("Comment");

    long comment_id = (long)(Math.random() * 100000000);
    messageEntity.setProperty("comment_id", comment_id);
    messageEntity.setProperty("user_id", id);
    messageEntity.setProperty("comment", comment);
    messageEntity.setProperty("timestamp", System.currentTimeMillis());

    datastore.put(messageEntity);
    response.sendRedirect("/home");
  }

  /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }

  /** Convert the time from an epoch long to formatted String **/
  private String convertTime(long time){
    Date date = new Date(time);
    Format format = new SimpleDateFormat("EEE dd/MM HH:mm");
    return format.format(date).toString();
  }

  /** Return a JSON string representing the comments data structure **/
  private String getCommentsJson(){
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    List<Entity> comments = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

}

