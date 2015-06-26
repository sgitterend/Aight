
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


  var eventLat = 52.3735462;
  var eventLong = 4.8021944;

  // calculate distance
  var latDifference = Math.pow(userLat - eventLong, 2);
  var longDifference = Math.pow(userLat - eventLong, 2);

  // round distance off to 10 meters
  var distance = parseInt((Math.pow((latDifference + longDifference), 0.5) + 10) / 10) * 10;

  var loc = new Parse.GeoPoint(request.object.get('location'));
  loc = $.parseJSON(loc);

  var userLat = loc.get ("latitude");
  var userLong = loc.get ("longitude");

  console.log(loc.radiansTo(userQuery.get('userLocation')));
  console.log(userQuery.get('userLocation'));
  console.log(eventUser);
  console.log(currentUser);
  console.log(loc);


  Parse.Push.send({
    where: query,
    expiration_time: ninetyMinFromNow,
    data: {
        title: request.object.get('username') + " just created a new AIGHT",
        alert: "within " + distance + " m"
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

});