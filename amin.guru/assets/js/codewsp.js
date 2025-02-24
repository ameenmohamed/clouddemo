const upload_endpoint = "https://api.amin.guru/uploadimage";

document.addEventListener('DOMContentLoaded', () => {

  const fileInput = document.getElementById('fileInput');
  filename = "";
  fileContent = "";

  fileInput.addEventListener('change', () =>  {
    if (fileInput.files.length > 0) {
   // const selectedFile = event.target.files[0];
    const selectedFile = fileInput.files[0];
     // Extract filename and add as a post parameter
     const filename = selectedFile.name;
    encodeFileToBase64(selectedFile, (base64Data) => {
      setPostData(filename, base64Data);
    });
  }else{
    console.log("No file selected");
  }
});

});

function setPostData(flname, b64Data) {
  filename = flname;
  fileContent = b64Data;
}

function encodeFileToBase64(file, callback) {
  const reader = new FileReader();
  reader.onload = (event) => {
    const base64Data = event.target.result.split(',')[1];
    callback(base64Data);
  };
  reader.onerror = (error) => {
    console.error("Error:", error);
  };
  reader.readAsDataURL(file);
}


async function postFileAsJsonWithFilename() {
  console.log("length of File to upload :"+fileContent.length);
  document.getElementById("loadingbtn").hidden = false;
  
  const formData = {
    filename: filename,
    content: fileContent
  };

   // You can send the formData to the server using fetch or other AJAX methods
      // Example using fetch:
      const response = await fetch(upload_endpoint, {
        method: 'POST',
        body: JSON.stringify(formData),
        headers: {
           "Accept":"image/webp,image/*,*/*;q=0.8", 
      //    "Content-type": "multipart/form-data; charset=UTF-8",
        }
      });
      const respdata = await response.json();
      document.getElementById('fileInput').value = "";
      document.getElementById("loadingbtn").hidden = true;
     // document.getElementById('responsedata').innerHTML = "<h2>Response : Upload Success </h2><p>" + respdata.filename + "</p>";
      var image = document.querySelector('#image');
      image.src = '../../assets/uploads/'+respdata.filename ;
      document.getElementById("imgcont").hidden = false;
      document.getElementById('imgattrs').innerHTML = "<h4>File name :  </h4>" + respdata.filename ;
      console.log(respdata);  // Response data as text

}

