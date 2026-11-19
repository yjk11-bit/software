package cn.csu.studyflow.data.model

/**
 * W2 4.2 · 本地数据模型（骨架版）
 *
 * 口径：与 A 的《知识点数据模型 v1》契约对齐（W2 3.3）——
 * 六标签：课程 / 章节 / 知识点 / 难度 / 来源 / 版本；
 * 错题·掌握度记录八字段：题目 / 错误答案 / 错误类型 / 对应知识点 /
 * 难度 / 首次错因 / 复习次数 / 掌握状态；
 * 底层用知识点关联（前置关系），不用文件夹分组。
 * 「可检查可修正」范式：confidence < 0.8 一律标黄、可点回溯源。
 */

/** 六标签中的「难度」：1–5 星 */
typealias Difficulty = Int

/** 知识点：三核心数据对象之一 */
data class KnowledgePoint(
    val id: String,
    val name: String,
    val course: String,          // 标签①课程
    val chapterId: String,       // 标签②章节
    val difficulty: Difficulty,  // 标签④难度
    val source: String,          // 标签⑤来源
    val version: String,         // 标签⑥版本
    val summary: String,
    val sourceRef: String,       // 溯源：可点回页码 / 节点 / 段落
    val confidence: Float,       // < 0.8 标黄可修正
    val prerequisites: List<String> = emptyList(),  // 知识点关联：前置
    val relatedCourses: List<String> = emptyList(), // 知识点关联：跨课程
    val mastered: Boolean = false,
)

/** 章节：课程三级组织（课程—章节—知识点） */
data class Chapter(
    val id: String,
    val title: String,
    val order: Int,
    val pointIds: List<String>,
)

/** 题目：三核心数据对象之一 */
data class Question(
    val id: String,
    val pointId: String,
    val difficulty: Difficulty,
    val type: String,   // 题型
    val source: String, // 来源
    val stem: String,
)

/** 错题·掌握度记录：三核心数据对象之一（八字段按 3.3 契约） */
data class MistakeRecord(
    val id: String,
    val question: String,      // ①题目
    val wrongAnswer: String,   // ②错误答案
    val errorType: String,     // ③错误类型
    val pointId: String,       // ④对应知识点
    val difficulty: Difficulty,// ⑤难度
    val firstCause: String,    // ⑥首次错因
    val reviewCount: Int,      // ⑦复习次数
    val mastery: Float,        // ⑧掌握状态 0–1
)

/** 资料（D 的 5.1 场景样例四类：课件 / 文献 / 手写批注 / 外文片段） */
enum class MaterialType(val label: String) {
    COURSEWARE("课件"),
    LITERATURE("文献"),
    HANDWRITTEN("手写批注"),
    FOREIGN("外文片段"),
}

data class Material(
    val id: String,
    val title: String,
    val type: MaterialType,
    val pointIds: List<String>,
    val sourceRef: String,   // 溯源位置
    val version: String,
    val confidence: Float,   // 识别/结构化置信度，< 0.8 标黄
)

data class PlanItem(
    val title: String,
    val detail: String,
    val progress: Float,   // 0–1
    val due: String,       // 截止
)

/** 顶栏进度卡片 */
data class ProgressCard(
    val mastered: Int,
    val total: Int,
    val todayPages: Int,
    val todayGoal: Int,
    val streakDays: Int,
)

/** 场景（对应 demo 的 SCENES：计科 / 文学院） */
data class Scene(
    val id: String,
    val label: String,
    val courseName: String,
    val chapters: List<Chapter>,
    val points: List<KnowledgePoint>,
    val questions: List<Question>,
    val materials: List<Material>,
    val mistakes: List<MistakeRecord>,
    val plan: List<PlanItem>,
    val progress: ProgressCard,
)
