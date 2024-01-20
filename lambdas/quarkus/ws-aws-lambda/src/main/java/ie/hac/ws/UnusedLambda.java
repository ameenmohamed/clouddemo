package ie.hac.ws;

import javax.inject.Inject;
import javax.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

@Named("unused")
public class UnusedLambda implements RequestHandler<RequestObject, OutputObject> {

    @Inject
    ProcessingService service;

    @Override
    public OutputObject handleRequest(RequestObject input, Context context) {
        throw new RuntimeException("Should be unused");
    }
}
