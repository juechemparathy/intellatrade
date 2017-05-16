package storm.kafka.project.util;


import java.io.StringReader;
import java.net.URI;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * ChatBot
 *
 * @author Jiji_Sasidharan
 */
public class RealtimeBot {

    /**
     * main
     *
     * {"messageType":"R","rateDelta":{"alphaCCYToken":"37","ccyPair":"EUR/NOK","ask":"9.35665","bid":"9.35235","status":"D","pubTimestamp":"05/15/2017 01:23:06"}}
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Start client");
        final RealtimeClient clientEndPoint = new RealtimeClient(new URI("ws://ec2-54-193-121-31.us-west-1.compute.amazonaws.com:8080/gaincapital-websocket-rateservice/broadcast"));
        clientEndPoint.addMessageHandler(new RealtimeClient.MessageHandler() {
            public void handleMessage(String message) {
                JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
                String pairName = jsonObject.getJsonObject("rateDelta").getString("ccyPair");
                String ask = jsonObject.getJsonObject("rateDelta").getString("ask");
                String bid = jsonObject.getJsonObject("rateDelta").getString("bid");
                String result = Json.createObjectBuilder()
                        .add("pair", pairName)
                        .add("ask", ask)
                        .add("bid", bid)
                        .build()
                        .toString();
                System.out.println(result);
            }
        });

        while (true) {
            clientEndPoint.sendMessage(getMessage("Hi"));
            Thread.sleep(30000);
        }
    }

    /**
     * Create a json representation.
     *
     * @param message
     * @return
     */
    private static String getMessage(String message) {
        return Json.createObjectBuilder()
                .add("user", "bot")
                .add("message", message)
                .build()
                .toString();
    }
}