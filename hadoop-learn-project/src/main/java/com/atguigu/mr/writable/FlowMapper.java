package com.atguigu.mr.writable;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    private Text outKey = new Text();
    private FlowBean outValue  = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // 读取一行数据
        // 1    13736230513	192.196.100.1	www.atguigu.com	2481	24681	200
        String line = value.toString();
        // 2. 切分数据
        String[] splits = line.split("\t");
        // 3. 封装 key
        outKey.set(splits[1]);
        // 4. 封装 value
        outValue.setUpFlow(Long.parseLong(splits[splits.length-3]));
        outValue.setDownFlow(Long.parseLong(splits[splits.length-2]));
        outValue.setSumFlow();
        // 5. 写出
        context.write(outKey, outValue);
    }
}
