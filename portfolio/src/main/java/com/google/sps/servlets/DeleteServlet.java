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

      Object comment_id_obj = entity.getProperty("comment_id");
      if (comment_id_obj == null) {
          System.err.println("comment_id of comment is NULL");
          response.sendRedirect("/home");
          return;
      }

      long comment_id = (long) comment_id_obj;

      if (Long.toString(comment_id).equals(id)) {
          datastore.delete(entity.getKey());
      }
    }

    response.sendRedirect("/home");
  }

}