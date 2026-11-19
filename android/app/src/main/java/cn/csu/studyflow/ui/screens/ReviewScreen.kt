package cn.csu.studyflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.csu.studyflow.data.model.Scene
import cn.csu.studyflow.ui.components.TagChip

/** ④ 复习入口 + 学习计划（同页双 Tab，对应 6 入口中的「复习入口」「学习计划」） */
@Composable
fun ReviewScreen(scene: Scene, sub: String, onSubChange: (String) -> Unit) {
    val tabs = listOf("review" to "复习入口", "plan" to "学习计划")
    val selectedIndex = tabs.indexOfFirst { it.first == sub }.coerceAtLeast(0)

    Column {
        TabRow(selectedTabIndex = selectedIndex) {
            tabs.forEachIndexed { i, (key, label) ->
                Tab(
                    selected = i == selectedIndex,
                    onClick = { onSubChange(key) },
                    text = { Text(label) },
                )
            }
        }
        if (sub == "plan") {
            PlanList(scene)
        } else {
            ReviewQueue(scene)
        }
    }
}

/** 今日复习队列：按掌握度升序取错题与薄弱知识点 */
@Composable
private fun ReviewQueue(scene: Scene) {
    val queue = scene.mistakes.filter { it.mastery < 0.7f }
        .sortedBy { it.mastery }
    val started = remember { mutableStateOf(setOf<String>()) }
    val pointNames = scene.points.associate { it.id to it.name }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = "今日复习队列 · ${queue.size} 项待复习",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "按掌握度从低到高排列；语音问答（步6）为后置待办，S3 起接入。",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        items(queue, key = { it.id }) { e ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = e.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TagChip("知识点 ${pointNames[e.pointId] ?: e.pointId}")
                        TagChip("掌握度 ${(e.mastery * 100).toInt()}%")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            started.value = if (e.id in started.value) started.value - e.id
                            else started.value + e.id
                        },
                    ) {
                        Text(
                            if (e.id in started.value) "复习中…（苏格拉底式 · 占位）"
                            else "开始复习（苏格拉底式 · 占位）"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanList(scene: Scene) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(scene.plan, key = { it.title }) { item ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        TagChip("截止 ${item.due}")
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { item.progress },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.padding(horizontal = 6.dp))
                        Text(
                            text = "${(item.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
