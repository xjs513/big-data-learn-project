package com.mapreduce.learn.topn_1;/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/20 22:05
 * @Description:
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.TreeMap;

public class TopN {

    public static final int K = 3;

//    public static class MyIntWritable extends IntWritable {
//
//        @Override
//        public int compareTo(IntWritable o) {
//            return -super.compareTo(o);  //重写IntWritable排序方法，默认是升序 ，
//        }
//    }


    public static class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        TreeMap<Integer, String> map = new TreeMap<>();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//            super.map(key, value, context);
            String[] arr = value.toString().split(" ");

            int score = Integer.parseInt(arr[1]);

            map.put(score, arr[0]);

            if (map.size() > K) {
                map.remove(map.firstKey()); //移除排在最前面的entry
            }


        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
//            super.cleanup(context);
            for (Integer key : map.keySet()) {

                context.write(new Text(map.get(key)), new IntWritable(key)); //map执行结束时将k,v写入context

            }
        }
    }

    public static class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        TreeMap<Integer, String> map = new TreeMap<>();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
//            super.reduce(key, values, context);

            map.put(values.iterator().next().get(), key.toString()); //排序
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
//            super.cleanup(context);
            for (Integer key : map.keySet()) {

                context.write(new Text(map.get(key)), new IntWritable(key));

            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf);

        job.setJarByClass(TopN.class);

        job.setMapperClass(MyMapper.class);

        job.setReducerClass(MyReducer.class);

        job.setOutputKeyClass(Text.class);

        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path("E:\\IdeaProjects\\big-data-learn-project\\data\\input_topn"));

        FileOutputFormat.setOutputPath(job, new Path("E:\\IdeaProjects\\big-data-learn-project\\data\\output_topn"));

        // exit(arg) arg 非0表示jvm异常终止
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}