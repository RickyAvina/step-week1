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
import java.time.LocalDateTime;

/** Servlet that returns writes greetings in many languages in JSON format */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private Comments comments = new Comments();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("doGet() activated!");
    response.setContentType("text/html;");

    // build up JSON response
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"messages\": ");
    sb.append("[");

    for (String greeting: getGreetings()) {
        sb.append("\"");
        sb.append(greeting);
        sb.append("\", ");
    }

    sb.setLength(sb.length() - 2);
    sb.append("]");
    sb.append("}");

    System.out.println(sb.toString());
    response.getWriter().println(sb.toString());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("application/json");

      // get input from the form
      String comment = request.getParameter("comment");
      LocalDateTime now = LocalDateTime.now();
      comments.addComment(now, comment);

      // convert to JSON
      String json = comments.json();
    
      response.setContentType("text/html;");
      response.getWriter().println("<p>JSON\n: " + json + "</p>");
    
    //   System.out.println(json);

      // redirect
  }

  /* Get hello world greetings in many different languages */
  public List<String> getGreetings(){
      return Arrays.asList("Hello world!", "Hola mundo!", "Bonjour le monde!", "Moien Welt!");
  }
}