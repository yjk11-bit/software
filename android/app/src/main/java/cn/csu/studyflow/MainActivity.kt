package cn.csu.studyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import cn.csu.studyflow.ui.StudyFlowApp
import cn.csu.studyflow.ui.theme.StudyFlowTheme

/**
 * StudyFlow 智学伴侣 · 移动端壳（W2 4.2：4 页 UI 骨架承载课程模块 6 入口）
 *
 * 边界：界面代码只依赖 data/model 契约与 bridge 边界；
 * A 的 adapters 换成真实实现（W3 起）时界面代码不改。
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 测试入口：adb shell am start -n cn.csu.studyflow/.MainActivity --ez handwriting_test true
        val startHandwriting = intent?.getBooleanExtra("handwriting_test", false) == true
        setContent {
            StudyFlowTheme {
                StudyFlowApp(
                    windowSizeClass = calculateWindowSizeClass(this),
                    startRoute = if (startHandwriting) "handwriting" else "home",
                )
            }
        }
    }
}
