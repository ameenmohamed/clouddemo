package org.anycompany.hac.lambda;

import java.util.List;
import java.util.Map;

import org.anycompany.model.PNR;
import org.anycompany.model.SFRequest;
import org.anycompany.service.SaveBatch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import io.quarkus.amazon.lambda.runtime.*;
import io.quarkus.runtime.annotations.*;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;
//import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

//import javax.inject.Inject;

/*
 * {
 * //https://docs.aws.amazon.com/lambda/latest/dg/with-sqs-create-package.html#with-sqs-example-deployment-pkg-java
 * //https://docs.aws.amazon.com/lambda/latest/dg/java-package.html#java-package-maven
 “Records”: [
 {
 “messageId”: “059f36b4–87a3–44ab-83d2–661975830a7d”,
 “receiptHandle”: “AQEBwJnKyrHigUMZj6rYigCgxlaS3SLy0a…”,
 “body”: “Test message.”,
 “attributes”: {
 “ApproximateReceiveCount”: “1”,
 “SentTimestamp”: “1545082649183”,
 “SenderId”: “AIDAIENQZJOLO23YVJ4VO”,
 “ApproximateFirstReceiveTimestamp”: “1545082649185”
 },
 “messageAttributes”: {},
 “md5OfBody”: “e4e68fb7bd0e697a0ae8f1bb342846b3”,
 “eventSource”: “aws:sqs”,
 “eventSourceARN”: “arn:aws:sqs:us-east-2:123456789012:my-queue”,
 “awsRegion”: “us-east-2”
 },
 {
 “messageId”: “2e1424d4-f796–459a-8184–9c92662be6da”,
 “receiptHandle”: “AQEBzWwaftRI0KuVm4tP+/7q1rGgNqicHq…”,
 “body”: “Test message.”,
 “attributes”: {
 “ApproximateReceiveCount”: “1”,
 “SentTimestamp”: “1545082650636”,
 “SenderId”: “AIDAIENQZJOLO23YVJ4VO”,
 “ApproximateFirstReceiveTimestamp”: “1545082650649”
 },
 “messageAttributes”: {},
 “md5OfBody”: “e4e68fb7bd0e697a0ae8f1bb342846b3”,
 “eventSource”: “aws:sqs”,
 “eventSourceARN”: “arn:aws:sqs:us-east-2:123456789012:my-queue”,
 “awsRegion”: “us-east-2”
 }
 ]
}
 */
//@FunctionName("pollSqsQueue")
public class SQSPollLambda implements RequestHandler<SQSEvent, Void> {

    public static Gson gson = new GsonBuilder().create(); //.setPrettyPrinting()
   
    AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();
    Region region = Region.EU_WEST_1;
   // Region region = Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable()));

    // Create an instance of AWSStepFunctions client
    SfnClient stepFunctionsClient = SfnClient.builder().region(region).credentialsProvider(credentialsProvider).build();

    // @Inject
  //  SqsAsyncClient sqsClient;

    @Override
    public Void handleRequest(SQSEvent event, Context context)  {
        StringBuffer msgAttrsBuffer = new StringBuffer("message:\n");
        SaveBatch sb = new SaveBatch();
        List<PNR> pnrList  = sb.saveBatchRecords(event.getRecords());
        String connectionId = sb.getConnectionId();
        for (PNR pnr : pnrList) {
            // Build the StartExecutionRequest
            SFRequest sfRequest = new SFRequest(pnr, connectionId);
            String sfReqJson = SaveBatch.getGSon().toJson(sfRequest);
            
            StartExecutionRequest executionRequest = StartExecutionRequest.builder()
            .stateMachineArn("arn:aws:states:eu-west-1:094312144437:stateMachine:SFunc-AddTravelDocs")
            .input(sfReqJson)
            .build();

            // Invoke the Step Function
            StartExecutionResponse  resp = stepFunctionsClient.startExecution(executionRequest);
          
        }

       
        return null;


    }
         /*/
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            String messageBody = message.getBody();
            // Process the message as desired
            msgAttrsBuffer.append("Received message: " + messageBody +"\n");
           
            Map<String,String> msgAttr =message.getAttributes();
            msgAttrsBuffer.append("Message Attributes: \n");
            for (Map.Entry<String,String> entry : msgAttr.entrySet()) { 
                msgAttrsBuffer.append("Key = " + entry.getKey() + ", Value = " + entry.getValue()+"\n");
            }
        }
        */
     //   System.out.println(msgAttrsBuffer);
}
