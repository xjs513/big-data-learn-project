package com.atguigu.mr.inputformat;

import com.evente.mapreduce.WordCount;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;

import java.io.File;
import java.io.IOException;

public class WordCountDriver {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //读取配置文件
        Configuration conf = new Configuration();


        Job job = Job.getInstance(conf,"WordCountDriver");
        job.setJarByClass(WordCountDriver.class); //主类

        job.setMapperClass(WordCountMapper.class);
        //combine过程发生在map方法和reduce方法之间，它将中间结果进行了一次合并。
//        job.setCombinerClass(WordCountReducer.class);
        job.setReducerClass(WordCountReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // 设置适用 CombineTextInputFormat  默认是TextInputFormat
        // job.setInputFormatClass(CombineTextInputFormat.class);
        // CombineTextInputFormat.setMaxInputSplitSize(job, 900000);

        // 设置适用 NLineInputFormat
        // 文件大小一定  但是每行很小  交给一个 MapTask 处理不合适
        job.setInputFormatClass(NLineInputFormat.class);
        NLineInputFormat.setNumLinesPerSplit(job, 5);

        job.setNumReduceTasks(3);

        String outPath = "E:\\test_data\\output";

        Path out = new Path(outPath);
        org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job,new Path("E:\\test_data\\nlineinputformat"));
        org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,out);


        File file = new File(out.toString());

        if (file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                file1.delete();
            }
            file.delete();
        }

        System.exit(job.waitForCompletion(true) ? 0:1 );
    }
}
