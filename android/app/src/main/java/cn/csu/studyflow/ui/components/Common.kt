package cn.csu.studyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.csu.studyflow.data.model.ProgressCard
import cn.csu.studyflow.data.model.Scene

/** 「可检查可修正」范式：低置信标黄 */
val LowConfidenceBg = Color(0xFFFFF3CD)
val LowConfidenceBorder = Color(0xFFE0B84C)

/** 每页顶栏：场景切换 + 进度卡片（执行手册 4.2②：每页顶栏保留场景切换与进度卡片） */
@Composable
fun StudyFlowTopBar(
    scenes: List<Scene>,
    currentScene: Scene,
    onSceneSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    onHandwritingTest: () -> Unit = {},
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "StudyFlow 智学伴侣",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = currentScene.courseName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(10.dp))
            Surface(
                onClick = onHandwritingTest,
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                Text(
                    text = "手写验证",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            scenes.forEach { s ->
                FilterChip(
                    selected = s.id == currentScene.id,
                    onClick = { onSceneSelect(s.id) },
                    label = { Text(s.label) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LinearProgressIndicator(
                progress = {
                    if (currentScene.progress.total == 0) 0f
                    else currentScene.progress.mastered.toFloat() / currentScene.progress.total
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "已掌握 ${currentScene.progress.mastered}/${currentScene.progress.total} · 今日 ${currentScene.progress.todayPages}/${currentScene.progress.todayGoal} 页",
                style = MaterialTheme.typography.labelSmall,
            )
        }
        HorizontalDivider()
    }
}

/** 小标签（六标签 / 溯源 / 题型等） */
@Composable
fun TagChip(text: String, color: Color = MaterialTheme.colorScheme.secondaryContainer) {
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

/** 低置信度徽标：< 0.8 标黄（可修正），已修正变绿 */
@Composable
fun ConfidenceBadge(confidence: Float, corrected: Boolean) {
    if (corrected) {
        TagChip("已人工修正", Color(0xFFD7EFD7))
    } else if (confidence < 0.8f) {
        TagChip("置信度 ${"%.2f".format(confidence)} · 待确认", LowConfidenceBg)
    } else {
        TagChip("置信度 ${"%.2f".format(confidence)}", Color(0xFFE8F0E8))
    }
}

/** 低置信卡片底色（「可检查可修正」：标黄 + 点回溯源） */
fun lowConfidenceModifier(confidence: Float, corrected: Boolean): Modifier =
    if (!corrected && confidence < 0.8f) {
        Modifier
            .background(LowConfidenceBg, RoundedCornerShape(12.dp))
            .border(1.dp, LowConfidenceBorder, RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

/** 溯源入口（可点回页码 / 节点 / 段落；骨架阶段为展示性占位） */
@Composable
fun SourceRefChip(ref: String, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(6.dp),
    ) {
        Text(
            text = "溯源 ▸ $ref",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

/** 章节内容块通用容器 */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    extra: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            extra()
        }
        content()
    }
}
