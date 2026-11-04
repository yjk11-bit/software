/* ============================================================
   adapters.js · 能力适配器（全部为 Mock）
   负责人：A（计算机科学与技术 · 后端+算法+多模态）
   ------------------------------------------------------------
   W1：只定义「接口形状」，让 UI 能跑通闭环。
   W3：技术 Spike 结论（端侧 vs 云端 ASR/OCR）出来后，
       逐个把 Mock 换成真实实现，UI 层（app.js）不改。
   TODO(A, W3) 标记处即集成点。
   ============================================================ */
function MockDelay(ms){
  return new Promise(function(resolve){ setTimeout(resolve, ms); });
}

/* 语音 → 文字
   TODO(A, W3): 按 Spike 结论接入端侧轻量 ASR 或云端 ASR。
   决策依据：字错率 CER / 延迟 / 耗电 / 断网表现（见 W3 实验方案）。*/
var MockASR = {
  transcribe: function(audioRef, fallbackText, fallbackConf){
    return MockDelay(420).then(function(){
      return { text: fallbackText, confidence: fallbackConf,
               source: audioRef, engine: "mock-ondevice", latencyMs: 420 };
    });
  }
};

/* 图像 / 手写 → 文字
   TODO(A, W3): 代码截图、公式、古籍竖排、手写批注需分别评测 OCR 方案；
   低置信度结果必须回传 UI 供人工修正（对应「可检查可修正」）。*/
var MockOCR = {
  recognize: function(imageRef, fallbackText, fallbackConf){
    return MockDelay(360).then(function(){
      return { text: fallbackText, confidence: fallbackConf,
               source: imageRef, engine: "mock-ondevice", latencyMs: 360 };
    });
  }
};

/* 多模态片段 → 结构化笔记
   TODO(A, W4): 换成 大模型 API + 智能体编排，按置信度排序并标出待核对项。
   注意：必须保留每段的来源引用，供「溯源」交互使用。*/
var MockLLM = {
  structure: function(segments, noteTemplate){
    return MockDelay(520).then(function(){
      return { title: noteTemplate.title, sections: noteTemplate.sections,
               segmentCount: segments.length, engine: "mock-llm" };
    });
  }
};

/* 个人知识库检索
   TODO(A, W4): 换成 RAG 向量库（按课程/问题/项目组织），返回命中依据与相关度。
   当前为关键词匹配模拟，仅用于跑通界面。*/
var MockRAG = {
  search: function(query, corpus){
    return MockDelay(300).then(function(){
      var q = (query || "").trim();
      if(!q) return corpus.slice();
      return corpus.filter(function(it){
        return (it.title + it.meta + it.hit + it.tag).indexOf(q) >= 0;
      });
    });
  }
};

/* 跨设备任务接续
   TODO(B + A, W9): 接入 OPPO 互联开放能力（设备发现/跨设备传输）与超级碰一碰。
   约束：必须由用户显式确认才触发，不自动同步（控制权留给人）。*/
var MockCrossDevice = {
  handoff: function(taskState){
    return MockDelay(380).then(function(){
      return { ok:true, via:"OPPO 互联开放能力 / 超级碰一碰 (Mock)",
               task: taskState, timestamp: Date.now() };
    });
  }
};
