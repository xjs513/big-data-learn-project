package kasa.test.kafka.tomysql.demo1;


import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:14
 * @Description: 写入mysql的类
 */
public class JdbcWriter extends RichSinkFunction<Tuple2<String,String>> {
    private Connection connection;
    private PreparedStatement preparedStatement;
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // 加载JDBC驱动
        Class.forName(ConfigKeys.DRIVER_CLASS());
        // 获取数据库连接
        connection = DriverManager.getConnection(ConfigKeys.SINK_DRIVER_URL(),ConfigKeys.SINK_USER(),ConfigKeys.SINK_PASSWORD());//写入mysql数据库
        preparedStatement = connection.prepareStatement(ConfigKeys.SINK_SQL());//insert sql在配置文件中
        super.open(parameters);
    }

    @Override
    public void close() throws Exception {
        super.close();
        if(preparedStatement != null){
            preparedStatement.close();
        }
        if(connection != null){
            connection.close();
        }
        super.close();
    }

    @Override
    public void invoke(Tuple2<String,String> value, Context context) throws Exception {
        try {
            String name = value.f0;//获取JdbcReader发送过来的结果
            preparedStatement.setString(1,name);
            preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
