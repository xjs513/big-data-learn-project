package com.evente.mapreduce;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * MR 全局排序
 */
public class GlobalSortMapReduce {

    private static String HOST_NAME = "dev201";
    private static int port = 8020;

    private static final String IN = "hdfs://" + HOST_NAME + ":" + port + "/sort/input/";
    private static final String OUT = "hdfs://" + HOST_NAME + ":" + port + "/sort/output_2/";

    static class SimpleMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            IntWritable intWritable = new IntWritable(Integer.parseInt(value.toString()));
            context.write(intWritable, intWritable);
        }
    }

    static class SimpleReducer extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {
        @Override
        protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            for (IntWritable value : values)
                context.write(value, NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception {
        //设置环境变量HADOOP_USER_NAME，其值是root
        //在本机调试
        System.setProperty("HADOOP_USER_NAME", "root");
        //读取配置文件
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://" + HOST_NAME + ":" + port);
        conf.set("yarn.resourcemanager.hostname",HOST_NAME);

        FileSystem fs = FileSystem.get(conf);

        Job job = Job.getInstance(conf,"GlobalSortMapReduce");
        job.setJarByClass(GlobalSortMapReduce.class); //主类

        job.setMapperClass(GlobalSortMapReduce.SimpleMapper.class);
        //combine过程发生在map方法和reduce方法之间，它将中间结果进行了一次合并。
//        job.setCombinerClass(WordCount.Reduce.class);
        job.setReducerClass(GlobalSortMapReduce.SimpleReducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(2);

        Path in = new Path(IN);
        Path out = new Path(OUT);
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job,in);
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,out);

        if(fs.exists(out)){
            fs.delete(out, true);
        }
        System.exit(job.waitForCompletion(true) ? 0:1 );
    }

}
