package kasa.test.kafka.tomysql.demo3;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;

/*
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:39
 * @Description: 方式三：通过DDL读写mysql
 */
public class ReadWriteMysqlByDDL {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment streamEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(streamEnv,fsSettings);
        String sourceTable ="CREATE TABLE sourceTable (\n" +
                "    FTableName VARCHAR,\n" +
                "    FECName VARCHAR\n" +
                ") WITH (\n" +
                "    'connector.type' = 'jdbc', -- 使用 jdbc connector\n" +
                "    'connector.url' = 'jdbc:mysql://xxx/xxx', -- jdbc url\n" +
                "    'connector.table' = 'xxx', -- 表名\n" +
                "    'connector.username' = 'xxx', -- 用户名\n" +
                "    'connector.password' = 'xxx', -- 密码\n" +
                "    'connector.write.flush.max-rows' = '1' -- 默认5000条，为了演示改为1条\n" +
                ")";
        tableEnvironment.sqlUpdate(sourceTable);
        String sinkTable ="CREATE TABLE sinkTable (\n" +
                "    FID VARCHAR,\n"+
                "    FRoomName VARCHAR\n" +
                ") WITH (\n" +
                "    'connector.type' = 'jdbc', -- 使用 jdbc connector\n" +
                "    'connector.url' = 'jdbc:mysql://xxx/xxx', -- jdbc url\n" +
                "    'connector.table' = 'xxx', -- 表名\n" +
                "    'connector.username' = 'xxx', -- 用户名\n" +
                "    'connector.password' = 'xxx, -- 密码\n" +
                "    'connector.write.flush.max-rows' = '1' -- 默认5000条，为了演示改为100条\n" +
                ")";

        tableEnvironment.sqlUpdate(sinkTable);
        String query = "SELECT FTableName as tableName,FECName as ecName FROM sourceTable";
        Table table = tableEnvironment.sqlQuery(query);
        table.filter("tableName === 'xxx'").select("'1',ecName").insertInto("sinkTable");

        streamEnv.execute();
    }
}
