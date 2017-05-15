package storm.kafka.project.util;


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelper {

    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    //    private static final String DB_CONNECTION = "jdbc:oracle:thin:@localhost:1521:MKYONG";
    private static final String DB_CONNECTION = "jdbc:mysql://mysql-instance.cxf62rwafx5t.us-west-1.rds.amazonaws.com:3306/MySqlDB";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "intellatrade";

    private static Map<String,RecommendationData> recos_sell = new HashMap<String, RecommendationData>();
    private static Map<String,RecommendationData> recos_buy = new HashMap<String, RecommendationData>();

    public static Map<String, RecommendationData> getRecos_sell() {
        return recos_sell;
    }

    public static Map<String, RecommendationData> getRecos_buy() {
        return recos_buy;
    }

    private static Date currentDate;

    public static Date getCurrentDate() {
        return currentDate;
    }

    public static void main(String args[]) {
        try {
            DatabaseHelper.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void refresh() throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;

        String selectTableSQL = "SELECT * from daily_recommendations WHERE datetime >= CURDATE()\n" +
                "  AND datetime < CURDATE() + INTERVAL 2 DAY";

        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();
            System.out.println(selectTableSQL);
            // execute select SQL stetement
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {

                String datetime = rs.getString("datetime");
                String day = rs.getString("day");
                String type = rs.getString("type");
                String symbol = rs.getString("symbol");
                String price = rs.getString("price");
                String stoploss = rs.getString("stoploss");
                String takeprofit = rs.getString("takeprofit");

                RecommendationData recommendationData = new RecommendationData();
                recommendationData.setDatetime(datetime);
                recommendationData.setDay(day);
                recommendationData.setSymbol(symbol);
                recommendationData.setType(type);
                recommendationData.setPrice(price);
                recommendationData.setStoploss(stoploss);
                recommendationData.setTakeprofit(takeprofit);
                if("buy".equalsIgnoreCase(type)) {
                    recos_buy.put(symbol, recommendationData);
                } else {
                    recos_sell.put(symbol, recommendationData);
                }

                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
                try {
                    currentDate = inputFormat.parse(datetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                StringBuilder sb = new StringBuilder();
                sb.append(" datetime : " + datetime);
                sb.append(" day : " + day);
                sb.append(" type : " + type);
                sb.append(" symbol : " + symbol);
                sb.append(" price : " + price);
                sb.append(" stoploss : " + stoploss);
                sb.append(" takeprofit : " + takeprofit);
                System.out.println(sb.toString());
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }

        }
    }

    private static Connection dbConnection = null;


    private static Connection getDBConnection() {

        if(dbConnection != null) {
            return dbConnection;
        }

        try {

            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());

        }

        try {

            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
                    DB_PASSWORD);
            return dbConnection;

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

        return dbConnection;

    }

    public static class RecommendationData {
        String datetime;
        String day;
        String type;
        String symbol;
        String price;
        String stoploss;
        String takeprofit;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getStoploss() {
            return stoploss;
        }

        public void setStoploss(String stoploss) {
            this.stoploss = stoploss;
        }

        public String getTakeprofit() {
            return takeprofit;
        }

        public void setTakeprofit(String takeprofit) {
            this.takeprofit = takeprofit;
        }
    }

}