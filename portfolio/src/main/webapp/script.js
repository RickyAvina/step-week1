// Copyright 2020 Google LLC
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

/**
 * Set up code to run when body loads
 */
function onLoad() {
    getLinkToCreatorsPage();
    getGreetings();
}

/**
 * Fetch and display the link to the page pf the creator of the home page's 
 * background for credit.  
*/
function getLinkToCreatorsPage() {
    fetch('/bg-creator').then(response => response.text()).then((link) => {
        document.getElementById("linkRef").innerHTML = link;
    });
}

/* Fetch DataServerlets JSON response representing greetings in many languages */
function getGreetings() {
    console.log("getGreetings() activated");

    fetch('/data').then(response => response.json()).then((json) => {
        console.log(json);
    });
}

/**
 * Fetches all the comments and displays them on UI.
 */
function loadComments() {
    fetch('/data').then(response => response.json()).then((commentsJSON) => {

        const commentsEl = document.getElementById("comments");
        const ul = document.createElement('ul');
        commentsEl.appendChild(ul);

        for (const messageObj of commentsJSON.comments) {
            const date = messageObj.date;
            const comment = messageObj.comment;

            var li = document.createElement('li');
            li.innerHTML = "<span style='color: blue;'>" + date + "</span> " +
                            comment + " <a onclick=deleteComment(";
            ul.appendChild(li);
        }
    });
}