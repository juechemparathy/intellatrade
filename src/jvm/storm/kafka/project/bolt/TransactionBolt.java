package storm.kafka.project.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by jue on 5/7/17.
 */
public class TransactionBolt extends BaseRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        String pairInfo = tuple.getString(0);
        System.out.println("Intellatrade: TransactionBolt input tuple: " + pairInfo);
        // Extract userinfo and transaction info
        // execute the transaction Asynchronously
//      _collector.emit(tuple, new Values(tuple.getString(0)));
        JSONObject pairJson  = new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            pairJson = (JSONObject) parser.parse(pairInfo);
        } catch(Exception e) {
            e.printStackTrace();
        }
        String userPick = pairJson.get("pair").toString();
        String ask = pairJson.get("ask").toString();
        String bid = pairJson.get("bid").toString();
        // Deal Request

        // http://ec2-52-53-232-188.us-west-1.compute.amazonaws.com:8080/gaincapital-rest-tradingservice/dealrequest?product=GBP/USD&buysell=B&amount=10000&rate=1.4
        String url = "http://ec2-54-193-121-31.us-west-1.compute.amazonaws.com:8080/gaincapital-rest-tradingservice/dealrequest?userName=Reshma&product=" +
                userPick +
                "&buysell=B&amount=10000&" +
                "rate="+bid;
        try {
            makeTransaction(url,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("traderesult"));
    }


    private void makeTransaction(String url, boolean isMock) throws IOException {
        if(isMock) {
            System.out.println("TransactionDone: "+ url);
            return;
        }
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

// add request header
        request.addHeader("User-Agent", "Storm Transaction");
        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
    }
}