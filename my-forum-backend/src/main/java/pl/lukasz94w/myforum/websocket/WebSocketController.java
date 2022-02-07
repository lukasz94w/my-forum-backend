package pl.lukasz94w.myforum.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import pl.lukasz94w.myforum.request.WebServiceMessageRequest;

import javax.validation.Valid;

@Controller
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/message-from-admin")
    public WebServiceMessageRequest webServiceMessage(@Valid WebServiceMessageRequest request) throws Exception {
        return request;
    }
}
