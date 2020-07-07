package com.google.sps.servlets;

import com.google.sps.data.Comments;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import com.google.appengine.api.datastore.PreparedQuery;
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

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
  
  // Delete a post with a given ID and update `/home`
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = request.getParameter("comment");
    System.out.println("Button val: " + id);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Task").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    datastore.delete(entity.getKey());
    
    response.sendRedirect("/home");
  }

}