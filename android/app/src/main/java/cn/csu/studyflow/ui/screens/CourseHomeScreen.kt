package cn.csu.studyflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.csu.studyflow.data.model.Scene
import cn.csu.studyflow.ui.components.SectionCard
import cn.csu.studyflow.ui.components.TagChip

/** 课程模块 6 入口定义 */
private data class Entry(
    val label: String,
    val desc: String,
    val icon: ImageVector,
    val route: String,
)

private val entries = listOf(
    Entry("课程首页", "课程概览与进度", Icons.Filled.Home, "home"),
    Entry("章节导航", "三级组织 · 知识点关联", Icons.Filled.List, "chapters"),
    Entry("资料库", "课件 / 文献 / 批注 / 外文", Icons.Filled.Create, "library/materials"),
    Entry("错题本", "八字段记录 · 掌握度", Icons.Filled.CheckCircle, "library/mistakes"),
    Entry("复习入口", "今日复习队列", Icons.Filled.CheckCircle, "review/review"),
    Entry("学习计划", "计划与打卡", Icons.Filled.Create, "review/plan"),
)

/** ① 课程首页：承载 6 入口 + 继续学习 + 第二步占位 */
@Composable
fun CourseHomeScreen(scene: Scene, onOpen: (String) -> Unit) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "继续学习 · ${scene.courseName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "已掌握 ${scene.progress.mastered}/${scene.progress.total} 个知识点 · 连续学习 ${scene.progress.streakDays} 天",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TagChip("今日 ${scene.progress.todayPages}/${scene.progress.todayGoal} 页")
                        TagChip("可检查可修正")
                    }
                }
            }
        }
        item {
            SectionCard(title = "课程模块 · 6 入口") {
                // 固定 6 入口，两列排布（外层 LazyColumn 内不能嵌套 LazyGrid）
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entries.chunked(2).forEach { rowEntries ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            rowEntries.forEach { e ->
                                Card(
                                    onClick = { onOpen(e.route) },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Icon(
                                            e.icon,
                                            contentDescription = e.label,
                                            modifier = Modifier.size(22.dp),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = e.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = e.desc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                            if (rowEntries.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        item {
            // 课程模块「分两步」：第一步本周实现，第二步（关联模块）只留占位
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "关联模块（第二步 · 跨课程知识图谱）",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    TagChip("占位 · 后置 S3")
                }
            }
        }
    }
}
