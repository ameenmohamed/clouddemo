package org.anycompany.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anycompany.model.AnyCompanyModel;
import org.anycompany.model.PNR;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import org.anycompany.util.*;




public class SaveBatch {

     ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
     DynamoDbClient client = DynamoDbClient.builder().region(Region.EU_WEST_1).build();
     String connectionId = "";

     public static GsonBuilder gsonBuilder ;
     public static Gson gson ;

    public  List<PNR>  saveBatchRecords(List<SQSMessage> messages){
        List<WriteRequest> writeRequests = new ArrayList<>();
        StringBuffer msgAttrsBuffer = new StringBuffer("message:\n");
        List<PNR> pnrList = new ArrayList<>();

        for (SQSEvent.SQSMessage message : messages) {
            String messageBody = message.getBody();
            // Process the message as desired
            msgAttrsBuffer.append("Received message: " + messageBody +"\n");

            JsonElement jsonElement = JsonParser.parseString(messageBody);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            connectionId = jsonObject.get("connectionId").getAsString();
            
            // Generate a random PNR with message body fields
            PNR pnr = AnyCompanyModel.generateRandomPNR( messageBody);
            pnrList.add(0, pnr);
       
            String json = buildGson().toJson(pnr);

            // Create PutRequest using the JSON string
            Map<String, AttributeValue> itemMap = new HashMap<>();
            itemMap.put("pnr", AttributeValue.builder().s(AnyCompanyModel.generateRandomString(6)).build());

            //String seatClass, String ticketNumber,             String ticketStatus
            Map<String, AttributeValue> bookingMap = new HashMap<>();
                    bookingMap.put("bookingReference", AttributeValue.builder().s(pnr.booking().bookingReference()).build());
                    bookingMap.put("seatClass", AttributeValue.builder().s(pnr.booking().seatClass()).build());
                    bookingMap.put("ticketNumber", AttributeValue.builder().s(pnr.booking().ticketNumber()).build());
                    bookingMap.put("ticketStatus", AttributeValue.builder().s(pnr.booking().ticketStatus()).build());       
            itemMap.put("booking", AttributeValue.builder().m(bookingMap).build());
            itemMap.put("journey", AttributeValue.builder().l(DynDBUtil.getDynFlights(pnr.journey())).build());
            itemMap.put("passengers", AttributeValue.builder().l(DynDBUtil.getDynPassengers(pnr.passengers())).build());

            Map<String,String> msgAttr =message.getAttributes();
            msgAttrsBuffer.append("Message Attributes: \n");
           
            Map<String, AttributeValue> passNameMap = new HashMap<>();
            for (Map.Entry<String,String> entry : msgAttr.entrySet()) { 
               
                    passNameMap.put(entry.getKey(), AttributeValue.fromS(entry.getValue()));
                    msgAttrsBuffer.append("Key = " + entry.getKey() + ", Value = " + entry.getValue()+"\n");
              }
              itemMap.put("MessageAttributes", AttributeValue.builder().m(passNameMap).build());
              itemMap.put("ConnectionId", AttributeValue.builder().s(connectionId).build());
              
               //  itemMap.put("json", AttributeValue.builder().s(json).build());
              PutRequest putRequest = PutRequest.builder().item(itemMap).build();
              System.out.println(pnr);
              writeRequests.add(WriteRequest.builder().putRequest(putRequest).build());
            
        }

        BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                .requestItems(Collections.singletonMap("bookings", writeRequests))
                .build();

        BatchWriteItemResponse batchWriteItemResponse = client.batchWriteItem(batchWriteItemRequest);

        Map<String, List<WriteRequest>> unprocessedItems = batchWriteItemResponse.unprocessedItems();
            if (!unprocessedItems.isEmpty()) {
                System.out.println("Some items were not processed:");
                System.out.println(unprocessedItems);
            } else {
                System.out.println("All items were successfully processed.");
            }

            return pnrList;
    }

    public String getConnectionId() {
        return connectionId;
    }
    
    public static Gson buildGson(){
            gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
            gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
            gson = gsonBuilder.setPrettyPrinting().create();
            return gson;
    }

    public static Gson getGSon() {
        if(null != gson){
            return gson;
        }else{
           return  buildGson();
        }
    }

}
