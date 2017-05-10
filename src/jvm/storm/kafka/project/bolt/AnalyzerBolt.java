package storm.kafka.project.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import storm.kafka.project.util.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        String pairInfo = tuple.getString(0);

        System.out.println("Intellatrade: AnalyzerBolt input tuple: " + pairInfo);
        // Lookup recommendation data
        try {
            Date todayDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            if (DatabaseHelper.getCurrentDate() != null || sdf.format(todayDate).equals(sdf.format(DatabaseHelper.getCurrentDate()))) {
                DatabaseHelper.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userPick = "GBP USD";
        if(DatabaseHelper.getRecos_buy().get(userPick) != null
                && Integer.parseInt(pairInfo) >= Integer.parseInt(DatabaseHelper.getRecos_buy().get(userPick).getPrice())) {
            // place order
            System.out.println("Order placed");
        }

        // Compare for stop loss OR sell price
        // Check if any pair is below or equal to stop loss value

        // If there is any potential transaction - emit userInfo and pairInfo to Transaction Bolt

        _collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
        _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("transaction"));
    }
}