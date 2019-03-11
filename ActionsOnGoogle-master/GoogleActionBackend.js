"use strict";

const {
  dialogflow,
  BasicCard,
  Permission,
  Suggestions
} = require("actions-on-google");

const admin = require("firebase-admin");

//Calling sms-one lambda in this lamda funtion
var AWS = require('aws-sdk');
AWS.config.region = 'us-east-1';
var lambda = new AWS.Lambda();

// Instantiate the Dialogflow client.
const app = dialogflow({ debug: true });

var serviceAccount = require("./acc.json");
var firebase = admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://actionson-4001e.firebaseio.com"
  });

// Handle the Dialogflow intent named 'Default Welcome Intent'.
app.intent("Default Welcome Intent", conv => {
  // conv.user.storage = {};
  const name = conv.user.storage.userName;
  if (!name) {
    // Asks the user's permission to know their name, for personalization.
    conv.ask(
      new Permission({
        context: "Hi there,Let's get connected,Register with us. To get to know you better",
        permissions: ['NAME', 'DEVICE_PRECISE_LOCATION']
      })
    );
  } else {
    conv.ask(`Hi, ${name}. How can I assist you?`);
  }
});

// Handle the Dialogflow intent named 'actions_intent_PERMISSION'. If user
// agreed to PERMISSION prompt, then boolean value 'permissionGranted' is true.
app.intent("actions_intent_PERMISSION", (conv, params, permissionGranted) => {
  if (!permissionGranted) {
    // If the user denied our request, go ahead with the conversation.
    conv.ask(`OK, no worries. What is you name?`);
    // conv.ask(new Suggestions("Blue", "Red", "Green"));
  } else {
    // If the user accepted our request, store their name in
    // the 'conv.user.storage' object for the duration of the conversation.
    conv.user.storage.userName = conv.user.name.display;
    var location = conv.device.location.coordinates;
    conv.user.storage.location = location;
    conv.ask(
      `Thanks, ${conv.user.storage.userName}. Provide emergency contact number.`
    );
    console.log("location is: "+location);
  }
});

app.intent("ContactNameIntent",(conv,{number}) => {
  return new Promise( function( resolve, reject ){
    //retain number in persistance storage
    conv.user.storage.contact_number = number;
    var data = {
    emergency_contact:{
      num1:number
    },
    is_emergency:false
  };
  
  firebase.database().ref("users").child(conv.user.storage.userName).set(data)
  .then(()=>{
      console.log("sucess");
        firebase.delete();
        conv.ask("We are connected. How can I assist you?")
        resolve();
    }).catch(function (error) {
          console.log('Firebase error: ', error);
          firebase.delete();
          conv.ask("Sorry for inconvinience. There is some network issue. Please try again.")
          reject();
    });
  })
});

app.intent("SendSOSIntent",conv => {
 return new Promise(function(resolve,reject){
    var map_query = "https://maps.google.com/?q="+conv.user.storage.location.latitude+","+conv.user.storage.location.longitude;
    //var num = "+91"+conv.user.storage.contact_number;
    var params = {
    FunctionName: 'sms-one', // the lambda function we are going to invoke
    InvocationType: 'RequestResponse',
    LogType: 'Tail',
    Payload: `{ "message" : "${map_query}"}`
  };
  lambda.invoke(params, function(err, data) {
    if (err) {
      // context.fail(err);
      console.log("error occured",err);
      reject();
    } else {
      // context.succeed('Lambda_B said '+ data.Payload);
      console.log("Success");
      conv.ask("Your request has be processed. Help is on the way.");
      resolve();
    }
  })
 })
})

app.intent("actions_intent_NO_INPUT", conv => {
  // Use the number of reprompts to vary response
  const repromptCount = parseInt(conv.arguments.get("REPROMPT_COUNT"));
  if (repromptCount === 0) {
    conv.ask("Which color would you like to hear about?");
  } else if (repromptCount === 1) {
    conv.ask(`Please say the name of a color.`);
  } else if (conv.arguments.get("IS_FINAL_REPROMPT")) {
    conv.close(
      `Sorry we're having trouble. Let's ` + `try this again later. Goodbye.`
    );
  }
});



// Set the DialogflowApp object to handle the HTTPS POST request.
exports.dialogflowFirebaseFulfillment = app;
