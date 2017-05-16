/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package storm.kafka.project.spout;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.kafka.project.util.RealtimeClient;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class KafkaTestSpout extends BaseRichSpout {
    public static Logger LOG = LoggerFactory.getLogger(KafkaTestSpout.class);
    boolean _isDistributed;
    SpoutOutputCollector _collector;

    public KafkaTestSpout() {
        this(true);
    }

    public KafkaTestSpout(boolean isDistributed) {
        _isDistributed = isDistributed;
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }

    public void close() {

    }

    public void nextTuple() {
        Utils.sleep(100);
        try {
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
                    _collector.emit(new Values(result));
                }
            });

            while (true) {
                clientEndPoint.sendMessage("Hi");
//                Thread.sleep(30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ack(Object msgId) {

    }

    public void fail(Object msgId) {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        if (!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }
}