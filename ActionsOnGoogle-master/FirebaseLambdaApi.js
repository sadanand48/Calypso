var Firebase = require('firebase');

var addNewUser = (n1,n2,is_emer) =>{
    var data = {
        emergency_contact:{
            num1:n1,
            num2:n2
        },
        is_emergency:is_emer
    };
     //code for adding data of new user.     
    Firebase.database().ref().child("users").child("Subojit") 
    .set(data)
    .then(function (data) {
      console.log('Firebase data: ',data);        
      context.succeed();                
    })
    .catch(function (error) {
        console.log('Firebase error: ', error);
        context.fail();
    });
};

var changeIsEmergencyVarTrue = () => {
    //code for chaing value of is_emergency    
    Firebase.database().ref("users/Subojit")
    .child("is_emergency").set(true)
    .then(function(data){
      console.log('Data changed: ',true);
      context.succeed();
    })
    .catch(function(error){
        console.log('Firebase error',error);
        context.fail();
    });
};

var changeIsEmergencyVarFalse = () => {
    //code for chaing value of is_emergency    
    Firebase.database().ref("users/Subojit")
    .child("is_emergency").set(false)
    .then(function(data){
      console.log('Data changed: ',true);
      context.succeed();
    })
    .catch(function(error){
        console.log('Firebase error',error);
        context.fail();
    });
};

var app = (event, context, callback) => {
   var config = { 
        apiKey: "foo-bar-foo",
        authDomain: "actionson-4001e.firebaseapp.com",
        databaseURL: "https://actionson-4001e.firebaseio.com",
        storageBucket: "gs://actionson-4001e.appspot.com"    
   };
    Firebase.initializeApp(config);
};
 
exports.handler = app;
