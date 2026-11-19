package cn.csu.studyflow.data.mock

import cn.csu.studyflow.data.model.Chapter
import cn.csu.studyflow.data.model.KnowledgePoint
import cn.csu.studyflow.data.model.Material
import cn.csu.studyflow.data.model.MaterialType
import cn.csu.studyflow.data.model.MistakeRecord
import cn.csu.studyflow.data.model.PlanItem
import cn.csu.studyflow.data.model.ProgressCard
import cn.csu.studyflow.data.model.Question
import cn.csu.studyflow.data.model.Scene

/**
 * W2 4.2 · 占位样例数据
 *
 * 两个场景（文学院 / 计科）形成异质对照；字段形状按 3.3 契约，
 * 等 A 契约定案后替换字段名/补全真实数据（D 的 5.1 场景样例灌入）。
 * 低置信样例（confidence < 0.8）故意保留，用于「可检查可修正」范式展示。
 */
object MockData {

    private val litChapters = listOf(
        Chapter("lit-ch1", "先秦文学", 1, listOf("lit-p1", "lit-p2", "lit-p3")),
        Chapter("lit-ch2", "汉魏六朝", 2, listOf("lit-p4", "lit-p5")),
        Chapter("lit-ch3", "唐宋文学", 3, listOf("lit-p6")),
    )

    private val litPoints = listOf(
        KnowledgePoint(
            id = "lit-p1", name = "《诗经》风雅颂", course = "中国古代文学", chapterId = "lit-ch1",
            difficulty = 2, source = "《诗经》通行本", version = "v1.0",
            summary = "风（民间歌谣）、雅（宫廷正声）、颂（宗庙祭祀）三体的内容与功能分野。",
            sourceRef = "教材 P.12 第 3 段", confidence = 0.93f, mastered = true,
        ),
        KnowledgePoint(
            id = "lit-p2", name = "赋比兴手法", course = "中国古代文学", chapterId = "lit-ch1",
            difficulty = 3, source = "朱熹《诗集传》", version = "v1.0",
            summary = "赋（铺陈）、比（比喻）、兴（起兴）三种表现手法及其辨析。",
            sourceRef = "教材 P.18 第 1 段", confidence = 0.87f, mastered = true,
            prerequisites = listOf("lit-p1"),
        ),
        KnowledgePoint(
            id = "lit-p3", name = "《楚辞》与屈原", course = "中国古代文学", chapterId = "lit-ch1",
            difficulty = 4, source = "《楚辞章句》", version = "v1.0",
            summary = "楚辞体式特征与《离骚》的抒情结构、香草美人传统。",
            sourceRef = "教材 P.31 第 2 段", confidence = 0.76f, mastered = false,
            prerequisites = listOf("lit-p2"),
        ),
        KnowledgePoint(
            id = "lit-p4", name = "《史记》叙事艺术", course = "中国古代文学", chapterId = "lit-ch2",
            difficulty = 4, source = "《史记》选注", version = "v1.0",
            summary = "纪传体通史的体例与「无韵之离骚」的文学性。",
            sourceRef = "教材 P.55 第 4 段", confidence = 0.90f, mastered = false,
            prerequisites = listOf("lit-p3"), relatedCourses = listOf("中国古代史"),
        ),
        KnowledgePoint(
            id = "lit-p5", name = "建安风骨", course = "中国古代文学", chapterId = "lit-ch2",
            difficulty = 3, source = "《文心雕龙·时序》", version = "v1.0",
            summary = "三曹七子的时代背景与慷慨悲凉的文学风貌。",
            sourceRef = "教材 P.67 第 1 段", confidence = 0.85f, mastered = false,
            prerequisites = listOf("lit-p4"),
        ),
        KnowledgePoint(
            id = "lit-p6", name = "唐诗气象（李杜）", course = "中国古代文学", chapterId = "lit-ch3",
            difficulty = 3, source = "《唐诗三百首》", version = "v1.0",
            summary = "李白浪漫飘逸与杜甫沉郁顿挫的风格对照。",
            sourceRef = "教材 P.102 第 5 段", confidence = 0.91f, mastered = false,
            prerequisites = listOf("lit-p5"),
        ),
    )

    private val litMaterials = listOf(
        Material("lit-m1", "课件：先秦文学总论.pptx", MaterialType.COURSEWARE,
            listOf("lit-p1", "lit-p2"), "课件 P.3", "v1.1", 0.94f),
        Material("lit-m2", "古籍引文：《诗集传》节选", MaterialType.LITERATURE,
            listOf("lit-p2"), "影印本 第 8 页", "v1.0", 0.89f),
        Material("lit-m3", "书边手写批注（课堂听记）", MaterialType.HANDWRITTEN,
            listOf("lit-p3"), "笔记本 P.7 左栏", "v0.9", 0.71f),
        Material("lit-m4", "外文片段：Owen, Anthology, ch.1", MaterialType.FOREIGN,
            listOf("lit-p3"), "英文教材 p.23", "v1.0", 0.83f),
    )

    private val litMistakes = listOf(
        MistakeRecord("lit-e1", "「赋比兴」中「兴」的判别", "把「兴」答成「比」",
            "概念混淆", "lit-p2", 3, "起兴与比喻的触发机制未分清", 3, 0.62f),
        MistakeRecord("lit-e2", "《离骚》香草美人传统内涵", "只答字面意象",
            "要点遗漏", "lit-p3", 4, "未联系「美政」理想作答", 2, 0.45f),
        MistakeRecord("lit-e3", "李杜风格对比论述", "罗列生平未比较风格",
            "答题框架缺失", "lit-p6", 3, "缺少「同中见异」的分析框架", 1, 0.3f),
    )

    private val litPlan = listOf(
        PlanItem("复习「赋比兴」错题", "重做 2 道辨析题 + 回看教材 P.18", 0.4f, "今晚"),
        PlanItem("通读《楚辞》章节", "教材 P.31–45，整理思维导图", 0.1f, "11-21"),
        PlanItem("李杜风格对照卡", "制作对比卡片 5 张", 0f, "11-22"),
    )

    private val litScene = Scene(
        id = "lit", label = "文学院 · 中国古代文学", courseName = "中国古代文学",
        chapters = litChapters, points = litPoints,
        questions = listOf(
            Question("lit-q1", "lit-p2", 3, "简答", "期中卷", "简述「赋比兴」的区别并各举一例。"),
            Question("lit-q2", "lit-p3", 4, "论述", "课后题", "结合《离骚》分析香草美人的象征体系。"),
        ),
        materials = litMaterials, mistakes = litMistakes, plan = litPlan,
        progress = ProgressCard(mastered = 2, total = 6, todayPages = 4, todayGoal = 10, streakDays = 3),
    )

    private val csChapters = listOf(
        Chapter("cs-ch1", "线性表", 1, listOf("cs-p1", "cs-p2")),
        Chapter("cs-ch2", "栈与队列", 2, listOf("cs-p3", "cs-p4")),
        Chapter("cs-ch3", "树", 3, listOf("cs-p5")),
    )

    private val csPoints = listOf(
        KnowledgePoint(
            id = "cs-p1", name = "线性表与顺序存储", course = "数据结构", chapterId = "cs-ch1",
            difficulty = 2, source = "《数据结构（C 语言版）》", version = "v1.0",
            summary = "顺序表随机存取与插入删除 O(n) 的代价。",
            sourceRef = "教材 P.18 图 2.1", confidence = 0.95f, mastered = true,
        ),
        KnowledgePoint(
            id = "cs-p2", name = "单链表操作", course = "数据结构", chapterId = "cs-ch1",
            difficulty = 3, source = "课件 week2", version = "v1.1",
            summary = "头插/尾插、删除后继节点的指针操作要点。",
            sourceRef = "课件 P.9", confidence = 0.88f, mastered = true,
            prerequisites = listOf("cs-p1"),
        ),
        KnowledgePoint(
            id = "cs-p3", name = "栈的应用：表达式求值", course = "数据结构", chapterId = "cs-ch2",
            difficulty = 4, source = "实验指导书", version = "v1.0",
            summary = "中缀转后缀 + 运算符优先级栈实现。",
            sourceRef = "实验二 任务 3", confidence = 0.79f, mastered = false,
            prerequisites = listOf("cs-p2"),
        ),
        KnowledgePoint(
            id = "cs-p4", name = "循环队列与假溢出", course = "数据结构", chapterId = "cs-ch2",
            difficulty = 3, source = "《数据结构（C 语言版）》", version = "v1.0",
            summary = "取模实现环形缓冲，区分队空/队满的三种方式。",
            sourceRef = "教材 P.64 第 2 段", confidence = 0.92f, mastered = false,
            prerequisites = listOf("cs-p3"),
        ),
        KnowledgePoint(
            id = "cs-p5", name = "二叉树遍历", course = "数据结构", chapterId = "cs-ch3",
            difficulty = 4, source = "课件 week5", version = "v1.2",
            summary = "前/中/后序遍历的递归与非递归实现。",
            sourceRef = "课件 P.17", confidence = 0.86f, mastered = false,
            prerequisites = listOf("cs-p4"),
        ),
    )

    private val csMaterials = listOf(
        Material("cs-m1", "课件：线性表.pptx", MaterialType.COURSEWARE,
            listOf("cs-p1", "cs-p2"), "课件 P.5", "v1.2", 0.96f),
        Material("cs-m2", "代码截图：循环队列实现", MaterialType.HANDWRITTEN,
            listOf("cs-p4"), "笔记本 P.11", "v0.9", 0.74f),
        Material("cs-m3", "外文片段：CLRS ch.10", MaterialType.FOREIGN,
            listOf("cs-p3", "cs-p4"), "英文教材 p.232", "v1.0", 0.85f),
    )

    private val csMistakes = listOf(
        MistakeRecord("cs-e1", "循环队列判空/判满条件", "写成 front==rear 判断队满",
            "边界条件遗漏", "cs-p4", 3, "未记住「牺牲一个单元」约定", 4, 0.58f),
        MistakeRecord("cs-e2", "中缀转后缀时括号处理", "遇到 '(' 直接弹出",
            "优先级规则记错", "cs-p3", 4, "栈顶 '(' 的优先级特判漏掉", 2, 0.5f),
    )

    private val csPlan = listOf(
        PlanItem("循环队列错题重做", "重做 2 道判空判满题", 0.6f, "今晚"),
        PlanItem("二叉树遍历非递归实现", "手写三种遍历 + 调试", 0.2f, "11-21"),
    )

    private val csScene = Scene(
        id = "cs", label = "计科 · 数据结构", courseName = "数据结构",
        chapters = csChapters, points = csPoints,
        questions = listOf(
            Question("cs-q1", "cs-p4", 3, "编程", "作业三", "实现循环队列的入队/出队并处理假溢出。"),
        ),
        materials = csMaterials, mistakes = csMistakes, plan = csPlan,
        progress = ProgressCard(mastered = 2, total = 5, todayPages = 6, todayGoal = 12, streakDays = 4),
    )

    val scenes: List<Scene> = listOf(litScene, csScene)
}
