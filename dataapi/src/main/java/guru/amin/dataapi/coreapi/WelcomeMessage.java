package guru.amin.dataapi.coreapi;

import org.springframework.stereotype.Component;

@Component
public class WelcomeMessage {

    private String message;


    public String getWelcomeMessage(String message) {

        return this.message = message;
    }
}