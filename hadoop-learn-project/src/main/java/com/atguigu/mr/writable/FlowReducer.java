package com.atguigu.mr.writable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

    private FlowBean resultBean = new FlowBean();
    private Long totalUpFlow = 0L;
    private Long totalDownFlow = 0L;

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        for (FlowBean value : values) {
            totalUpFlow += value.getUpFlow();
            totalDownFlow += value.getDownFlow();
        }
        resultBean.setUpFlow(totalUpFlow);
        resultBean.setDownFlow(totalDownFlow);
        resultBean.setSumFlow();
        context.write(key, resultBean);
    }
}
