package org.anycompany.hac.lambda;

import java.util.HashMap;
import java.util.Map;

public class OutputObject {

    private String connectionId;
    private Map<String,String> data = new HashMap<String,String>();
   
    public String getConnectionId() {
        return connectionId;
    }
    public void setConnectionId(String connectionid) {
        this.connectionId = connectionid;
    }
    public Map<String,String> getMapData() {
        return data;
    }
    public void setMapData(Map<String,String> data) {
        this.data = data;
    }
    public void setData(String key, String value) {
        data.put(key, value);
    }
    
    public String getData(String key) {
       if(data.containsKey(key)){
            return data.get(key);
       }else {
            return null;
       }
    }

}
