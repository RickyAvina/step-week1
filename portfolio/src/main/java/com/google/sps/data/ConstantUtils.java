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

/** All of the constants used by the servlets **/
public final class ConstantUtils {

    public static final String CODE_ORIGIN_STRING = ".*javascript:.*|.*//.*|.*<.*|.*>.*";

    /** Constants to be used by Comments Table in datastore **/
    public static class CommentTable {
        public static final String TABLE_TYPE = "Comment";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_COMMENT_ID = "comment_id";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_SENTIMENT = "sentiment";
    }

    /** Constants to be used by Users Table in datastore **/
    public static class UserTable {
        public static final String TABLE_TYPE = "UserInfo";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NICKNAME = "nickname";

    }
    
}