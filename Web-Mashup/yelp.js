
var map;
var geocoder;
var markers;
var southwestlat;
var southwestlng;
var northeastlat;
var northeastlng;
var marker;
var markers=[];
var myMarker=[];
function initialize () 
{
  geocoder=new google.maps.Geocoder();
  var myCenter= new google.maps.LatLng(32.75,-97.13);
  var mapOptions = {
    zoom: 14,
    center: myCenter,
  };
  map = new google.maps.Map(document.getElementById("output"),
      mapOptions);
    
  if(navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
    var MyPosition = new google.maps.LatLng(position.coords.latitude,
                                       position.coords.longitude);
    map.setCenter(MyPosition);
    geocoder.geocode({'latLng': MyPosition}, function(results, status) {
      if (status == google.maps.GeocoderStatus.OK) {
        if(results[0]) {
          add = results[0].formatted_address;
          marker = new google.maps.Marker({
              map: map,
              position:MyPosition,
              title: add,
              icon: 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=Me|FF0000|000000'
          });
        myMarker.push(marker);
        }
      }
    });
    },function() {
      handleNoGeolocation(true);
    });
  } 
  else {
    // Browser doesn't support Geolocation
    handleNoGeolocation(false);
  }
  marker=new google.maps.Marker({
  });
  marker.setMap(map);
}



function sendRequest ()
{
    //Extracting the map bounds -Southwest and Northeast
    var bounds= map.getBounds();
    northeastlat = bounds.getNorthEast().lat(); 
    southwestlat = bounds.getSouthWest().lat();
    northeastlng = bounds.getNorthEast().lng();
    southwestlng = bounds.getSouthWest().lng();
    var xhr = new XMLHttpRequest();
    var query = encodeURI(document.getElementById("search").value);
    xhr.open("GET", "proxy.php?term="+query+"&bounds="+southwestlat+","+southwestlng+"|"+northeastlat+","+northeastlng+"&limit=10");
    xhr.setRequestHeader("Accept","application/json");
    xhr.onreadystatechange = function () {
       if (this.readyState == 4) {
         // getting the address from the json response
          var json = JSON.parse(this.responseText); 
          var str = JSON.stringify(json,undefined,2);
          var listing="";
          var address="";
          var name="";
          var add="";
          var city= "";
          var state="";
          var postal="";
          var country="";
          if( json.businesses.length!=0){
          for(var i=0;i<json.businesses.length;i++)
           {
             name= json.businesses[i].name;
             var image=json.businesses[i].image_url;
             var url=json.businesses[i].url;
             var rating=json.businesses[i].rating_img_url;
             add=json.businesses[i]["location"].address;
             city=json.businesses[i]["location"].city;
             state=json.businesses[i]["location"].state_code;
             postal=json.businesses[i]["location"].postal_code;
             country=json.businesses[i]["location"].country_code;
             address=add+","+city+","+state+","+postal;
             codeAddress(address,name,i);
             var snippet=json.businesses[i].snippet_text;
             var index=i+1;
               
             listing+= "<table style='border: 1px solid black;border-collapse: collapse;'><tr class='restaurant'><td><b>"+index+".</b></td><td><img style='float:left; margin-top:10px; width:100px;' src="+image+" alt='IMAGE NOT FOUND' /></td><td><div><br/><b>Name: </b><a href="+url+">"+name+"</a><br><b>Rating: </b><img src="+rating+" /><br/><b>Customer Review: </b>"+snippet+"</div></td></tr></table>";
          }  
          document.getElementById("displays").innerHTML =listing;
          document.getElementById("displays").style.display="block";
          //$(".restaurant").css()
         deleteMarkers();
         
       }}
    else if(json.businesses.length==0){
         document.getElementById("displays").style.display="block";
         deleteMarkers();
        }
   };
   xhr.send(null);
}

//setting the markers on the map
function codeAddress(address,name,i) 
{
    i++;
    geocoder.geocode( {'address': address}, function(results, status)
    {
     if (status == google.maps.GeocoderStatus.OK) 
      {
          marker = new google.maps.Marker({ 
	  map: map,
          title: name+"|"+address,
          position: results[0].geometry.location,
          icon:'http://chart.googleapis.com/chart?chst=d_map_pin_letter&chld='+ i +'|FF0000|000000',
          });
          markers.push(marker);
     } 
    else
     {
     }
   });
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
  setAllMap(null);
}


// Sets the map on all markers in the array.
function setAllMap(map) {
  for (var i = 0; i < markers.length; i++) {
    markers[i].setMap(map);
  }
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
  clearMarkers();
  markers = [];
}

//trigger "find" on click of keyboard enter
$(document).keypress(function(e) {
    if(e.which == 13){
        document.getElementById("find").click();
        e.preventDefault();
    }
});