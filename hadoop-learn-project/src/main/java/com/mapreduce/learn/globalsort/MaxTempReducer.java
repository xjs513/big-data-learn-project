package com.mapreduce.learn.globalsort;/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/20 20:47
 * @Description:
 */

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MaxTempReducer extends Reducer<LongWritable, NullWritable, LongWritable, NullWritable> {
    /**
     * reduce
     */
    protected void reduce(LongWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        for (NullWritable iw:values) {
            context.write(key,iw);
        }
    }


//    protected void reduce(LongWritable key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
//        long max=Long.MIN_VALUE;
//        for (LongWritable iw:values) {
//            max=max>iw.get()?max:iw.get();
//        }
//        context.write(key,new LongWritable(max));
//    }

}
