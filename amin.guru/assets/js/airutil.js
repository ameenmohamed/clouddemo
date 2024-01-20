function callBooking(msg){
    var testStr = '{ "connectionId": "testc", "data": { "category": "booking","message": "your PNR num ABC will need XYZ docyment in UAE Dubai" }}';
    var  tstJson =JSON.parse(testStr);
    //  alert(tstJson.data.message);
      document.getElementById("notificationArea").innerHTML = tstJson.data.message; 
}

function callBoarding(){
  var testStr = '{ "connectionId": "testc", "data": { "category": "boarding","message": "Your Boarding gate is change to gate 22" }}';
  var  tstJson =JSON.parse(testStr);
 //   alert(tstJson.data.message);
    document.getElementById("notificationArea").innerHTML = tstJson.data.message; 
}

function callGeneral(){
  var testStr = '{ "connectionId": "testc", "data": { "category": "travel","message": "remember BREXIT happened!! now you need visa  " }}';
  var  tstJson =JSON.parse(testStr);
  //  alert(tstJson.data.message);
    document.getElementById("notificationArea").innerHTML = tstJson.data.message; 
}