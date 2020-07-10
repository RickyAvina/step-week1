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

import com.google.sps.data.Comments;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns writes greetings in many languages in JSON format */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private Comments comments = new Comments();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("doGet() activated!");

    Query query = new Query("Task").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    System.out.println(results.toString());

    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      System.out.println(comment);
      comments.addComment(timestamp, comment);
    }

    // return the json
    String json = comments.stringToJson();
    System.out.println(json);

    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("application/json");

      // get input from the form
      String comment = request.getParameter("comment");
      long now = System.currentTimeMillis();
      comments.addComment(now, comment);

      // convert to JSON
      String json = comments.stringToJson();

      // store data
      Entity taskEntity = new Entity("Task");
      taskEntity.setProperty("comment", comment);
      taskEntity.setProperty("timestamp", now);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
    
      // redirect
      response.sendRedirect("/form.html");
  }

  /* Get hello world greetings in many different languages */
  public List<String> getGreetings(){
      return Arrays.asList("Hello world!", "Hola mundo!", "Bonjour le monde!", "Moien Welt!");
  }
}