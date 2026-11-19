package cn.csu.studyflow.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.csu.studyflow.data.model.KnowledgePoint
import cn.csu.studyflow.data.model.Scene
import cn.csu.studyflow.ui.components.ConfidenceBadge
import cn.csu.studyflow.ui.components.SourceRefChip
import cn.csu.studyflow.ui.components.TagChip
import cn.csu.studyflow.ui.components.lowConfidenceModifier

/**
 * ② 章节/知识点导航：课程—章节—知识点三级组织
 * 按 3.3 契约用知识点关联（前置关系箭头），不用文件夹树；
 * 平板（expanded）双栏：左章节列表 / 右知识点详情。
 */
@Composable
fun ChapterNavScreen(scene: Scene, wide: Boolean) {
    val points = scene.points
    var selectedChapterId by rememberSaveable { mutableStateOf(scene.chapters.firstOrNull()?.id ?: "") }

    // 「可检查可修正」：已人工修正的知识点 id 集合（骨架阶段仅本页状态）
    val correctedIds = remember { mutableStateOf(setOf<String>()) }

    if (wide) {
        Row(Modifier.fillMaxWidth()) {
            ChapterList(
                scene = scene,
                selectedId = selectedChapterId,
                onSelect = { selectedChapterId = it },
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(Modifier.fillMaxHeight())
            val chapter = scene.chapters.firstOrNull { it.id == selectedChapterId }
            PointDetailColumn(
                chapterTitle = chapter?.title ?: "",
                points = points.filter { it.chapterId == selectedChapterId },
                correctedIds = correctedIds.value,
                onCorrect = { id ->
                    correctedIds.value = correctedIds.value + id
                },
                modifier = Modifier.weight(1.4f),
            )
        }
    } else {
        // 手机单栏：章节标题 + 其下知识点，全部展开
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            scene.chapters.forEach { ch ->
                item(key = "h-${ch.id}") {
                    Text(
                        text = "第 ${ch.order} 章 · ${ch.title}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                items(points.filter { it.chapterId == ch.id }, key = { it.id }) { p ->
                    KnowledgePointCard(
                        point = p,
                        corrected = p.id in correctedIds.value,
                        onCorrect = { correctedIds.value = correctedIds.value + p.id },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterList(
    scene: Scene,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(8.dp)) {
        items(scene.chapters, key = { it.id }) { ch ->
            val selected = ch.id == selectedId
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(ch.id) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "第 ${ch.order} 章 · ${ch.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun PointDetailColumn(
    chapterTitle: String,
    points: List<KnowledgePoint>,
    correctedIds: Set<String>,
    onCorrect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                text = chapterTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        items(points, key = { it.id }) { p ->
            KnowledgePointCard(p, p.id in correctedIds, onCorrect = { onCorrect(p.id) })
        }
    }
}

/** 知识点卡片：六标签 + 溯源 + 前置关联 + 低置信标黄 */
@Composable
private fun KnowledgePointCard(
    point: KnowledgePoint,
    corrected: Boolean,
    onCorrect: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = point.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                if (point.mastered) {
                    TagChip("✓ 已掌握", Color(0xFFD7EFD7))
                } else {
                    TagChip("学习中", MaterialTheme.colorScheme.tertiaryContainer)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = point.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TagChip("难度 ${"★".repeat(point.difficulty)}")
                TagChip("来源 ${point.source}")
                TagChip("版本 ${point.version}")
            }
            Spacer(Modifier.height(6.dp))
            if (point.prerequisites.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    point.prerequisites.forEach { pid ->
                        TagChip("← 前置 $pid")
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SourceRefChip(point.sourceRef)
                ConfidenceBadge(point.confidence, corrected)
                if (!corrected && point.confidence < 0.8f) {
                    Text(
                        text = "修正",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onCorrect() },
                    )
                }
            }
            if (point.relatedCourses.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    point.relatedCourses.forEach { course ->
                        TagChip("关联课程 $course", Color(0xFFE3E0F5))
                    }
                }
            }
        }
    }
}
