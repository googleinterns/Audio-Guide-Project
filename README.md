# Audio Guide Projet

### Objective
The probem that we are trying to solve with this project is the lack of personalized and easy-to-follow travel guides that are available online. To address this issue, our main goal is to provide a platform for creating and sharing audio guides with tourists, all for free. 

### Overview
The project enables users, in the status of content-creators, to create and share travel guides in audio format. 
The guides include a photo, a short description and an audio file. 
They are also linked to a place on google maps.
The creator can specify if a guide is private or public.

The travellers can search for guides in the area of their next destination, and they can listen to the audio guides.

The users may have a public portfolio, which includes all the guides created by them and some information about the user(name, a self-introduction and a profile picture).

For Googlers only: [demo](https://screencast.googleplex.com/cast/NTUyMDcxMDk1OTEwNDAwMHxkMjMwOWVhZi02OA)

### Technologies
For developing the project we used the following technologies:

* AppEngine on Google Cloud for testing and deployment
* Java Servlets for back-end
* Google Javascript API for representing geographical point(place guides) on the map and for geolocation
* Google Places API for getting detailed information about places, enable searching for places and for geocoding
* Google Userservice API: for authenticating and identifying users, based on their Google accounts.
* Datastore API: Store all string data, including our parsed file meta data.
* Blobstore API: Blobstore does the parsing and hosting for the uploaded file instead of handing it to Datastore, and provides a key for file access.

### Usage and Deployment
To run the project, you need to install Maven and the Google Cloud Platform SDK.

Additionally, after cloning the project, you will need an [API key for the Google Maps and Places APIs](https://developers.google.com/maps/documentation/javascript/get-api-key).
Include the key in a file `~/step253-2020/src/main/webapp/maps/api_key.js`, with the following content:

```
function setApiKeyAndMapsCallback() {
  document.getElementById('maps-api').src ='https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&callback=initPage&libraries=places';
}
```
To run the project on the server, navigate to the root(step253-2020) and then
- for running a development server, execute the command `mvn package appengine:run`
- to deploy the project on the live server, you will need to set your own project id in pom.xml, and then execute the command `mvn package appengine:deploy`

### Context
This application was developed by [Dennis](https://github.com/Denniswillie) and [Bori](https://github.com/bori00), as a capstone project for Google's STEP internship program, in Summer, 2020.

Our hosts and mentors throughout the development process were [Christian Göllner](https://github.com/cgollner) and [Hung Tran](https://github.com/tlheng).
