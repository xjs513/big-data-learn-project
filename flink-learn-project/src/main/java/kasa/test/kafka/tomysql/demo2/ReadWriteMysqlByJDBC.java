package kasa.test.kafka.tomysql.demo2;

/*
https://blog.csdn.net/ASN_forever/article/details/106023235?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.channel_param&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.channel_param
 * @Author: "songzhanliang"
 * @Date: 2020/11/5 21:31
 * @Description: 2 3 4 来自同一文章
 */

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

public class ReadWriteMysqlByJDBC {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment fbEnv = ExecutionEnvironment.getExecutionEnvironment();
        //需要与获取的字段一一对应，否则会取不到值
        TypeInformation[] fieldTypes = new TypeInformation[] {
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO};
        RowTypeInfo rowTypeInfo = new RowTypeInfo(fieldTypes);

        //读mysql
        DataSet<Row> dataSource = fbEnv.createInput(JDBCInputFormat.buildJDBCInputFormat()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://xxx/xxx")
                .setUsername("xxx")
                .setPassword("xxx")
                .setQuery("xxx")
                .setRowTypeInfo(rowTypeInfo)
                .finish());

        //写MySQL
        dataSource.output(JDBCOutputFormat.buildJDBCOutputFormat()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://xxx/xxx")
                .setUsername("xxx")
                .setPassword("xxx")
                .setQuery("xxx")
                .finish());

        fbEnv.execute();
    }
}

