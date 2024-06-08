package org.hac.codewsp.lambdas.lambda;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.hac.codewsp.awsutil.HACAWSUtil;
import org.hac.codewsp.model.UPFileResponse;
import org.hac.codewsp.util.HacFileUtil;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UploadFileLambda implements RequestStreamHandler {

   // final String tempLoc = "/tmp/";
   // final static String FILENAMEKEY= "filename";
    final static String MULTIPARTCONTENT= "multipart/form-data";

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, Charset.forName("US-ASCII"))));
        String mpartBoundary ;
        ObjectMapper mapper = new ObjectMapper();
     
        
        //get environment variable named bucketname
        String S3BUCKET = System.getenv("bucketname");
        logger.log("Sys env Bucket name "+S3BUCKET);
        
        // read InputStream into a string    
        //convert string to json node
        String json = new String(input.readAllBytes());
        JsonNode jsonNode = mapper.readTree(json);
       
        // print jsonNode as string using objec mapper
        logger.log(mapper.writeValueAsString(jsonNode));
        

        // Method to read jsonNode and get  Content type from headers.contenType
        String contType =  jsonNode.get("headers").get("content-type").asText();           
        String acceptStr =  jsonNode.get("headers").get("accept").asText();     
         // get body from jsonNode 
         String reqBody = jsonNode.get("body").asText();
         String fileName = null;

        // extract boundary if content-type  is multipart/form-data
        if(contType.contains(MULTIPARTCONTENT)){
           
             mpartBoundary = HacFileUtil.extractBoundary(contType);
             System.out.println("Multipart form data ... boundary:"+mpartBoundary);
              fileName = HacFileUtil.writeS3File(reqBody,  HACAWSUtil.S3BUCKET_LOC, mpartBoundary.getBytes());
        }
        else if(contType.contains("image/")){
            System.out.println("rawnoboundary ....");
            mpartBoundary = "rawnoboundary";
            //create uniquefilename string 
          //  String uniqueFileName = HacFileUtil.createUniqueFileName();
            //write file to s3 bucket
            fileName = HacFileUtil.writeS3File(reqBody, HACAWSUtil.S3BUCKET_LOC, mpartBoundary.getBytes());
        }else{
            if(acceptStr.contains("image/")){
                System.out.println("in Accept ....");
                mpartBoundary = "rawnoboundary";
                //write file to s3 bucket
                fileName = HacFileUtil.writeS3File(reqBody,  HACAWSUtil.S3BUCKET_LOC, mpartBoundary.getBytes());
            }
        }    
        
        String resp = mapper.writeValueAsString(new UPFileResponse(fileName));
        logger.log("returining resp:"+resp);
        try (input) {
             writer.println(resp);   
        }catch (Exception e) {
            e.getMessage();
          } finally {
            writer.close();
           
          }
      
    }

    
}
