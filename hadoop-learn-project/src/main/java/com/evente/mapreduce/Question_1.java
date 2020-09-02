package com.evente.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Question_1 {

    private static final String OUT = "hdfs://kasa:9000/wordcount/output/";
    public static class Map extends Mapper<LongWritable,Text,LongWritable, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] words = value.toString().split(",");

            LongWritable longWritable = new LongWritable(Integer.parseInt(words[1].replaceAll("-", "")));
            Text text = new Text(words[0]);

            context.write(longWritable, text);
        }
    }

    public static class Reduce extends Reducer<LongWritable, Text,LongWritable,Text> {
        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            Long count = 0L;
            for(Text value : values){
                count += value.get();
            }
            context.write(key,new LongWritable(count));
        }
    }

}
