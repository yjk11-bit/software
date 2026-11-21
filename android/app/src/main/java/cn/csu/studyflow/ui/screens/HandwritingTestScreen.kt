package cn.csu.studyflow.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * W2 4.3 · 平板手写三块候选的可跑小验证
 *
 * 块① 笔迹采集：Canvas 自绘（pointerInput + Path）
 * 块② 低延迟：三种模式可切换实测——
 *   0 原始点（逐点折线）/ 1 历史点+贝塞尔（历史点全量采样+二次贝塞尔平滑，W1 方案①）
 *   / 2 预测描点（在①基础上叠加线性外推预测段，W1 方案②备选）
 * 块③ 跨设备断点：WindowSizeClass 实测见 app 整体布局（手机 Tabbar ↔ 平板 Rail）。
 *
 * 模拟器鼠标无压感：压感/丢笔/笔类型需真机平板复核（S3），页内已标注。
 */
private data class Stats(
    val strokes: Int = 0,
    val points: Int = 0,
    val lastStrokeMs: Long = 0,
    val avgBatchMs: Long = 0,
    val tool: String = "-",
    val pressure: Float = 0f,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HandwritingTestScreen(modifier: Modifier = Modifier) {
    var mode by rememberSaveable { mutableIntStateOf(1) }
    val finished = remember { mutableStateListOf<List<Offset>>() }
    val active = remember { mutableStateListOf<Offset>() }
    var stats by remember { mutableStateOf(Stats()) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("0 原始点") })
            FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("1 历史点+贝塞尔") })
            FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("2 预测描点") })
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { if (finished.isNotEmpty()) finished.removeAt(finished.size - 1) }) {
                Text("撤销")
            }
            OutlinedButton(onClick = { finished.clear(); active.clear() }) {
                Text("清屏")
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = "笔画 ${stats.strokes} · 点 ${stats.points} · 上笔 ${stats.lastStrokeMs}ms · 批间隔 ${stats.avgBatchMs}ms · 工具 ${stats.tool} · 压感 ${"%.2f".format(stats.pressure)}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(4.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .pointerInput(mode) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        active.clear()
                        active.add(down.position)
                        var press = down.pressure
                        var tool = down.type
                        val t0 = System.currentTimeMillis()
                        var batches = 0L
                        while (true) {
                            val ev = awaitPointerEvent()
                            val change = ev.changes.firstOrNull { it.id == down.id } ?: break
                            if (mode >= 1) {
                                // 历史点全量采样：不丢帧间点（低延迟方案①的核心）
                                change.historical.forEach { h -> active.add(h.position) }
                            }
                            active.add(change.position)
                            press = change.pressure
                            tool = change.type
                            batches += 1
                            if (!change.pressed) break
                        }
                        val dur = System.currentTimeMillis() - t0
                        val pts = active.toList()
                        active.clear()
                        finished.add(pts)
                        stats = Stats(
                            strokes = finished.size,
                            points = finished.sumOf { it.size },
                            lastStrokeMs = dur,
                            avgBatchMs = if (batches > 0) dur / batches else 0,
                            tool = tool.toString(),
                            pressure = press,
                        )
                    }
                },
        ) {
            val ink = Color(0xFF1A1A2E)
            val strokeStyle = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            val smoothStrokeStyle = strokeStyle

            finished.forEach { pts ->
                drawPath(buildPath(pts, mode), ink, style = if (mode == 0) strokeStyle else smoothStrokeStyle)
                if (mode == 0) {
                    // 原始点模式：把采样点画出来，直观看到点密度
                    pts.forEach { p -> drawCircle(ink.copy(alpha = 0.5f), radius = 2.dp.toPx(), center = p) }
                }
            }
            if (active.isNotEmpty()) {
                drawPath(buildPath(active.toList(), mode), ink, style = smoothStrokeStyle)
                if (mode == 2 && active.size >= 2) {
                    // 预测描点：按最后两点矢量外推一段，作为预测笔迹（演示性，生产需真实时间戳）
                    val last = active[active.size - 1]
                    val prev = active[active.size - 2]
                    val predicted = Offset(last.x + (last.x - prev.x), last.y + (last.y - prev.y))
                    drawLine(
                        color = ink.copy(alpha = 0.35f),
                        start = last,
                        end = predicted,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
        Text(
            text = "验证页（W2 4.3）：模拟器=鼠标无压感；手写笔压感/丢笔/工具类型待真机平板复核（S3）。平板上画布自动占满双栏主区。",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/** 按模式构造路径：0 原始折线；1/2 二次贝塞尔平滑（过中点） */
private fun buildPath(pts: List<Offset>, mode: Int): Path {
    val p = Path()
    if (pts.isEmpty()) return p
    p.moveTo(pts[0].x, pts[0].y)
    if (mode == 0) {
        for (i in 1 until pts.size) p.lineTo(pts[i].x, pts[i].y)
        return p
    }
    if (pts.size == 1) return p
    for (i in 1 until pts.size - 1) {
        val mid = Offset((pts[i].x + pts[i + 1].x) / 2f, (pts[i].y + pts[i + 1].y) / 2f)
        p.quadraticBezierTo(pts[i].x, pts[i].y, mid.x, mid.y)
    }
    p.lineTo(pts.last().x, pts.last().y)
    return p
}
