package com.mapreduce.learn.globalsort;/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/20 20:46
 * @Description:
 */

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MaxTempMapper extends Mapper<LongWritable, Text, LongWritable, NullWritable>{

    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {

     /*   String line=value.toString();
        String arr[]=line.split(" ");*/
        context.write(new LongWritable(Long.parseLong(value.toString())), NullWritable.get());
    }

}
