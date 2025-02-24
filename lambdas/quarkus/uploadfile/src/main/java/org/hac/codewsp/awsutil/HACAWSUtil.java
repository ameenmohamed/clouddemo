package org.hac.codewsp.awsutil;

import java.io.IOException;

import org.hac.codewsp.util.HacFileUtil;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
//import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;


public class HACAWSUtil{
    static final SdkHttpClient httpClient = ApacheHttpClient.create();
    public final static String S3BUCKET_LOC= "assets/uploads/";
   //static SdkHttpClient httpClient = UrlConnectionHttpClient.create();

    static final AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
    static final Region region = Region.EU_WEST_1;
	static S3Client s3 = S3Client.builder()
        .httpClient(httpClient)
        .defaultsMode(DefaultsMode.IN_REGION)
        .region(region)
        .credentialsProvider(credentialsProvider).build();

        
    // method to return S3 client
    public static S3Client getS3Client() {
        System.out.println("Accesskey:"+credentialsProvider.resolveCredentials().accessKeyId());
        System.out.println("Secretkey:"+credentialsProvider.resolveCredentials().secretAccessKey());
        System.out.println("Region:"+region.toString());
        System.out.println("S3Client:"+s3.toString());
        return s3;
    }

    // create aws s3 client with iam credentials and eu-west-1 region and apache http client
    // method to write byte array to s3 bucket
    public static String writeFileToS3(byte[] bytes, String fileName) throws IOException {
        String flName;
        String bucketName = System.getenv("bucketname");
        if(bucketName == null) { 
            System.out.println("Code is running LOCAL ...");
            // code running in local ENV 
            flName =  HacFileUtil.tempDisk+fileName;
            // write bytes to file
            java.io.FileOutputStream fos = new java.io.FileOutputStream(flName);
            fos.write(bytes);
            fos.close();


        }else {
           
            flName = S3BUCKET_LOC + fileName;
           
            PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(flName)
            .build();
            PutObjectResponse writeResp = s3.putObject(objectRequest, RequestBody.fromBytes(bytes));
            String retetag = writeResp.eTag();
            System.out.println("HACAWSUtil:writeFileToS3:Write resp eTag Value"+retetag);
        }
        return flName;
    }

    public static boolean isEnvLive(){
        String S3BUCKET = System.getenv("bucketname");
        if(S3BUCKET == null) {
            return false;
        }
        else
         return true;
    }

 
    
}   