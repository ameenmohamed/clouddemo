// MDN WebSocket documentation
// https://developer.mozilla.org/en-US/docs/Web/API/WebSocket
//WebSocket URL: wss://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production
// Connection URL: https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/@connections


var wsurl = "wss://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production";
//var wsurl ="wss://ws.amin.guru/production";

//var connurl = "https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/@connections";
var connurl = "https://ws.amin.guru/production/@connections";

socket = new WebSocket(wsurl);
var connId = "defaulrConnId";
isSocketOpen = false;
isConnIdUpdated = false;
var pnrnum;
var lname;

socket.addEventListener("open", (e) => {
  isSocketOpen = true;
  console.log("WebSocket is connected");
  pnrnum = document.getElementById("pnrnumber");
  lname = document.getElementById("lname");
  // sendUserData(pnrnum,lname);
  document.getElementById("connectStatus").innerHTML = "Connected@";
  requestConnID();
  // Start the 3 min ping to keep socket alive
  startInterval();

});

socket.addEventListener("close", (e) => {

  console.log("WebSocket is closed");
  document.getElementById("connectStatus").innerHTML = "Off Line";
  isSocketOpen = false;
});

socket.addEventListener("error", (e) =>
  console.error("WebSocket is in error", e)
);

socket.addEventListener("message", (e) => {
  console.log('<<<< WebSocket received a message:', e);
  processIncomingMessage(e);
});

window.ask = function (msg) {
  const payload = {
    action: "message",
    msg,
  };
  socket.send(JSON.stringify(payload));
};



function callConnect() {
  socket = new WebSocket(wsurl);
}

function callDisconnect() {
  socket.close();
  isConnIdUpdated = false;
  isSocketOpen = false;
  console.log("WebSocket is closed by Client Browser");

}

function requestConnID() {
  const payload = {
    action: "message",
    command: "getconnectionId"
  };
  socket.send(JSON.stringify(payload));
}

function sendUserData(pnrNumber, user) {
  if (!isSocketOpen) { callConnect(); }
  if (socket.readyState === WebSocket.OPEN) {
    const payload = {
      action: "message",
      connectionId: connId,
      username: user,
      pnr: pnrNumber
    };
    socket.send(JSON.stringify(payload));
  }

}

function processIncomingMessage(event) {
  var eventJson;

  try {
    eventJson = JSON.parse(event.data);
    console.log("processIncomingMessage event:" + JSON.stringify(eventJson));
    //{"connectionId":"FsPa6egsjoECG4w=","data":{"reponse":"200","command":"getconnectionId"}} 
    //{"connectionId":"FsQCadtoDoECI7A=","data":{"reponse":"Canadian redress number validated successfuly","serverTime":"15:38:54.931906520","wsmsgcat":"traveldocsupdate","command":"serverevent"}}
    console.log("Command Switch **** :" + eventJson.data.command);

    switch (eventJson.data.command) {
      case "ping":
        console.log("Response to 'ping' command", eventJson.data.reponse);
        // Handle ping command
        break;

      case "getconnectionId":
        console.log("response to getconnectionId command", eventJson.reponse);
        if (eventJson && eventJson.connectionId) {
          console.log("processIncomingMessage connid:" + eventJson.connectionId);
          if (isConnIdUpdated == false) {
            isConnIdUpdated = true;
            connId = eventJson.connectionId;
            document.getElementById("connectStatus").innerHTML = "Connected@" + connId;
          }
        }
        break;
      case "serverevent":
        console.log("Response 'serverevent' command");
        manageWebSocketEventsForUX(eventJson);
        break;

      default:
        console.log("Unknown command");
        // Handle unknown command
        break;
    }

  } catch (error) {
    console.error("Error parsing IncomingMessage event data:", error);
  }




  /*
   //  var eventStr = JSON.stringify(event.data);
     var  eventJson =JSON.parse(event.data);
  //  console.log("processIncomingMessage event:"+eventStr);
    console.log("processIncomingMessage connid:"+eventJson.connectionId);
    connId = eventJson.connectionId;
    document.getElementById("connectStatus").innerHTML = "Connected@"+connId;
    
  */
}

function sendServerMsg() {
  if (!isSocketOpen) { callConnect(); }
  var msg = document.getElementById("talk").value;
  const payload = {
    action: "message",
    command: "talk",
    connectionId: connId,
    msg
  };

  if (socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(payload));
    document.getElementById("talk").value = "";
  } else {
    console.log("WebSocket sendServerMsg attempted but socket not open", payload);
  }
}

function pingWebsocket() {
  console.log(">>>  Function called every 2 minutes");
  if (!isSocketOpen) { callConnect(); }
  const payload = {
    action: "message",
    command: "ping",
    connectionId: connId,
    browserTime: printTime(),
  };

  if (socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(payload));
    console.log("WebSocket ping sent", payload);
  } else {
    console.log("WebSocket ping attempted but socket not open");
  }

}

function startInterval() {
  // Call the function initially - this is done at websocket connect
  // Set the interval to run the function every 2 minutes (180,000 milliseconds)
  setInterval(pingWebsocket, 1 * 60 * 1000);
}



function printTime() {
  // Get the current time
  const currentTime = new Date();

  // Extract the hour, minute, and second from the current time
  const hours = currentTime.getHours();
  const minutes = currentTime.getMinutes();
  const seconds = currentTime.getSeconds();

  // Format the time with leading zeros if necessary
  const formattedTime = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

  // Print the time
  return `Current time: ${formattedTime}`;
}