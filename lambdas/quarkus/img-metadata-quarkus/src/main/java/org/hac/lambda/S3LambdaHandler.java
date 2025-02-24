package org.hac.lambda;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


public class S3LambdaHandler implements RequestHandler<S3Event, Void>{

    public static Gson gson = new GsonBuilder().setExclusionStrategies(new CircularReferenceExclusionStrategy()).create(); //.setPrettyPrinting()
    public static final String METADATA_SQS_URL = "https://sqs.eu-west-1.amazonaws.com/094312144437/queue-am-search-img-filemd";
    Region region = Region.EU_WEST_1;
    AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
    
    S3Client s3client = S3Client.builder().region(region)
                            .credentialsProvider(credentialsProvider)
                            .build();

    SqsClient sqs = SqsClient.builder().region(region)
                            .credentialsProvider(credentialsProvider)
                            .build();

    @Override
    public Void handleRequest(S3Event event, Context context) {
        AtomicInteger count = new AtomicInteger(0);
        event.getRecords().forEach(record -> {
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();

            int currentCount = count.incrementAndGet();
            String respMetadata = getS3MetaData(bucketName,objectKey);
            SendMessageResponse response = sendSQSMessage(respMetadata);
            String messageId = response.messageId();
            int statusCode = response.sdkHttpResponse().statusCode();
            System.out.println(objectKey + " processed successfully" + " MessageId "+messageId + " status "+statusCode);
         //   System.out.println("Object key is "+objectKey + "count "+currentCount);
            // Process the S3 event
          
        });
        return null;
    }

    private SendMessageResponse sendSQSMessage(String message){
        try {
            SendMessageRequest request = SendMessageRequest.builder()
            .queueUrl(METADATA_SQS_URL)
            .messageBody(message)
            .build();

            SendMessageResponse sendResp =  sqs.sendMessage(request);
            return sendResp;
        }catch(Exception e){
            System.out.println("Exception in sendSQSMessage "+e.getMessage());
            return null;
        }
    }

    private String getS3MetaData(String bucketName,String keyName){
        String respMetadata = "";
        // Create GetObjectRequest
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

                try {
                    // Download the file from S3
                    ResponseBytes<GetObjectResponse> objectBytes = s3client.getObjectAsBytes(getObjectRequest);
                    byte[] data = objectBytes.asByteArray();

                    // Write the data to a local file.
                    File myFile = new File("/tmp/"+keyName);
                    OutputStream os = new FileOutputStream(myFile);
                    os.write(data);
                    System.out.println("Successfully obtained bytes from an S3 object");
                    os.close();
                    System.out.println("File downloaded successfully.");
                    Metadata metadata = ImageMetadataReader.readMetadata(myFile);

                    
                    // Get the GPS directory from the metadata
                     GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
                     if (gpsDirectory != null) {
                        // Retrieve the latitude and longitude values
                        Rational[] latitudeComponents = gpsDirectory.getRationalArray(GpsDirectory.TAG_LATITUDE);
                        Rational[] longitudeComponents = gpsDirectory.getRationalArray(GpsDirectory.TAG_LONGITUDE);
        
                        // Convert the latitude and longitude components to double values
                        double latitude = latitudeComponents[0].doubleValue() +
                                latitudeComponents[1].doubleValue() / 60 +
                                latitudeComponents[2].doubleValue() / 3600;
        
                        double longitude = longitudeComponents[0].doubleValue() +
                                longitudeComponents[1].doubleValue() / 60 +
                                longitudeComponents[2].doubleValue() / 3600;
        
                        // Print the GPS information
                        System.out.println("Latitude: " + latitude);
                        System.out.println("Longitude: " + longitude);

                    } else {
                        System.out.println("GPS information not found.");
                    } 
                    
                    JsonObject metadataJsonObj = new JsonObject(); //root object

                    for (Directory directory : metadata.getDirectories()) {
                                               
                        if(!directory.isEmpty()){
                            JsonObject jsonObject = new JsonObject();
                           // System.out.println("Tag Count: "+ directory.getTagCount());
                            jsonObject.addProperty("TagCount", directory.getTagCount());
                            for (Tag tag : directory.getTags()) {
                                if(tag.hasTagName()){
                                    jsonObject.addProperty(tag.getTagName(), tag.getDescription());
                                 //   System.out.println("Directory: " + tag.getDirectoryName() + " TAG:"+tag.getTagName() + " TagDesc:"+tag.getDescription()) ;
                                }
                            }
                            metadataJsonObj.add(directory.getName(), jsonObject);
                         }
                    
                 }//end for loop 
                 System.out.println("Saved file :"+myFile.getAbsolutePath());
                 respMetadata = gson.toJson(metadataJsonObj);
                 System.out.println("fromjsonNodes Metadata :"+respMetadata);
                } catch (S3Exception | ImageProcessingException |  IOException e) {
                    e.printStackTrace();
                } finally {
                    // Close the S3 client
                    //s3Client.close();
                }
                return respMetadata;

    }


     // Exclusion strategy to exclude circular references during serialization
     static class CircularReferenceExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(Expose.class) != null;
        }
    }
    
}
