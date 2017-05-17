/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package storm.kafka.project;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import storm.kafka.*;
import storm.kafka.project.bolt.AnalyzerBolt;
import storm.kafka.project.bolt.TransactionBolt;
import storm.kafka.project.spout.KafkaTestSpout;

/**
 * Storm topology for IntellaTrade.
 */
public class AutoTradeTopology {
  private static String zkConnString = "ec2-54-67-46-190.us-west-1.compute.amazonaws.com";
  private static String topicName = "GainCap_RSData";

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();

    BrokerHosts hosts = new ZkHosts(zkConnString);
    SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/"+topicName, topicName);
    spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

//    builder.setSpout("pairinfo", new KafkaSpout(spoutConfig), 10);
    builder.setSpout("pairinfo", new KafkaTestSpout(), 10);
    builder.setBolt("analyser", new AnalyzerBolt(), 3).shuffleGrouping("pairinfo");
    builder.setBolt("transaction", new TransactionBolt(), 2).shuffleGrouping("analyser");

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 0) { // prod
      conf.setNumWorkers(3);
      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else { // local
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("test", conf, builder.createTopology());
      Utils.sleep(1000000000);
      cluster.killTopology("test");
      cluster.shutdown();
    }
  }
}
