
// send push notification to nearby users
Parse.Cloud.afterSave('Event', function (request) {

  Parse.Cloud.useMasterKey();

  
  var eventUser = request.object.get('username');
  var currentUser = request.user.get('username');

  console.log(eventUser == currentUser);

  var userQuery = new Parse.Query('User');
  userQuery.withinKilometers('userLocation', request.object.get('location'), 3.5);
  

  var query = new Parse.Query(Parse.Installation);
  query.matchesQuery("user", userQuery);



Parse.Push.send({
  where: query, // Set installation query
  data: {
      alert: request.object.get('username') + " aights within " + request.object.get('') 
    }
  }, 
  {
   success: function() {
   // Push was successful
   },
   error: function(error) {
   // Handle error
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