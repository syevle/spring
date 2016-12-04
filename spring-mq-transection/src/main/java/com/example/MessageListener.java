package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;



@Component
class MessageListener {

    private Log log = LogFactory.getLog(getClass());

    @JmsListener(destination = App.MSGS)
    public void messageWritten(String id) {
        log.info("wrote message having id: " + id);
    }
}
