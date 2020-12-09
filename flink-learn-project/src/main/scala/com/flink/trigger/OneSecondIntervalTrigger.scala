package com.flink.trigger

import com.flink.UtilCaseClasses.SensorReading
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.windowing.triggers.{Trigger, TriggerResult}
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

/*
  * @author : Kasa 
  * @date : 2020/12/9 15:47  
  * @descripthon :
  */
class OneSecondIntervalTrigger
  extends Trigger[SensorReading, TimeWindow] {

  override def onElement(
    r:SensorReading,
    timestamp: Long,
    window: TimeWindow,
    ctx: Trigger.TriggerContext
  ): TriggerResult = {
    val firstSeen: ValueState[Boolean] = ctx
      .getPartitionedState(
        new ValueStateDescriptor[Boolean](
          "firstSeen", classOf[Boolean]
        )
      )

    if (!firstSeen.value()) {
//      val t = ctx.getCurrentWatermark
//      + (1000 - (ctx.getCurrentWatermark % 1000))
      val t = ctx.getCurrentWatermark - 1000
      ctx.registerEventTimeTimer(t)
      ctx.registerEventTimeTimer(window.getEnd)
      firstSeen.update(true)
    }

    TriggerResult.CONTINUE
  }

  override def onEventTime(
    timestamp: Long,
    window: TimeWindow,
    ctx: Trigger.TriggerContext
  ): TriggerResult = {
    if (timestamp == window.getEnd) {
      TriggerResult.FIRE_AND_PURGE
    } else {
//      val t = ctx.getCurrentWatermark
//      + (1000 - (ctx.getCurrentWatermark % 1000))
      val t = ctx.getCurrentWatermark - 1000
      if (t < window.getEnd) {
        // ctx.registerEventTimeTimer(t)
        ctx.deleteEventTimeTimer(t)
      }
      TriggerResult.FIRE
    }
  }

  override def onProcessingTime(
     timestamp: Long,
     window: TimeWindow,
     ctx: Trigger.TriggerContext
   ): TriggerResult = {
    TriggerResult.CONTINUE
  }

  override def clear(
    window: TimeWindow,
    ctx: Trigger.TriggerContext
  ): Unit = {
    val firstSeen: ValueState[Boolean] = ctx
      .getPartitionedState(
        new ValueStateDescriptor[Boolean](
          "firstSeen", classOf[Boolean]
        )
      )
    firstSeen.clear()
  }
}
