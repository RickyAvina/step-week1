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

import com.google.sps.data.ConstantUtils;
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
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    Query query = new Query(ConstantUtils.CommentTable.TABLE_TYPE).addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      long timestamp = (long) entity.getProperty(ConstantUtils.CommentTable.COLUMN_TIMESTAMP);
      Key key_id = entity.getKey();
      String comment_id = Long.toString((long) entity.getProperty(ConstantUtils.CommentTable.COLUMN_COMMENT_ID));
      String user_id = (String) entity.getProperty(ConstantUtils.CommentTable.COLUMN_USER_ID);
      double sentiment = (double) entity.getProperty(ConstantUtils.CommentTable.COLUMN_SENTIMENT);
      String sentimentHex = getColor(sentiment);
      String nickname = getUserNickname(user_id);

      // prevent script injection
      String comment = (String) entity.getProperty(ConstantUtils.CommentTable.COLUMN_COMMENT);
      if (checkValidate(comment)){
          System.err.println("Dangerous characters detected");
          continue;
      }

      out.println("<li>" +
                    "<span style='color:blue;'>" + convertTime(timestamp) + "</span> " +
                    "<span style='color: black;'>" + nickname + "</span>: " +
                    "<span style='color: " + sentimentHex + ";'>" + comment + "</span>" +
                    "<form method='POST' action='/delete'>"+
                        "<input type='submit' name='deleteBtn' value='Delete'/>" +
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
    String user_id = userService.getCurrentUser().getUserId();
    String comment = request.getParameter(ConstantUtils.CommentTable.COLUMN_COMMENT);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity messageEntity = new Entity(ConstantUtils.CommentTable.TABLE_TYPE);

    long comment_id = (long)(Math.random() * 100000000);
    messageEntity.setProperty(ConstantUtils.CommentTable.COLUMN_COMMENT_ID, comment_id);
    messageEntity.setProperty(ConstantUtils.CommentTable.COLUMN_USER_ID, user_id);
    messageEntity.setProperty(ConstantUtils.CommentTable.COLUMN_COMMENT, comment);
    messageEntity.setProperty(ConstantUtils.CommentTable.COLUMN_TIMESTAMP, System.currentTimeMillis());

    // Sentiment analysis
    Document doc = Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
    messageEntity.setProperty(ConstantUtils.CommentTable.COLUMN_SENTIMENT, score);

    datastore.put(messageEntity);

    response.sendRedirect("/home");
  }

  /**
   * Check whether a given string contains javascript or dangerous code.
   * @param str - a string to validate
   * @return - true is the input `str` contains dangerous characters
   */
  private static boolean checkValidate(String str) {
        if (str != null) {
            String patternString = ConstantUtils.CODE_ORIGIN_STRING;
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }
        return false;
    }

  /**
   * Convert a sentiment to a color
   * @param sentiment - a double in the range [-1, 1] representing
   *                    the sentiment of a given piece of text
   * @return - a hexidecimal color String 
   */
  private String getColor(double sentiment) {

      // convert sentiment to a color value in range [0,255]
      double adjustedVal = (sentiment+1)*255/2.0;  
      double red = 255.0 - adjustedVal;
      double green = adjustedVal;

      Color color = new Color((int)red, (int)green, 0);

      StringBuilder hex = new StringBuilder(Integer.toHexString(color.getRGB() & 0xffffff));
      if (hex.length() < 6) {
        hex.insert(0, "0");
      }
      
      hex.insert(0, "#");
      return hex.toString();
  }

  /** Returns the nickname of the user with id, or null if the user has not set a nickname. */
  private String getUserNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(ConstantUtils.UserTable.TABLE_TYPE).setFilter(new Query.FilterPredicate(ConstantUtils.UserTable.COLUMN_ID, Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String nickname = (String) entity.getProperty(ConstantUtils.UserTable.COLUMN_NICKNAME);
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
    Query query = new Query(ConstantUtils.CommentTable.TABLE_TYPE).addSort(ConstantUtils.CommentTable.COLUMN_TIMESTAMP, SortDirection.DESCENDING);
    List<Entity> comments = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(10));
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

}

