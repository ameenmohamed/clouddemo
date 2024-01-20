package ie.hac.ws;

public class RequestObject {

    public final String routekey;
    public final String callerApi;
    public final String connectionId;
    public final String sourceIp;
    public final String requestId;
    public final String eventType;
    public final String origin;
    public final String traceId;
    public final String  tableName = "websocketusers";

    public RequestObject(String routekey, String callerApi, String connectionId, String sourceIp, String requestId,
            String eventType, String origin, String traceId) {
        this.routekey = routekey;
        this.callerApi = callerApi;
        this.connectionId = connectionId;
        this.sourceIp = sourceIp;
        this.requestId = requestId;
        this.eventType = eventType;
        this.origin = origin;
        this.traceId = traceId;
    }

    public String getTableName() {
        return tableName;
    }

    public String getRoutekey() {
        return routekey;
    }

    public String getCallerApi() {
        return callerApi;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTraceId() {
        return traceId;
    }

}
