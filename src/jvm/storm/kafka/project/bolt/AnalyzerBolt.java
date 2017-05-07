package storm.kafka.project.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Created by jue on 5/7/17.
 */
public class AnalyzerBolt extends BaseRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String pairInfo =  tuple.getString(0);
        System.out.println("Intellatrade: AnalyzerBolt input tuple: " + pairInfo);
        // Lookup recommendation data
        // Compare for stop loss OR sell price
        // Check if any pair is below or equal to stop loss value
        // Check for userinfo table for any potential transactions
        // If there is any potential transaction - emit userInfo and pairInfo to Transaction Bolt
        _collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
        _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("transaction"));
    }
}