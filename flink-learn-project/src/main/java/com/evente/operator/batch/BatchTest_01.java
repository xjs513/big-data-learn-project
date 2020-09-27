package com.evente.operator.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class BatchTest_01 {

    static String path = "E:\\IdeaProjects\\big-data-learn-project\\data\\input\\words.txt";

    public static void main(String[] args) {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> lines = env.readTextFile(path);

        DataSet<Tuple2<String,Integer>> words = lines.flatMap(
            new FlatMapFunction<String, Tuple2<String,Integer>>() {
                @Override
                public void flatMap(String line, Collector<Tuple2<String,Integer>> collector) throws Exception {
                    String[] splits = line.split(" ");
                    for (String s : splits) {
                        collector.collect(new Tuple2(s, 1));
                    }
                }
            }
        );

        DataSet<Tuple2<String, Integer>> result = words.groupBy(0).sum(1);

        try {
            result.print();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
