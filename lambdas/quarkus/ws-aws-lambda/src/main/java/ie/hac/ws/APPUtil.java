package ie.hac.ws;

import java.util.HashMap;
import java.util.Map;


public class APPUtil {
    
 //public   static  DynamoDbClient dynamoDB = DynamoDbClient.builder().region(Region.EU_WEST_1).credentialsProvider(InstanceProfileCredentialsProvider.create()).build();
//public static InstanceProfileCredentialsProvider credentialsProvider = InstanceProfileCredentialsProvider.create();


   public static String getNullSafeStr(String inString) {
        if(inString == null){
            return "";
        }else {
            return inString;
        }
   }          

   public static OutputObject getOutputObject(RequestObject reqIn){
    String connId = reqIn.getConnectionId();
  
    Map<String, String> defdata = new HashMap<>();
        defdata.put("message", "Default response message ");
        defdata.put("requestId", reqIn.getRequestId());
        defdata.put("origin", reqIn.getOrigin());
        defdata.put("traceId", reqIn.getTraceId());


    OutputObject sendDefObj = new OutputObject();
        sendDefObj.setConnectionId(connId);
        sendDefObj.setMapData(defdata);

    return sendDefObj;
   }


}
