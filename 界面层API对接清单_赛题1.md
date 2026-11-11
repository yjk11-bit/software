# 界面层真实 API 对接清单（W1 · B 交付物 4.3）

> 依据：《第一周（W1）工作安排 · 执行手册》4.3 ｜ 负责人：B（移动前端+UX）｜ 与 A 的 3.3「adapters.js 替换边界」成对
> 原则：`demo/js/adapters.js` 是唯一边界——只要 5 个方法的入参/返回形状不变，Mock 换成真实实现时界面代码（index.html / app.css / app.js）一行不改。

## 0. 验证状态（验收口径：闭环能跑完、场景切换生效、清单与 adapters 一一对应）

- [x] 一键演示闭环四阶段代码路径已核：`tour()` → 采集 3 种模态（录音/拍照/手写）→ 生成结构化笔记 → 知识库检索 → 「用于当前问题」→ 接续页 3 次手动确认，左侧进度高亮逐阶段同步（[app.js](demo/js/app.js) 268–298 行）
- [x] 场景切换：计科（红黑树）↔ 文学院（文心雕龙）切换后，采集内容/笔记/知识库/当前问题全部换源（[data.js](demo/js/data.js) 两套 SCENES 样本）
- [x] 低置信度（<0.8）标黄提示人工核对；笔记要点可编辑、可溯源；接续必须用户显式确认（控制权留给人）
- [ ] 浏览器实际点按复核（demo/index.html 已在浏览器打开，点击右上角两个按钮即可）

## 1. 映射总表：界面动作 → 适配器方法

| # | 界面动作（页面/元素） | 适配器方法 | 入参形状 | 界面实际用到的返回字段 | 目前 Mock 返回 | 真实实现依赖（周次/负责人） |
|---|---|---|---|---|---|---|
| 1 | 采集页·「录音」按钮 | `MockASR.transcribe` | `audioRef`(字符串·录音来源标签), `fallbackText`, `fallbackConf` | `text`（识别文字）、`confidence`（<0.8 标黄）、`engine`/`latencyMs`（日志） | 420ms 后返回兜底文本，`engine:"mock-ondevice"` | W3 技术 Spike 定端侧 vs 云端 ASR；`audioRef` 需升级为真实录音引用（文件/流） |
| 2 | 采集页·「拍照」按钮 | `MockOCR.recognize` | `imageRef`(字符串), `fallbackText`, `fallbackConf` | `text`、`confidence`、`engine`/`latencyMs` | 360ms 后返回兜底文本 | W3 Spike：代码截图/公式/古籍竖排分别评测；`imageRef` 需升级为真实图片引用 |
| 3 | 采集页·「手写」按钮 | `MockOCR.recognize`（手写走同一 OCR 通道） | 同上 | 同上 | 同上（`CAP_ENGINE` 显示「OCR(手写)」） | W3 Spike 手写识别方案；低置信度必须回传 UI 供修正 |
| 4 | 采集页·「生成结构化笔记」 | `MockLLM.structure` | `segments`(已采集片段数组), `noteTemplate`(场景笔记模板，来自 data.js) | `title`、`sections[].h`（小节标题）、`sections[].points[]`（要点，可编辑）、`sections[].src`（溯源行，展开显示） | 原样返回模板内容，`engine:"mock-llm"` | W4 换大模型 API+智能体编排；**必须保留每段来源引用**（`sections[].src` 支撑溯源交互） |
| 5 | 知识库页·「检索」按钮/回车 | `MockRAG.search` | `query`(问题文本), `corpus`(场景语料数组) | 命中数组每项：`title`、`meta`、`hit`、`tag` | 关键词包含匹配，300ms 返回 | W4 换向量库（按课程/章节/知识点组织），返回命中依据与相关度 |
| 6 | 知识库页·「用于当前问题」 | 无（纯界面状态） | — | — | — | 仅建立 `state.task` 并跳转接续页 |
| 7 | 接续页·「确认接力到下一设备」 | `MockCrossDevice.handoff` | `taskState`（`{ task: 任务标题, step: 1–3 }`） | `ok`、`via`（展示"OPPO 互联开放能力/超级碰一碰"）、`task` | 380ms 返回成功 | W9 接 OPPO 互联开放能力（设备发现/跨设备传输）+ 超级碰一碰；**须用户显式确认，不自动同步** |

## 2. 与 A（3.3）对齐的边界结论与待核对分歧点

**边界结论**（与 A 3.3 ⑤ 口径一致）：W3 Spike 后由 A 扩 MockLLM（苏格拉底/出题/作业分级）并替换真实实现，B 界面不改。

**分歧点清单**（4.3 ⑤ 要求当天在群里对齐，周四 11-12 核对）：

| # | 分歧点 | B 的建议 | 待 A 确认 |
|---|---|---|---|
| F1 | ASR/OCR 入参目前是字符串标签，真实实现需要音频/图片的实际引用 | W3 前把入参升级为对象 `{ ref, mime, meta }`，字符串可作 `meta` 兼容 | 是否 W3 Spike 一并定入参形状 v2 |
| F2 | `LLM.structure` 真实实现由 AI 生成笔记，但 `sections[].src` 溯源字段必须保留 | 溯源字段进契约，不能省 | 大模型编排时每段带来源引用的成本 |
| F3 | `RAG.search` 目前返回语料原数组，真实实现应带相关度分数 | 界面只用 `title/meta/hit/tag` 4 字段，**加字段不破坏界面**（如 `score`） | 返回形状 v2 是否加 `score` 与命中依据 |
| F4 | `CrossDevice.handoff` 的 `taskState` 目前只有 `{task, step}` | W9 接 OPPO SDK 时需含设备信息/任务负载，届时升级入参 | W9 前与 OPPO 文档对齐 |

## 3. 界面层将来要接的真实能力清单（按周次）

| 周次 | 界面位置 | 要接的真实能力 | 来源 |
|---|---|---|---|
| W3 | 采集页（录音/拍照/手写） | 端侧或云端 ASR / OCR（含手写识别） | A 技术 Spike 结论 |
| W4 | 采集页→笔记页 | 大模型结构化（多模态→笔记，保留溯源） | A 大模型 API+编排 |
| W4 | 知识库页 | 真实向量库检索（课程/章节/知识点组织） | A RAG 向量库 |
| W9 | 接续页 | OPPO 互联开放能力（设备发现/跨设备传输）+ 超级碰一碰（NFC） | 官方可支持能力清单（附件2） |
| W9–W10 | 采集页 | 真实录音/拍照权限与硬件调用（MediaRecorder / CameraX 或 Photo Picker） | B 实现（Android 端） |

## 4. 平板/多端需调整的界面组件（4.2 ③ 对照，详细取舍见《平板手写触控交互方案》）

现 demo 为手机单栏：`phone` 容器 + 底部 Tabbar + 4 个单列 `screen`。
平板需调整：① 采集页 `cap-grid` 三按钮可升为侧边工具条；② 笔记页 `note-wrap` 分栏（左笔记/右溯源+反馈）；③ 知识库 `search-row + kb-list` 双栏（左检索右命中列表）；④ 接续页 `steps` 横排 3 卡片；⑤ Tabbar 平板可换侧边导航。本周不实现，W3 多端适配时落地。

*本清单按《第一周工作安排·执行手册》4.3 验收标准编写：闭环可跑、场景切换生效、清单与 adapters.js 边界一一对应。*
