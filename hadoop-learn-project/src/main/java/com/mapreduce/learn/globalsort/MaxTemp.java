package com.mapreduce.learn.globalsort;/*
 * @Author: "songzhanliang"
 * @Date: 2020/10/20 20:48
 * @Description:
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;

public class MaxTemp {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", "file:///");

        Job job = Job.getInstance(conf);

        // 设置job的各种属性
        job.setJobName("MaxTempApp");                        //作业名称
        job.setJarByClass(MaxTemp.class);                 //搜索类
        // job.setInputFormatClass(SequenceFileInputFormat.class); //设置输入格式

        // 添加输入路径
        FileInputFormat.addInputPath(job,new Path("E:\\IdeaProjects\\big-data-learn-project\\data\\input_sort"));
        // 设置输出路径
        FileOutputFormat.setOutputPath(job,new Path("E:\\IdeaProjects\\big-data-learn-project\\data\\output_sort"));
        job.setMapperClass(MaxTempMapper.class);             //mapper类
        job.setReducerClass(MaxTempReducer.class);           //reducer类
        job.setNumReduceTasks(3);                       //reduce个数

        job.setMapOutputKeyClass(LongWritable.class);           //
        job.setMapOutputValueClass(NullWritable.class);  //

        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(NullWritable.class);     //


        // 设置全排序分区类S
        job.setPartitionerClass(TotalOrderPartitioner.class);
        // 创建随机采样器
        /**
         * freq:key被选中的概率
         * numSampales 抽取样本的总数
         * maxSplitsSampled 最大采样切片数
         */
        InputSampler.Sampler<LongWritable,LongWritable> sampler = new InputSampler.RandomSampler<>(0.1,10,1);

        TotalOrderPartitioner.setPartitionFile(job.getConfiguration(),new Path("file:///E:\\IdeaProjects\\big-data-learn-project\\data\\first_part"));
        // 将sample数据写入分区文件中
        InputSampler.writePartitionFile(job,sampler);

        job.waitForCompletion(true);
    }
}
