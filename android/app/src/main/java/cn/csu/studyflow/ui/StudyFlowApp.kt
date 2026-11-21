package cn.csu.studyflow.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cn.csu.studyflow.data.mock.MockData
import cn.csu.studyflow.ui.components.StudyFlowTopBar
import cn.csu.studyflow.ui.screens.ChapterNavScreen
import cn.csu.studyflow.ui.screens.CourseHomeScreen
import cn.csu.studyflow.ui.screens.HandwritingTestScreen
import cn.csu.studyflow.ui.screens.LibraryScreen
import cn.csu.studyflow.ui.screens.ReviewScreen

/**
 * W2 4.2 · 应用根：4 页骨架承载课程模块 6 入口
 *
 * 4 页：① 课程首页 ② 章节/知识点导航 ③ 资料库+错题本 ④ 复习入口+学习计划
 * 6 入口：课程首页 / 章节导航 / 资料库 / 错题本 / 复习入口 / 学习计划
 * 跨设备：手机单栏（底部 Tabbar）↔ 平板双栏（侧边 Rail），WindowSizeClass 断点。
 * 移动端壳：界面代码只依赖 data/model 契约与 bridge 边界，不退化为 PC 网页 LMS。
 */
private data class TopDest(val label: String, val icon: ImageVector, val route: String)

private val topDests = listOf(
    TopDest("课程首页", Icons.Filled.Home, "home"),
    TopDest("章节导航", Icons.Filled.List, "chapters"),
    TopDest("资料库+错题本", Icons.Filled.Create, "library"),
    TopDest("复习+计划", Icons.Filled.CheckCircle, "review"),
)

@Composable
fun StudyFlowApp(windowSizeClass: WindowSizeClass, startRoute: String = "home") {
    val navController = rememberNavController()
    var sceneId by rememberSaveable { mutableStateOf(MockData.scenes.first().id) }
    val scene = MockData.scenes.first { it.id == sceneId }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val wide = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Scaffold(
        topBar = {
            StudyFlowTopBar(
                scenes = MockData.scenes,
                currentScene = scene,
                onSceneSelect = { sceneId = it },
                onHandwritingTest = { navController.navigateSmart("handwriting") },
            )
        },
        bottomBar = {
            if (!wide) {
                NavigationBar {
                    topDests.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute.isOn(dest.route),
                            onClick = { navController.navigateSmart(dest.route) },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        },
    ) { inner ->
        if (wide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
            ) {
                NavigationRail(Modifier.fillMaxHeight()) {
                    topDests.forEach { dest ->
                        NavigationRailItem(
                            selected = currentRoute.isOn(dest.route),
                            onClick = { navController.navigateSmart(dest.route) },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
                VerticalDivider()
                StudyFlowNavHost(navController, scene, wide = true, startRoute = startRoute, Modifier.weight(1f))
            }
        } else {
            StudyFlowNavHost(navController, scene, wide = false, startRoute = startRoute, Modifier.padding(inner))
        }
    }
}

@Composable
private fun StudyFlowNavHost(
    navController: NavHostController,
    scene: cn.csu.studyflow.data.model.Scene,
    wide: Boolean,
    startRoute: String = "home",
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = startRoute, modifier = modifier) {
        composable("home") {
            CourseHomeScreen(scene = scene, onOpen = { navController.navigateSmart(it) })
        }
        composable("chapters") {
            ChapterNavScreen(scene = scene, wide = wide)
        }
        composable(
            route = "library/{sub}",
            arguments = listOf(navArgument("sub") { defaultValue = "materials" }),
        ) { entry ->
            LibraryScreen(
                scene = scene,
                sub = entry.arguments?.getString("sub") ?: "materials",
                onSubChange = { navController.navigateSmart("library/$it") },
            )
        }
        composable(
            route = "review/{sub}",
            arguments = listOf(navArgument("sub") { defaultValue = "review" }),
        ) { entry ->
            ReviewScreen(
                scene = scene,
                sub = entry.arguments?.getString("sub") ?: "review",
                onSubChange = { navController.navigateSmart("review/$it") },
            )
        }
        composable("handwriting") {
            HandwritingTestScreen()
        }
    }
}

private fun String?.isOn(base: String): Boolean =
    this != null && (this == base || this.startsWith("$base/"))

private fun NavHostController.navigateSmart(route: String) {
    // 底部导航传的是基路由，映射到带默认子 Tab 的真实路由
    val resolved = when (route) {
        "library" -> "library/materials"
        "review" -> "review/review"
        else -> route
    }
    navigate(resolved) { launchSingleTop = true }
}
