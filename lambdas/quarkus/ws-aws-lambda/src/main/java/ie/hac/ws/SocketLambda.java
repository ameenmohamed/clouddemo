package ie.hac.ws;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;

import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import software.amazon.awssdk.services.apigatewaymanagementapi.model.PostToConnectionResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


import org.jboss.logging.Logger;


@Named("socket")
public class SocketLambda implements RequestHandler<APIGatewayV2WebSocketEvent, APIGatewayV2HTTPResponse> {

    long startTime = System.currentTimeMillis();
    public static Gson gson = new GsonBuilder().create(); //.setPrettyPrinting()
    private static final Logger LOG = Logger.getLogger(SocketLambda.class);

       
    private static final String connUrl = "https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/"; // get this from envirnment or parameter store 
  //  WebSocket URL: wss://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production
  //Connection URL: https://98hoolg0n7.execute-api.eu-west-1.amazonaws.com/production/@connections
  //094312144437
    private URI WSAPIURIResp= URI.create(connUrl);

    ApiGatewayManagementApiClient client =  ApiGatewayManagementApiClient.builder().endpointOverride(WSAPIURIResp).build();
    ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
    DynamoDbClient db = DynamoDbClient.builder().region(Region.EU_WEST_1).build();
    
    

   // private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).connectTimeout(Duration.ofSeconds(10)).build();
    long endTime = System.currentTimeMillis();


    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2WebSocketEvent input, Context context) {

        LOG.info("enetred lambda new INIT Duration"+ TimeUnit.MILLISECONDS.toSeconds(endTime-startTime) );
        String routekey = input.getRequestContext().getRouteKey();
        String connectionId = APPUtil.getNullSafeStr(input.getRequestContext().getConnectionId()); 
        String requestId = APPUtil.getNullSafeStr(input.getRequestContext().getRequestId()); 
        String eventType = APPUtil.getNullSafeStr(input.getRequestContext().getEventType());
             
        LOG.info("Request Body : "+input.getBody());

                   // ----- 
        ProcessingService pSrv = new ProcessingService();
                
        LOG.info("RAW Input Event:"+ gson.toJson(input));
    //    LOG.info("Request Object " + gson.toJson(req));
        LOG.info("route key to Sswitch  "+routekey);
        switch (routekey) {
            case "$connect":
                // add recird to Dynamo DB
                LOG.info(" connect switch case - add new connection "+connectionId);
               
                String origin = input.getHeaders().get("Origin");
                String callerApi = APPUtil.getNullSafeStr(input.getRequestContext().getDomainName());  // not available in ws comms 
                String sourceIp = APPUtil.getNullSafeStr(input.getHeaders().get("X-Forwarded-For"));  // not available in ws comms  in reqcontext.identity.sourceip
                String srcSite = origin == null ? "":origin; //not available in ws comms
                String traceId = APPUtil.getNullSafeStr(input.getRequestContext().getExtendedRequestId());   // no trace id but messageId

                RequestObject req = new RequestObject(routekey, callerApi, connectionId, sourceIp, requestId, eventType, srcSite,traceId);
                OutputObject respObj =  APPUtil.getOutputObject(req);
                String sendDefJsonStr = gson.toJson(respObj);
                pSrv.addConnection(db,req);
             //   sendAPIGWRes(sendDefJsonStr,connectionId);// cant send response in Connect Case , here ut just connects 
              
            break;
           
            case "$disconnect":
                LOG.info(" calling REMOVE Connection "+connectionId);
                RequestObject disConnReq = new RequestObject(routekey, "", connectionId, "", requestId, eventType, "", "");
                pSrv.removeConnection(db,disConnReq);
                LOG.info(" Success: REMOVE Connection "+connectionId);
                connectionId = null;
            break;    

            case "message":
                LOG.info(" >>>> SocketLambda:handleRequest-case:message "+connectionId);
                String sendJsonStr = SocketService.manageWebSocketMessage(input);
               
                replyAPIGWRes(sendJsonStr,connectionId);
            break;
            
            default:
            LOG.info(" default route ");
         
                Map<String, String> defdata = new HashMap<>();
                defdata.put("message", "Default response message ");
                defdata.put("requestId", requestId);
        
                OutputObject sendDefObj = new OutputObject();
                sendDefObj.setConnectionId(connectionId);
                sendDefObj.setMapData(defdata);
                String respDefJsonStr = gson.toJson(sendDefObj);
                LOG.info("WS_responseSTR: "+respDefJsonStr);
              //  sendAPIGWRes(respDefJsonStr,connectionId);
            break;
        }

        // LOG.info():
        LOG.info("End Switch.. ");     
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .build();
    }

  /* 
    public void sendData(String data){
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .uri(URI.create(connUrl))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.info("Response to WS "+response.toString());
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        
    }
    */

    public void replyAPIGWRes(String data, String connId){
        if(null != connId){
            if(null == client){
             client =  ApiGatewayManagementApiClient.builder().endpointOverride(WSAPIURIResp).build();       
             System.out.println("re Build APIGW WEBSOCKET client   ");
             }
            try {
                PostToConnectionResponse postResp =   client.postToConnection(PostToConnectionRequest.builder().connectionId(connId).data(SdkBytes.fromUtf8String(data)).build());
                System.out.println(" <<<<<<< Server Response send to websocket:"+connId+ "  respStr:"+data);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }    
        }else {
            LOG.info("sendAPIGWRes:Null conn id no smg to send to WS..  ");
        }
    }
            

}
