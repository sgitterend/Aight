
// send push notification to nearby users
Parse.Cloud.afterSave('Event', function (request) {

  Parse.Cloud.useMasterKey();

  var ninetyMinFromNow = new Date() + 5400;
  var now = new Date();


  var userQuery = new Parse.Query('User');
  userQuery.withinKilometers('userLocation', request.object.get('location'), 3.5);
  

  var query = new Parse.Query(Parse.Installation);
  query.matchesQuery("user", userQuery);

  var eventUser = request.object.get('username');
  var currentUser = request.user.get('username');

  console.log(ninetyMinFromNow);
  console.log(now);
Parse.Push.send({
  where: query,
  // expiration_interval: ninetyMinFromNow,
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