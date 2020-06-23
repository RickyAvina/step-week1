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

    // locate gallery div
    const imgRef = document.getElementById('galleryImage');
    var images = [];
    preload(images, 5);

    // cycle through photos
    displayPhoto(imgRef, images, 0);
});

document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();

        document.querySelector(this.getAttribute('href')).scrollIntoView({
            behavior: 'smooth'
        });
    });
});

function displayPhoto(img, images, index) {
    if (index > 4) {
        index = 0;
    } 

    img.src = images[index].src;
    setTimeout(displayPhoto, 3000, img, images, index+1);
}


function preload(images, numImages) {
    for (var i = 0; i < numImages; i++) {
        images[i] = new Image();
        images[i].src = "images/soccer/" + i.toString() + ".jpg";
    }
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

