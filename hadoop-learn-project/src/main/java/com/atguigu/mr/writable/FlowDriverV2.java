package com.atguigu.mr.writable;

import com.atguigu.mr.inputformat.KVDriver;
import com.atguigu.mr.inputformat.KVMapper;
import com.atguigu.mr.inputformat.KVReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueLineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.File;
import java.io.IOException;

public class FlowDriverV2 {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();

        // configuration.set(KeyValueLineRecordReader.KEY_VALUE_SEPERATOR, " ");

        Job job = Job.getInstance(configuration, "FlowDriverV2");

        job.setJarByClass(FlowDriverV2.class);

        job.setMapperClass(FlowMapperV2.class);
        job.setReducerClass(FlowReducerV2.class);

        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);


        String inputPath = "E:\\test_data\\flow_input";

        String outputPath = "E:\\test_data\\flow_output";

        // 清理输出目录
        File file = new File(outputPath);
        if (file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                file1.delete();
            }
            file.delete();
        }

        FileInputFormat.setInputPaths(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));

        System.exit(job.waitForCompletion(true) ? 0:1 );
    }
}
