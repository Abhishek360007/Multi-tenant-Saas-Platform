package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.consts.WebSocketConst;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/30 14:35
 */


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/ws/test")
public class WsTestController {

    private final SimpMessagingTemplate messagingTemplate;


    /**
     * Send a broadcast message
     * -- Notes:
     *       1) @MessageMapping matches the client stomp.send('url');
     *       2) Option 1: use @SendTo("target path") without messagingTemplate and return msg; the return value goes to the @SendTo path;
     *       3) Option 2: return void and use messagingTemplate to choose the destination, without returning msg. Recommended.
     *
     * @param msg Message
     */
    @MessageMapping("/send")
    public void sendAll(@RequestParam String msg) {
        log.info("[Send a message]>>>> msg: {}", msg);
        // Send a message to the client
        messagingTemplate.convertAndSend(WebSocketConst.TOPIC, msg);
    }


}
