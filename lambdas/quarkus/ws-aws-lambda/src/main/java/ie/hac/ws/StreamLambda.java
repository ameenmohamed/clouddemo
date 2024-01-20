package ie.hac.ws;

import javax.inject.Named;

import org.jboss.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Named("stream")
public class StreamLambda implements RequestStreamHandler {
    Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    private static final Logger LOG = Logger.getLogger(StreamLambda.class);

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
       
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
         
        JsonElement jsonElement = JsonParser.parseReader(reader);
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        LOG.info("using  GSon");
     //   HashMap event = gson.fromJson(reader, HashMap.class);
         LOG.info(jsonObj.get("Origin").toString() + jsonObj.get("X-Forwarded-For").toString()); 
         LOG.info( jsonObj.get("Host").toString() + jsonObj.get("routeKey").toString() + jsonObj.get("connectionId").toString());

        writer.write("{status:200}");


    }

    
}
