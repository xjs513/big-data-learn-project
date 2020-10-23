package com.atguigu.mr.writable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

public class FlowDriver {

//    private static String HOST_NAME = "dev201";
    private static String HOST_NAME = "kasa";
    private static int port = 9000;
    // hadoop fs -mkdir -p /flow/input
    // hadoop fs -put /kasa_data/test_data/phone_data.txt /flow/input
    // hadoop fs -cat /flow/output/*
    private static final String IN = "hdfs://" + HOST_NAME + ":" + port + "/flow/input/";
    private static final String OUT = "hdfs://" + HOST_NAME + ":" + port + "/flow/output/";

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //设置环境变量HADOOP_USER_NAME，其值是root
        //在本机调试
//        System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("HADOOP_USER_NAME", "kasa");
        //读取配置文件
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://" + HOST_NAME + ":" + port);
        conf.set("yarn.resourcemanager.hostname",HOST_NAME);

        FileSystem fs = FileSystem.get(conf);

        Job job = Job.getInstance(conf,"FlowDriver");
        job.setJarByClass(FlowDriver.class); //主类

        job.setMapperClass(FlowMapper.class);
        //combine过程发生在map方法和reduce方法之间，它将中间结果进行了一次合并。
//        job.setCombinerClass(WordCount.Reduce.class);
        job.setReducerClass(FlowReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

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
