package kasa.test.kafka.tomysql.demo3;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:33
 * @Description:
 */

@Slf4j
public class MysqlSource extends RichSourceFunction<SourceVo> {

    private Connection connection = null;
    private PreparedStatement ps = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName("com.mysql.jdbc.Driver");//加载数据库驱动
        connection = DriverManager.getConnection("jdbc:mysql://xxx/xxx", "xxx", "xxx");//获取连接
        ps = connection.prepareStatement("xxx");
    }

    @Override
    public void run(SourceContext<SourceVo> ctx) throws Exception {
        try {
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                SourceVo vo = new SourceVo();
                // vo.setxxx(resultSet.getString("xxx"));
                ctx.collect(vo);
            }
        } catch (Exception e) {
            log.error("runException:{}", e);
        }
    }

    @Override
    public void cancel() {
        try {
            super.close();
            if (connection != null) {
                connection.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            log.error("runException:{}", e);
        }
    }
}

