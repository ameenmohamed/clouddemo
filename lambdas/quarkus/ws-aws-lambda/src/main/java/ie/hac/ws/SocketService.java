package ie.hac.ws;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2WebSocketEvent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/*
 * {
    "requestContext": {
        "stage": "production",
        "requestId": "FHfWyGOhDoEFyZQ=",
        "identity": {
            "sourceIp": "54.240.197.231",
            "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
        },
        "apiId": "98hoolg0n7",
        "connectedAt": 1684410752252,
        "connectionId": "FHesFd_oDoECJOA=",
        "domainName": "98hoolg0n7.execute-api.eu-west-1.amazonaws.com",
        "eventType": "MESSAGE",
        "extendedRequestId": "FHfWyGOhDoEFyZQ=",
        "messageDirection": "IN",
        "messageId": "FHfWyfJ1DoECJOA=",
        "requestTime": "18/May/2023:11:57:05 +0000",
        "requestTimeEpoch": 1684411025592,
        "routeKey": "message"
    },
    "body": "{\"action\":\"message\",\"msg\":\"YA Baqi Antalbaqi Laisal Baqi Illallah 11\"}",
    "isBase64Encoded": false
}
 * 
 * 
 */
@ApplicationScoped
public class SocketService {

    private static final Logger LOG = Logger.getLogger(SocketService.class);

    @PostConstruct
    public void init() {
        LOG.info("PostConstruct Constructir called  ");
    }

    public static String manageWebSocketMessage(APIGatewayV2WebSocketEvent input) {
         LOG.info("manageWebSocketMessage:Request Body : "+input.getBody());
         // {"action":"message","command":"getconnectionId"}

         String connectionId = APPUtil.getNullSafeStr(input.getRequestContext().getConnectionId()); 
         String requestId = APPUtil.getNullSafeStr(input.getRequestContext().getRequestId()); 
         String body =  APPUtil.getNullSafeStr(input.getBody());

         if(null !=body && body.contains("action") && body.contains("command")) {

            JsonElement jsonElement = JsonParser.parseString(input.getBody());
            JsonObject jsonInputBody = jsonElement.getAsJsonObject();

            // read body using json-path library
            
            JsonElement elementCmd = jsonInputBody.get("command");
            String command = (elementCmd != null && !elementCmd.isJsonNull()) ? elementCmd.getAsString() : null;

             LOG.info("****** SocketService:manageWebSocketMessage:command: "+command);
             OutputObject sendObj = new OutputObject();
             sendObj.setConnectionId(connectionId);
           
             switch(command) {
                case "getconnectionId":
                        LOG.info("**** SocketService:manageWebSocketMessage:switchCase-message "+command);
                        Map<String, String> data = new HashMap<>();
                        data.put("command", command);
                        data.put("reponse", "200");
                        sendObj.setMapData(data);    
                        
                    break;
                case "ping":
                    LOG.info("**** SocketService:manageWebSocketMessage:switchCase-message "+command);
                    Map<String, String> pdata = new HashMap<>();
                    pdata.put("command", command);
                    pdata.put("reponse", "pong");
                    pdata.put("serverTime", LocalTime.now().toString());
                    sendObj.setMapData(pdata);    
                    
                break;
                default:              
                        LOG.info("SocketService:manageWebSocketMessage:switchCase-default ");
                        Map<String, String> ddata = new HashMap<>();
                        ddata.put("command", command);
                        ddata.put("reponse", "Unsupported Command");
                    sendObj.setMapData(ddata);   
                     
                break;
             }//end switch
               
       
     
       String respMsg = SocketLambda.gson.toJson(sendObj);
   
        LOG.info("manageWebSocketMessage:WS_responseSTR: "+respMsg);
        return respMsg;
        
    }else{//end if 
        return null;//throw exception instead 
    }
    
}
}
