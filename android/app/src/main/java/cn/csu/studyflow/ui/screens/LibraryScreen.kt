package cn.csu.studyflow.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.csu.studyflow.data.model.Scene
import cn.csu.studyflow.ui.components.ConfidenceBadge
import cn.csu.studyflow.ui.components.SourceRefChip
import cn.csu.studyflow.ui.components.TagChip

/** ③ 资料库 + 错题本（同页双 Tab，对应 6 入口中的「资料库」「错题本」） */
@Composable
fun LibraryScreen(scene: Scene, sub: String, onSubChange: (String) -> Unit) {
    val tabs = listOf("materials" to "资料库", "mistakes" to "错题本")
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
        if (sub == "mistakes") {
            MistakeList(scene)
        } else {
            MaterialList(scene)
        }
    }
}

@Composable
private fun MaterialList(scene: Scene) {
    val correctedIds = remember { mutableStateOf(setOf<String>()) }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(scene.materials, key = { it.id }) { m ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = m.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        TagChip(m.type.label)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        m.pointIds.forEach { pid -> TagChip("知识点 $pid") }
                        TagChip("版本 ${m.version}")
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SourceRefChip(m.sourceRef)
                        ConfidenceBadge(m.confidence, m.id in correctedIds.value)
                        if (m.id !in correctedIds.value && m.confidence < 0.8f) {
                            Text(
                                text = "修正",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    correctedIds.value = correctedIds.value + m.id
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakeList(scene: Scene) {
    val pointNames = scene.points.associate { it.id to it.name }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(scene.mistakes, key = { it.id }) { e ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        text = e.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "错答：${e.wrongAnswer}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB3261E),
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TagChip("错因 · ${e.errorType}")
                        TagChip("知识点 ${pointNames[e.pointId] ?: e.pointId}")
                        TagChip("难度 ${"★".repeat(e.difficulty)}")
                        TagChip("复习 ${e.reviewCount} 次")
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "首次错因：${e.firstCause}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { e.mastery },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.padding(horizontal = 6.dp))
                        Text(
                            text = "掌握度 ${(e.mastery * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}
