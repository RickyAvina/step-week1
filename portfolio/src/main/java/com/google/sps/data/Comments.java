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

package com.google.sps.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
// import java.time.format.DateTimeFormatter;

/**
 * Class representing the comments left on our webpage, not threadsafe.
 */
 public class Comments {

    /* Map associating comments and the time they were written. */
    private final Map<Long, String> comments;

    /* The format that dates are reported as */
    // private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Create an empty map of comments.
     */
    public Comments() {
        comments = new HashMap<Long, String>();
    }

    /**
     * Add a comment to our map of comments
    */
    public void addComment(long time, String comment) {
        comments.put(time, comment);
    }

    /**
     * Serialize the comments into JSON format.
     */
    public String json() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"comments\": [");

        for (Map.Entry<Long, String> entry : comments.entrySet()) {
            long time = entry.getKey();            
            sb.append("{ \"date\": \"");
            sb.append(time);
            sb.append("\",");

            sb.append("\"comment\": \"");
            String comment = entry.getValue();
            sb.append(comment);
            sb.append("\"}, ");
        }

        // delete extra characters 
        if (sb.length() > 14)
            sb.setLength(sb.length() - 2);
        sb.append("] }");
        
        return sb.toString();
    }
 }