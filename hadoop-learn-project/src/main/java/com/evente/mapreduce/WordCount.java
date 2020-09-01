package com.evente.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class WordCount {
    private static final String OUT = "hdfs://kasa:9000/wordcount/output/";
    public static class Map extends Mapper<LongWritable,Text,Text,LongWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] words = value.toString().split(" ");
            for(String word : words){
                context.write(new Text(word),new LongWritable(1L));
            }
        }
    }

    public static class Reduce extends Reducer<Text,LongWritable,Text,LongWritable> {
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context)
                throws IOException, InterruptedException {
            Long count = 0L;
            for(LongWritable value : values){
                count += value.get();
            }
            context.write(key,new LongWritable(count));
        }
    }

    public static void main(String[] args) throws Exception{

        //设置环境变量HADOOP_USER_NAME，其值是root
        //在本机调试
        System.setProperty("HADOOP_USER_NAME", "kasa");
        //读取配置文件
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://kasa:9000");
        conf.set("yarn.resourcemanager.hostname","kasa");

        FileSystem fs = FileSystem.get(conf);

        Job job = Job.getInstance(conf,"Demo");
        job.setJarByClass(WordCount.class); //主类

        job.setMapperClass(Map.class);
        //combine过程发生在map方法和reduce方法之间，它将中间结果进行了一次合并。
        job.setCombinerClass(Reduce.class);
        job.setReducerClass(Reduce.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.setNumReduceTasks(2);

        Path out = new Path(OUT);
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job,new Path("hdfs://kasa:9000/wordcount/input/"));
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,out);

        if(fs.exists(out)){
            fs.delete(out, true);
        }
        System.exit(job.waitForCompletion(true) ? 0:1 );
    }
}
