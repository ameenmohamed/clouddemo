package org.hac.codewsp.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.fileupload.MultipartStream;
import org.hac.codewsp.awsutil.HACAWSUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HacFileUtil{

    public final static String tempLoc =  "/tmp/";
    public final static String tempDisk = "/Users/aminasif/amroot/temp/testfos/";
    final static String FILENAMEKEY= "filename";
    final static String MULTIPARTCONTENT= "multipart/form-data";


/**
* Returns an Image object that can then be painted on the screen. 
* The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
* argument is a specifier that is relative to the url argument. 
* <p>
* This method always returns immediately, whether or not the 
* image exists. When this applet attempts to draw the image on
* the screen, the data will be loaded. The graphics primitives 
* that draw the image will incrementally paint on the screen. 
*
* @param  url  an absolute URL giving the base location of the image
* @param  name the location of the image, relative to the url argument
* @return      the Image at the specified URL
* @see         Image
*/




//method to get max number from an array as argument
public static int getMax(int[] array) {
    int max = array[0];
    for (int i = 1; i < array.length; i++) {
        if (array[i] > max) {
            max = array[i];
        }
    }
    return max;
}


    // method to decode base64 byte array
    public static byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
    

    //method to write byte array to file
    public static void writeFile(byte[] bytes, File inFile) throws IOException {
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(inFile)) {
            fos.write(bytes);
            fos.close();
        }
    }

    // method to check null or empty string
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }
    
    // method to create unqique string for file name with .jpg extention
    public static String createUniqueFileName() {
        String uniqueFileName = System.currentTimeMillis() + ".jpg";
        return uniqueFileName;
    }


    //"content-type": "multipart/form-data; boundary=----WebKitFormBoundaryqNc0s0ZsFukHV6Wd"
    //Method to extract boundary
    public static String extractBoundary(String contentType) {
        return contentType.substring(contentType.indexOf("boundary=") + "boundary=".length());
    }

    //Headers:Content-Disposition: form-data; name="file"; filename="security.png"
    // method to extract filename from header
    public static String extractFileName(String header) {
        String retStr =  header.substring(header.indexOf(FILENAMEKEY) + FILENAMEKEY.length() + 2, header.length());
        return retStr.substring(0, retStr.indexOf("\""));
    }

    public static void main(String[] args) throws Exception {
     
        testInputParse();
        int[] numberArray = {15, 23, 66, 3, 51, 79};
        System.out.println("Max number from array: " + getMax(numberArray));

    }


    public static void testInputParse()throws Exception {
      
        String json = HACTesStr.readInputFile();
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        // print jsonNode as string
        //  System.out.println(new ObjectMapper().writeValueAsString(jsonNode));

    // Method to get Content type from headers.contenType
       String contType =  jsonNode.get("headers").get("content-type").asText();   
        System.out.println(" body content-type: " + contType);
        byte[] boundary= null;
        if(contType.contains(MULTIPARTCONTENT)){
          
            // extract boundray String from content-type
            String mpartBoundary = HacFileUtil.extractBoundary(contType);
            boundary = mpartBoundary.getBytes();
            System.out.println("boundary: " + mpartBoundary.trim());

        }else{
            boundary = "rawnoboundary".getBytes();
        }
    
    String reqBody = jsonNode.get("body").asText();
    String fileNm =  writeS3File(reqBody,HACAWSUtil.S3BUCKET_LOC , boundary);
    System.out.println("Saved File: "+fileNm);

    }
   

    // Method to write file to s3 bucket with filename as key and s3 buckey key as a parameter
    public static String writeS3File(String reqBody, String path, byte[] boundary) throws IOException {
        
        
        String retString = "";  

       String strBoundary = new String(boundary);
        if("rawnoboundary".equalsIgnoreCase(strBoundary)){      
            JsonNode jsonBody = new ObjectMapper().readTree(reqBody);
            String fileName  =  jsonBody.get("filename").asText();
            byte[] decodedBytes = Base64.getDecoder().decode(jsonBody.get("content").asText());
            System.out.println("Binary handle -File name: "+fileName);
            ByteArrayInputStream content = new ByteArrayInputStream(decodedBytes);
            retString = handleBinary(content, fileName);
        }else {
            //base64 decode the reqBody to byte array 
            byte[] decodedBytes = Base64.getDecoder().decode(reqBody);
            ByteArrayInputStream content = new ByteArrayInputStream(decodedBytes);
         
            retString = handleMultiPart(content,  path, boundary);
        
         }//ense else  
         return retString;
    
    }

    public static String handleBinary(ByteArrayInputStream content, String fileName) throws IOException {
        if(HACAWSUtil.isEnvLive()){
          
            HACAWSUtil.writeFileToS3(content.readAllBytes(), fileName);
        return fileName;
        }else{
            File file = new File(tempDisk + "/" +  fileName);
            HacFileUtil.writeFile(content.readAllBytes(), file);
            return file.getName();
        }
        

    }

    public static String handleMultiPart(ByteArrayInputStream content,String path, byte[] boundary){
        StringBuilder fileNames  = new StringBuilder("");
        
        @SuppressWarnings("deprecation")
        MultipartStream multipartStream = new MultipartStream(content, boundary);
        try {
             boolean nextPart = multipartStream.skipPreamble();
        // method to write byte array to s3 bucket
        while (nextPart) {

            String header = multipartStream.readHeaders();
            String fileName = HacFileUtil.extractFileName(header);
            System.out.println("FileName:"+fileName);
            File file;
            if(HACAWSUtil.isEnvLive()){
                file = new File(tempLoc + "/" + fileName);
            }
            else{
                file = new File(tempDisk + "/" +  fileName);
            }
    
            try (FileOutputStream fos = new FileOutputStream(file)) {
             // System.out.println("Headers: " + header);
            //    System.out.println("Body: " +  multipartStream.readBodyData(fos));
            multipartStream.readBodyData(fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
          
             // file to byte Array 
        byte[] fileBytes = HacFileUtil.readFile(file);
        // write file to s3 bucket
            
        String fleName =  HACAWSUtil.writeFileToS3(fileBytes, fileName);

        fileNames.append(fleName);

        nextPart = multipartStream.readBoundary();
         //   if(nextPart){
           //     fileNames.append(",");
         //   }
           
        } 
    }catch (Exception e) {
        System.out.println(e.getMessage());  
       }
        return fileNames.toString();
    }

    // Method to read File into Byte array
    public static byte[] readFile(File file) {
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileBytes);
            fis.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return fileBytes;
    }



}



