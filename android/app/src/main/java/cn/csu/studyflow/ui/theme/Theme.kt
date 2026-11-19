package cn.csu.studyflow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** StudyFlow 主题（W2 骨架先使用 Material3 默认亮色，品牌色 S3 再定制） */
@Composable
fun StudyFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(), content = content)
}
