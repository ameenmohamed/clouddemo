

var seatsTemplate = "";
var seatsTemplatePath = "/assets/templates/seats-fragment.html";
var seatsTemplateContent = "";




/*
<div id="dynamicContent"></div>

<!-- HTML template -->
<template id="template">
  <p>Hi, <span class="name"></span>! Welcome to our website.</p>
  <p>Your account balance is <span class="balance"></span>.</p>
</template>

// Get a reference to the <div> element
var div = document.getElementById("dynamicContent");

// Get a reference to the HTML template
var template = document.getElementById("template");  // this we need to get from path

// Clone the template content
var templateContent = template.content.cloneNode(true);

// Find the placeholders in the template and substitute the data
var nameElement = templateContent.querySelector(".name");
nameElement.textContent = "John Doe";

var balanceElement = templateContent.querySelector(".balance");
balanceElement.textContent = "$500";

// Append the template content to the <div> element
div.appendChild(templateContent);
*/ 


function getSeatUI(template) {
  
   // var seatsContent = template.content.cloneNode(true);
    /* update as sp 
    var nameElement = seatsContent.querySelector(".name");
        nameElement.textContent = "John Doe";
    // Find the placeholders in the template and substitute the data
        var nameElement = seatsContent.querySelector(".name");
        nameElement.textContent = "John Doe";
    */ 
    document.getElementById("dynamicContent").innerHTML=template;
    resetActiveTab("seatsTab");
   
}

function resetActiveTab(activelinkName){
    var navLinks = document.getElementsByClassName("nav-link");
    Array.from(navLinks).forEach(function(link) {
        link.classList.remove("active");
      });
      // Add "active" class to the clicked link
      var link = document.getElementById(activelinkName);
      link.classList.add("active");
}


/**
 * 
 * @param {*} templatePath 
 * @param {*} callbackFNCName 
 * we give the html template name e.g seatsTemplatePath  and 
 * callback function name that will merge  the tempate with data to be displayed 
 */
function loadTemplateWithConent(templatePath,callbackFNCName) {

    fetch(templatePath)
        .then(response => response.text())
        .then(html => {
            // Process the loaded HTML fragment
            console.log("tenplate conents :",html);
            callbackFNCName(html);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
