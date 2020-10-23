package com.atguigu.mr.inputformat;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class KVMapper extends Mapper<Text, Text, Text, LongWritable> {

    LongWritable longWritable = new LongWritable(1);
    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        context.write(key, longWritable);
    }
}
