package kasa.test.kafka.tomysql.demo1;

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:11
 * @Description: 配置参数类
 */

public class ConfigKeys {
    static String DRIVER_CLASS = "";
    static String SOURCE_DRIVER_URL = "";
    static String SOURCE_USER = "";
    static String SOURCE_PASSWORD = "";
    static String SOURCE_SQL = "";

    static String SINK_DRIVER_URL = "";
    static String SINK_USER = "";
    static String SINK_PASSWORD = "";
    static String SINK_SQL = "";

    static String SINK_DRIVER_URL(){
        return SINK_DRIVER_URL;
    }

    static String SINK_USER(){
        return SINK_USER;
    }

    static String SINK_PASSWORD(){
        return SINK_PASSWORD;
    }

    static String SINK_SQL(){
        return SINK_SQL;
    }

    static String DRIVER_CLASS(){
        return DRIVER_CLASS;
    }

    static String SOURCE_DRIVER_URL(){
        return SOURCE_DRIVER_URL;
    }

    static String SOURCE_USER(){
        return SOURCE_USER;
    }

    static String SOURCE_PASSWORD(){
        return SOURCE_PASSWORD;
    }

    static String SOURCE_SQL(){
        return SOURCE_SQL;
    }
}
