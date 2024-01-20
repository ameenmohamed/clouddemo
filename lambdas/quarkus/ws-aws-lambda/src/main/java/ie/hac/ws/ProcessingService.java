package ie.hac.ws;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;


import org.jboss.logging.Logger;



import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@ApplicationScoped
public class ProcessingService {
    private static final Logger LOG = Logger.getLogger(ProcessingService.class);

    @PostConstruct
    public void init() {
        LOG.info("PostConstruct Constructir called  ");
    }

    public void addConnection(DynamoDbClient db,RequestObject input) {
        LOG.info("addconnection ");
        Map<String, AttributeValue> websocketConnMap = new HashMap<>();
        websocketConnMap.put("userid", AttributeValue.builder().s(input.getRequestId()).build());
        websocketConnMap.put("connectionid", AttributeValue.builder().s(input.getConnectionId()).build());
        websocketConnMap.put("eventType", AttributeValue.builder().s(input.getEventType()).build());
        websocketConnMap.put("callerApi", AttributeValue.builder().s(input.getCallerApi()).build());
        websocketConnMap.put("origin", AttributeValue.builder().s(input.getOrigin()).build());
        websocketConnMap.put("routeKey", AttributeValue.builder().s(input.getRoutekey()).build());
        websocketConnMap.put("sourceIp", AttributeValue.builder().s(input.getSourceIp()).build());
        websocketConnMap.put("traceId", AttributeValue.builder().s(input.getTraceId()).build());
        LOG.info("populated connec map ");
       
        
        PutItemRequest reStatus = PutItemRequest.builder()
                .tableName(input.getTableName())
                .item(websocketConnMap)
                .build();
       
        if(null == db){
            LOG.info("Dynamo DB client not initialized ...........");
        }else{
              PutItemResponse res=  db.putItem(reStatus);   
              LOG.info("Dynamo DB Response:"+SocketLambda.gson.toJson(input));
        }    
    }

    public void removeConnection(DynamoDbClient db,RequestObject input) {

        Map<String, AttributeValue> websocketConnMap = new HashMap<>();
        websocketConnMap.put("connectionid", AttributeValue.builder().s(input.getConnectionId()).build());
      

        DeleteItemRequest delReq = DeleteItemRequest.builder()
                .tableName(input.getTableName())
                .key(websocketConnMap)
                .build();

                if(null == db){
                    LOG.error("Dynamo DB client not initialized ...........");
                }else{
                      DeleteItemResponse res = db.deleteItem(delReq); 
                    
                      LOG.info("DynDB Delete Response Success for connId:"+input.getConnectionId());
                }    
    }

}
