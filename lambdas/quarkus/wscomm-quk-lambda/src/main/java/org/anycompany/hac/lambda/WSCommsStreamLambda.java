package org.anycompany.hac.lambda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalTime;

import java.util.HashMap;
import java.util.Map;



import com.amazonaws.services.lambda.runtime.Context;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;


public class WSCommsStreamLambda implements RequestStreamHandler   {

    public static Gson gson = new GsonBuilder().create(); //.setPrettyPrinting()
    private static final String connUrl = "https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/"; // get this from envirnment or parameter store 
    private URI WSAPIURIResp= URI.create(connUrl);
    ApiGatewayManagementApiClient client =  ApiGatewayManagementApiClient.builder().endpointOverride(WSAPIURIResp).build();


    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
	    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
        
        String awsRequestId = context.getAwsRequestId();
        HashMap event = gson.fromJson(reader, HashMap.class);
        String traceId = (String) event.get(AppConstants.TRACEID);
        String jsonString = gson.toJson(event);
       

        System.out.println("**** Input Data :"+jsonString);
        String connId = (String)event.get("connectionId");
        if(null != connId){
        try {

            if(event.containsKey("wscomms")){
                LinkedTreeMap<String, String> hshMapComms = (LinkedTreeMap<String, String>) event.get("wscomms");
                System.out.println("Type : "+hshMapComms.getClass().getName());
                System.out.println(hshMapComms);
                String wsmsgcat = (String)hshMapComms.get("wsmsgcat");
                String wsmsg = (String)hshMapComms.get("wsmsg");
                String state = (String)hshMapComms.get("state");


                OutputObject outObj = new OutputObject();
              
                outObj.setData("command", "serverevent");
                outObj.setData("wsmsgcat", wsmsgcat);
                outObj.setData("response", wsmsg);
                outObj.setData("serverTime", LocalTime.now().toString());
                outObj.setConnectionId(connId);
         
                String data = gson.toJson(outObj);
                System.out.println("Sending WS Socket data: "+data);
                PostToConnectionResponse postResp =   client.postToConnection(PostToConnectionRequest.builder().connectionId(connId).data(SdkBytes.fromUtf8String(data)).build());
                System.out.println("WS Socket postResp: "+postResp.sdkHttpResponse().statusCode());

            }

            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }    
    }else {
        System.out.println("sendAPIGWRes:Null conn id no smg to send to WS..  ");
    }
    event.remove("wscomms");
    String updatedEvent = gson.toJson(event);
    writer.print(updatedEvent);
    writer.flush();
    writer.close();
    }
}
//NcR*ThEkQPkN2tgC