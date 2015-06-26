// send push notification to nearby users
Parse.Cloud.afterSave('Event', function (request) {

  Parse.Cloud.useMasterKey();

  // set expiration for notification
  var ninetyMinFromNow = new Date();
  ninetyMinFromNow.setMinutes(ninetyMinFromNow.getMinutes() + 90);


  // only notify if event is within 3.5 kilometers
  var userQuery = new Parse.Query('User');
  userQuery.withinKilometers('userLocation', request.object.get('location'), 3.5);
  
  // query 
  var query = new Parse.Query(Parse.Installation);
  query.matchesQuery("user", userQuery);


  // don't send notification to creator
  var eventUser = request.object;
  var currentUser = request.user;

  var loc = new Parse.GeoPoint(request.object.get('location'));
  // 
  // loc = $.parseJSON(loc);
  // 
  // var userLatitude = loc.get ("latitude");
  // var userLongitude = loc.get ("longitude");

  console.log(eventUser);
  console.log(currentUser);
  console.log(loc);


  // var aightLatitude = 53.3835462;
  // var aightLongitude = 4.8021944;

  // var userLatitude = 52.3835462;
  // var userLongitude = 4.8021944;

  // console.log(aightLongitude);
  // console.log(aightLatitude);
  // console.log(userLatitude);
  // console.log(userLongitude);

  //distance = measure(userLatitude, userLongitude, aightLatitude, aightLongitude);

  // console.log(distance);

  Parse.Push.send({
    where: query,
    expiration_time: ninetyMinFromNow,
    data: {
        title: request.object.get('username') + " just created a new Aight",
        alert: "within 50 meters"
      }
    }, 
  {
   success: function() {
   // Push was successful
   },
   error: function(error) {
      throw "Error " + error.code + " : " + error.message;
   }
  });

// calculate meters from latitude and longitude
// function measure(lat1, lon1, lat2, lon2){  // generally used geo measurement function
//     var R = 6378.137; // Radius of earth in KM
//     var dLat = (lat2 - lat1) * Math.PI / 180;
//     var dLon = (lon2 - lon1) * Math.PI / 180;
//     var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//     Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
//     Math.sin(dLon/2) * Math.sin(dLon/2);
//     var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//     var d = R * c;
//     console.log(d);
//     return d * 1000; // meters
// }

});

