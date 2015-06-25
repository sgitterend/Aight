
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

  console.log(eventUser);
  console.log(currentUser);

  Parse.Push.send({
    where: query,
    expiration_time: ninetyMinFromNow,
    data: {
        title: request.object.get('username'),
        alert: request.object.get('description') 
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
  // query.find({
  // success: function (installations) {
  // console.log('installations in range:');
  // console.log(installations);
  // }, error: function (err) {
  // console.log('err');
  // console.log(err);
  // }
  //   });
  });