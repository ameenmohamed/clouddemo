
var APIGW_URL = "https://api.amin.guru";
var APILOGIN_RES = "/login";
var APITRAVELDOCS_RES = "/traveldocs";
var sfExecreference = "";


function submitTravelDocs() {

    var firstName = document.getElementById("firstName").value;
    var lastName = document.getElementById("lastName").value;
    var email = document.getElementById("email").value;
    var address2 = document.getElementById("address2").value;
    var country = document.getElementById("country").value;
    var state = document.getElementById("state").value;
    var address = document.getElementById("address").value;
    var postcode = document.getElementById("postcode").value;
    var passportnumber = document.getElementById("passportnumber").value;
  
    const payload = {
      apidocs: {
        firstName: firstName,
        lastName: lastName,
        email: email,
        postcode: postcode,
        country: country,
        state: state,
        address: address,
        address2: address2,
        passportnumber: passportnumber,
        connectionId: connId,
      },
    };
  
    fetch(APIGW_URL + APITRAVELDOCS_RES, {
   //   mode: 'no-cors',
      method: "POST",
      body: JSON.stringify(payload),
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      },
    })
      .then((response) => {
        console.log(response.status);
        if (!response.ok) {
              
            throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((json) => {
        console.log(json);
        processTravelDocsResponse(json);
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }
  

  function processTravelDocsResponse(json){
    const testexecutionArnValue = "arn:aws:states:eu-west-1:094312144437:express:SFuncExpress-AddTravelDocs:7e9f51ab-8f87-4eb2-8d74-cf2b4cbe4e0b:14490f86-fbc2-4ddb-a3c7-a88f2d4fe066";
    const excref = json.executionArn;
    const extractedSubstring = excref.split(":").slice(-2).join(":");
    console.log("no parse -execution ref:",extractedSubstring);
    sfExecreference = extractedSubstring; //assign to globale variabvle

    reqStatus = json.status;

    console.log("NO parse respos status:");    console.log(json.status);
   
    if (reqStatus === "SUCCEEDED"){
        console.log("SUCCEEDED");
        //show card travel docs
       var seatConent = loadTemplateWithConent (seatsTemplatePath,getSeatUI);
       document.getElementById("userdataalert").innerHTML = "Request has been submitted <strong>successfully</strong> and will be processed in a few minutes. Request reference: <strong>" + sfExecreference+"</strong>";

     }
  }

function retrievePNR() {
    //show card travel docs
    document.getElementById("cont-apis-info").hidden = false;
    document.getElementById("cont-lbl-pnrname").hidden = false;
    document.getElementById("cont-field-pnrname").hidden = true;
    document.getElementById("aiplaneicon").hidden = true;
    

    var PNR = document.getElementById("pnrNumber").value;
    var lastname = document.getElementById("lastName").value;
    var payload = {
        connectionId: connId,
        pnrNumber: PNR,
        lastName: lastname
    };
    fetch(APIGW_URL + APILOGIN_RES, {
    //    mode: 'no-cors',
        method: "POST",
        body: JSON.stringify(payload),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    })
    .then((response) => {
        console.log(response.status);
        if (!response.ok) {            
            throw new Error("Network response was not ok");
        }
        //console.log(response.text());
        return response.text();
      })
      .then((text) => {
        console.log(text);
        // Do something else with the response
      })
      .catch((error) => {
        console.error("Error:", error);
      });
    document.getElementById("btn-login").hidden = true;
    document.getElementById("btn-signout").hidden = false;

}


function signout() {
    document.getElementById("btn-login").hidden = false;
    document.getElementById("btn-signout").hidden = true;

    document.getElementById("cont-apis-info").hidden = true;
    document.getElementById("cont-lbl-pnrname").hidden = true;
    document.getElementById("cont-field-pnrname").hidden = false;
}

function manageWebSocketEventsForUX(eventJson){
//{"connectionId":"FsQCadtoDoECI7A=","data":{"reponse":"Canadian redress number validated successfuly","serverTime":"15:38:54.931906520","wsmsgcat":"traveldocsupdate","command":"serverevent"}}
    switch (eventJson.data.wsmsgcat) {
      case "traveldocsupdate":
        var respMsg = eventJson.data.response;
        var serverTime = eventJson.data.serverTime;
        document.getElementById("traveldocsData").innerHTML = respMsg;
        document.getElementById("tdTime").innerHTML = serverTime;
        console.log("Handling traveldocsupdate...",serverTime,respMsg);
        break;

      case "healthupdate":
        var respMsg = eventJson.data.response;
        var serverTime = eventJson.data.serverTime;
        document.getElementById("healthupdateData").innerHTML = respMsg;
        document.getElementById("huTime").innerHTML = serverTime;
        console.log("Handling healthupdate...",serverTime,respMsg);
        break;

      case "travelupdate":
        var respMsg = eventJson.data.response;
        var serverTime = eventJson.data.serverTime;
        document.getElementById("travelUpdateData").innerHTML = respMsg;
        document.getElementById("tuTime").innerHTML = serverTime;
        console.log("Handling traveldocsupdate...",serverTime,respMsg);

        break;
      case "flightupdate":
        var respMsg = eventJson.data.response;
        var serverTime = eventJson.data.serverTime;
        var dispStr = "["+serverTime +"] "+respMsg;
   
        document.getElementById("alertsection").innerHTML = dispStr;
        console.log("Handling flightsupdate...",serverTime,respMsg);
        break;

      default:
        // Handle default case
        console.log("Unsupported event ...");
        // Add your code here
        break;
    }


}
