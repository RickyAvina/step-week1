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

$(document).ready(function() {
    console.log("ready");
    // locate gallery div
    const pictureDiv = document.getElementById('gallery');

    // cycle through photos
    displayPhoto(pictureDiv, 0);
    // const numImages = 5;
    // var i = 0;
    // setInterval(displayPhoto, 1000, pictureDiv, ++i)

    // for (var i=0; i < numImages; i++) {
    //     setInterval(displayPhoto, 1000, pictureDiv, i);
    // }
});

function displayPhoto(div, index) {
    if (index > 4) {
        index = 0;
    } 

    console.log("index: " + index.toString());
    div.innerHTML = "<img src='images/soccer/" + index.toString() + ".jpg'/>";

    setTimeout(displayPhoto, 3000, div, index+1);
}

function getRandomImage() {
    // Pick random image name
    const numImages = 5;
    const imageName = "images/utopia/" + Math.floor(Math.random()*numImages).toString() + ".jpg";

    // Add it to page
    const pictureContainer = document.getElementById('picture-container');
    pictureContainer.innerHTML = '<img src="'+ imageName +'" />';

    // Add commentary on images
    pictureContainer.innerHTML += "</br>" + "<p>These were all painted by \
                <a href='https://www.mccallstudios.com/'>Robert McCall<a>, one of my favorite artists.</p>"
}


