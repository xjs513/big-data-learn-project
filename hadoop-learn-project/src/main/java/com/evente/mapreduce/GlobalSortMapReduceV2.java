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
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class GlobalSortMapReduceV2 {
    private static final String OUT = "hdfs://kasa:9000/sort/output_v2/";
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

    public static class IteblogPartitioner extends Partitioner<IntWritable, IntWritable> {
        @Override
        public int getPartition(IntWritable key, IntWritable value, int numPartitions) {
            int keyInt = Integer.parseInt(key.toString());
            if (keyInt <= 80) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //设置环境变量HADOOP_USER_NAME，其值是root
        //在本机调试
        System.setProperty("HADOOP_USER_NAME", "kasa");
        //读取配置文件
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://kasa:9000");
        conf.set("yarn.resourcemanager.hostname","kasa");

        FileSystem fs = FileSystem.get(conf);

        Job job = Job.getInstance(conf,"GlobalSortMapReduceV2");
        job.setJarByClass(GlobalSortMapReduceV2.class); //主类

        job.setMapperClass(GlobalSortMapReduceV2.SimpleMapper.class);
        job.setReducerClass(GlobalSortMapReduceV2.SimpleReducer.class);

        job.setPartitionerClass(IteblogPartitioner.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(NullWritable.class);

        job.setNumReduceTasks(2);

        Path out = new Path(OUT);
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job,new Path("hdfs://kasa:9000/sort/input/"));
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,out);

        if(fs.exists(out)){
            fs.delete(out, true);
        }
        System.exit(job.waitForCompletion(true) ? 0:1 );
    }

}
