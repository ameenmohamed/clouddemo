package org.anycompany.hac.lambda;

import java.net.URI;
import java.time.LocalTime;

import java.util.HashMap;
import java.util.Map;



import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;


public class WSCommsLambda implements RequestHandler<String, String>  {

    public static Gson gson = new GsonBuilder().create(); //.setPrettyPrinting()
    private static final String connUrl = "https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/"; // get this from envirnment or parameter store 
    private URI WSAPIURIResp= URI.create(connUrl);
    ApiGatewayManagementApiClient client =  ApiGatewayManagementApiClient.builder().endpointOverride(WSAPIURIResp).build();


    @Override
    public String handleRequest(String event, Context context) {
        System.out.println("Event:" + event);
        String awsRequestId = context.getAwsRequestId();
        String connId = null;
        if(null != connId){
        try {
            OutputObject outObj = new OutputObject();
            Map<String, String> pdata = new HashMap<>();
            pdata.put("command", "test first");
            pdata.put("reponse", "TEST...");
            pdata.put("serverTime", LocalTime.now().toString());

            String data = gson.toJson(outObj);
            PostToConnectionResponse postResp =   client.postToConnection(PostToConnectionRequest.builder().connectionId(connId).data(SdkBytes.fromUtf8String(data)).build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }    
    }else {
        System.out.println("sendAPIGWRes:Null conn id no smg to send to WS..  ");
    }
        return "";
    }
}
