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
        // useful snippet from the message
        // {"alphaCCYToken":"2","high":"1.29572","ccyPair":"GBP/USD","low":"1.29340","decimals":"5","ask":"1.29548","bid":"1.29530","type":"A","prevDayClosePrice":"1.29402","status":"D"}
        //2\GBP/USD\1.29530\1.29548\1.29572\1.29340\D\A\5\1.29402
        System.out.println("Intellatrade: AnalyzerBolt input tuple: " + pairInfo);
        // Lookup recommendation data
        try {
            Date todayDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            if (DatabaseHelper.getCurrentDate() == null || sdf.format(todayDate).equals(sdf.format(DatabaseHelper.getCurrentDate()))) {
                DatabaseHelper.refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userPick = "GBP USD";
//        if (DatabaseHelper.getRecos_buy().get(userPick) != null
//                && Float.parseFloat(pairInfo) >= Float.parseFloat(DatabaseHelper.getRecos_buy().get(userPick).getPrice())) {
        if(true) {
            // place order
            System.out.println("Place Order");
//            _collector.emit(tuple, new Values(Float.parseFloat(DatabaseHelper.getRecos_buy().get(userPick).getPrice())));
            _collector.emit(tuple, new Values("1.4"));
            _collector.ack(tuple);
        }


        if (DatabaseHelper.getRecos_sell().get(userPick) != null
                && Float.parseFloat(pairInfo) <= Float.parseFloat(DatabaseHelper.getRecos_sell().get(userPick).getPrice())) {
            // place order
            System.out.println("Place Order");
            _collector.emit(tuple, new Values(Float.parseFloat(DatabaseHelper.getRecos_sell().get(userPick).getPrice())));
            _collector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("transaction"));
    }
}