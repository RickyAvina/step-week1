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
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.*;
import com.google.gson.Gson;


@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
  
  // Delete a post with a given ID and update `/home`
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String id = request.getParameter("comment");
    System.out.println("Button val: " + id);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      long comment_id = (long) entity.getProperty("comment_id");

      if (Long.toString(comment_id).equals(id)) {
          datastore.delete(entity.getKey());
      }
    }

    response.sendRedirect("/home");
  }

}