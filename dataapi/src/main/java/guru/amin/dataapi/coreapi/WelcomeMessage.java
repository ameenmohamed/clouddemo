package restapi.coreapi;

import org.springframework.stereotype.Component;

@Component
public class WelcomeMessage {

    private String message;


    public WelcomeMessage(String message) {
        this.message = message;
    }
}