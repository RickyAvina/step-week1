var access_token = ""

const params = {        
    client_id: "885120077899-842us5gd8dr5784ik6nm8psvddbgn52k.apps.googleusercontent.com",
    scope: 'https://www.googleapis.com/auth/youtube.force-ssl'
}

function init() {
  gapi.load('auth2');
}

function auth() {
    gapi.auth2.authorize(params, function(response) {
    if (response.error) {
        // An error happened!
        return;
    }
    // The user authorized the application for the scopes requested.
    accessToken = response.access_token;
    var idToken = response.id_token;

    // You can also now use gapi.client to perform authenticated requests.
    console.log(response);
    console.log("accessToken: " + accessToken);

    // call youtube APIs 
    });
}

function getCaptions() {
    // supply access token
    var xmlHttp = new XMLHttpRequest();
    const api_key = "AIzaSyC3t5YIbra1b5dvhasGReYDZWQZ2LRaUbU";
    const video_id = "XPUuF_dECVI";
    const url = "https://www.googleapis.com/youtube/v3/captions/" + video_id + "?key=" + api_key + "HTTP/1.1";

    xmlHttp.open("GET", url);
    xmlHttp.setRequestHeader("Bearer", accessToken);
    xmlHttp.send(null);

    console.log(xmlHttp.responseText);
    // if (xmlHttp.responseText != null) {
    //     console.log()
    // }
    // return xmlHttp.responseText;

}
