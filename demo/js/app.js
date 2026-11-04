/* ============================================================
   app.js · 状态机 / 渲染 / 事件 / 一键演示
   负责人：B（移动前端+UX），队长负责闭环串联
   依赖：data.js（SCENES）、adapters.js（Mock*）
   ------------------------------------------------------------
   状态只存在内存里：刷新即重置（W1 脚手架的明确边界）。
   ============================================================ */
(function(){
"use strict";

var $  = function(s){ return document.querySelector(s); };
var $$ = function(s){ return Array.prototype.slice.call(document.querySelectorAll(s)); };
var esc = function(s){ return String(s).replace(/[&<>"']/g, function(c){
  return {"&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;"}[c]; }); };

var CAP_LABEL = { audio:"录音", image:"拍照", hand:"手写" };
var CAP_ENGINE = { audio:"ASR", image:"OCR", hand:"OCR(手写)" };

var state = {
  scene: "cs",
  stage: "capture",
  recording: false,
  seconds: 0,
  timer: null,
  segments: [],
  note: null,
  kbResults: [],
  handoffIndex: 0,
  task: null,
  busy: false,
  touring: false
};

/* ---------- 工具 ---------- */
function log(msg){
  var el = $("#log");
  var t = new Date().toLocaleTimeString("zh-CN",{hour12:false});
  el.textContent += "\n[" + t + "] " + msg;
  el.scrollTop = el.scrollHeight;
}
function toast(msg){
  var old = document.querySelector(".toast");
  if(old) old.remove();
  var d = document.createElement("div");
  d.className = "toast"; d.textContent = msg;
  document.body.appendChild(d);
  setTimeout(function(){ if(d.parentNode) d.remove(); }, 2100);
}
function scene(){ return SCENES[state.scene]; }
function sleep(ms){ return new Promise(function(r){ setTimeout(r, ms); }); }

/* ---------- 左侧闭环高亮 ---------- */
function setStage(stage){
  state.stage = stage;
  var map = { capture:"采集", note:"理解", knowledge:"积累 / 复用", handoff:"反馈 / 接续" };
  var liMap = { capture:"capture", note:"note", knowledge:"knowledge", handoff:"handoff" };
  $$("#loopList li").forEach(function(li){
    li.classList.toggle("on", li.getAttribute("data-step") === liMap[stage] || li.getAttribute("data-step") === stage);
  });
  $("#iStage").textContent = map[stage] || stage;
  $$(".screen").forEach(function(s){ s.classList.toggle("on", s.id === "s-" + stage); });
  $$(".tab").forEach(function(b){ b.classList.toggle("on", b.getAttribute("data-tab") === stage); });
}

/* ---------- 采集页 ---------- */
function initWave(){
  var w = $("#wave");
  w.innerHTML = "";
  for(var i=0;i<24;i++){ var s=document.createElement("i"); w.appendChild(s); }
}
function renderStream(){
  var el = $("#stream");
  if(!state.segments.length){
    el.innerHTML = '<div class="empty">暂无采集内容</div>';
    $("#structureBtn").disabled = true;
    return;
  }
  el.innerHTML = state.segments.map(function(s){
    var low = s.confidence < 0.8;
    return '<div class="seg">'
      + '<span class="seg-src">' + esc(CAP_LABEL[s.type]) + " · " + esc(s.source) + "</span>"
      + "<p>" + esc(s.text) + "</p>"
      + '<span class="seg-conf">置信度 ' + s.confidence.toFixed(2)
      + (low ? " · 建议人工核对" : "") + " · " + esc(CAP_ENGINE[s.type]) + "</span></div>";
  }).join("");
  el.scrollTop = el.scrollHeight;
  $("#structureBtn").disabled = false;
}
function startRec(){
  if(state.recording) return;
  state.recording = true;
  $("#wave").classList.add("live");
  $("#sessionTitle").textContent = scene().session;
  $("#recHint").textContent = "采集中… 点击「录音 / 拍照 / 手写」投喂样本";
  state.timer = setInterval(function(){
    state.seconds++;
    var m = String(Math.floor(state.seconds/60)).padStart(2,"0");
    var s = String(state.seconds%60).padStart(2,"0");
    $("#sessionClock").textContent = m + ":" + s;
  },1000);
  log("开始采集 · 场景=" + scene().label);
}
function addCap(type){
  var sample = scene().capture.find(function(c){ return c.type === type; });
  if(!sample) return;
  if(state.segments.some(function(s){ return s.source === sample.source; })){
    toast("该样本已采集过了"); return;
  }
  var engine = type === "audio" ? MockASR : MockOCR;
  var ref = sample.source.replace(/\s.*$/,"");
  var p = type === "audio"
    ? engine.transcribe(ref, sample.text, sample.confidence)
    : engine.recognize(ref, sample.text, sample.confidence);
  return p.then(function(res){
    state.segments.push({ type: type, source: sample.source,
      text: res.text, confidence: res.confidence });
    renderStream();
    log(CAP_ENGINE[type] + " → " + sample.source + "（" + res.engine + ", " + res.latencyMs + "ms）");
  });
}
/* ---------- 笔记页 ---------- */
function renderNote(){
  var el = $("#noteWrap");
  if(!state.note){
    el.innerHTML = '<div class="empty">尚未生成笔记，请先在「采集」页生成</div>';
    $("#feedback").textContent = "生成笔记后，这里会给出「疑问整理 / 重点标记」建议。";
    return;
  }
  var n = state.note;
  var html = '<div class="note-title">' + esc(n.title) + "</div>";
  n.sections.forEach(function(sec, si){
    html += '<div class="note-sec"><h4>' + esc(sec.h) + "</h4>";
    sec.points.forEach(function(pt, pi){
      html += '<div class="note-point">'
        + '<span class="txt" contenteditable="true" data-si="' + si + '" data-pi="' + pi + '">' + esc(pt) + "</span>"
        + '<span class="note-btns"><button class="mini" data-act="ok" data-si="' + si + '" data-pi="' + pi + '">核对</button>'
        + '<button class="mini" data-act="src" data-si="' + si + '">溯源</button></span></div>';
    });
    html += '<div class="src-line" data-src="' + si + '">来源：' + esc(sec.src) + "</div>";
    html += "</div>";
  });
  el.innerHTML = html;

  el.querySelectorAll('[contenteditable]').forEach(function(t){
    t.addEventListener("blur", function(){
      var si = +t.getAttribute("data-si"), pi = +t.getAttribute("data-pi");
      state.note.sections[si].points[pi] = t.textContent;
      log("用户修正笔记要点 [" + si + "." + pi + "] → " + t.textContent.slice(0,24));
      toast("已修正并保留（控制权留给人）");
    });
  });
  el.querySelectorAll('[data-act="src"]').forEach(function(b){
    b.addEventListener("click", function(){
      var line = el.querySelector('[data-src="' + b.getAttribute("data-si") + '"]');
      line.classList.toggle("show");
    });
  });
  el.querySelectorAll('[data-act="ok"]').forEach(function(b){
    b.addEventListener("click", function(){ toast("已标记为核对通过"); log("用户确认该要点"); });
  });

  var lows = state.segments.filter(function(s){ return s.confidence < 0.8; }).length;
  $("#feedback").innerHTML = "已整理 <b>" + n.segmentsLength + "</b> 段采集内容，生成 <b>"
    + n.sections.length + "</b> 组要点。其中 <b>" + lows + "</b> 段置信度偏低，已标黄建议人工核对。"
    + "<br>每点可点击直接修改，可展开「溯源」回到原始录音 / 图像位置。";
}
function structureNote(){
  if(!state.segments.length || state.busy) return;
  state.busy = true;
  var btn = $("#structureBtn"); btn.disabled = true; btn.textContent = "AI 整理中…";
  log("MockLLM.structure · 片段数=" + state.segments.length);
  return MockLLM.structure(state.segments, scene().note).then(function(res){
    res.segmentsLength = state.segments.length;
    state.note = res;
    renderNote();
    btn.disabled = false; btn.textContent = "生成结构化笔记";
    state.busy = false;
    log("结构化笔记完成 · " + res.title);
    toast("笔记已生成，可检查 / 可修正");
  });
}
/* ---------- 知识库页 ---------- */
function renderKB(){
  var el = $("#kbList");
  $("#kbCount").textContent = state.kbResults.length;
  if(!state.kbResults.length){
    el.innerHTML = '<div class="empty">输入问题后点「检索」</div>';
    return;
  }
  el.innerHTML = state.kbResults.map(function(it, i){
    return '<div class="kb-item"><div class="kb-t">' + esc(it.title) + "</div>"
      + '<div class="kb-m">' + esc(it.meta) + " · #" + esc(it.tag) + "</div>"
      + '<div class="kb-hit">' + esc(it.hit) + "</div>"
      + '<button class="kb-use" data-i="' + i + '">用于当前问题</button></div>';
  }).join("");
  el.querySelectorAll(".kb-use").forEach(function(b){
    b.addEventListener("click", function(){
      var it = state.kbResults[+b.getAttribute("data-i")];
      state.task = { title: scene().question, hit: it.title, tag: it.tag };
      state.handoffIndex = 0;
      renderHandoff();
      log("任务建立 · 关联资料=" + it.title);
      toast("已关联到当前问题，去「接续」页确认");
      setStage("handoff");
    });
  });
}
function doSearch(q){
  $("#kbQuestion").textContent = "当前问题：" + (q || scene().question);
  log("MockRAG.search · q=" + (q || scene().question));
  return MockRAG.search(q || scene().question, scene().kb).then(function(res){
    state.kbResults = res.length ? res : scene().kb;
    renderKB();
    log("命中 " + state.kbResults.length + " 条资料");
  });
}
/* ---------- 接续页 ---------- */
var STEP_TITLES = [
  ["手机 · 随身采集", "课堂录音 / 拍照 / 手写，任务已创建"],
  ["平板 · 手写续写", "补批注、修正识别误差"],
  ["PC · 深度创作", "导出报告 / 论文，保留依据与过程"]
];
function renderHandoff(){
  var t = state.task;
  $("#taskTitle").textContent = t ? "当前任务：" + t.title : "当前任务：未建立";
  $("#taskQuestion").textContent = t ? "已关联资料：" + t.hit + "（#" + t.tag + "）" : "先在知识库里点「用于当前问题」";
  $("#iTask").textContent = t ? "已建立" : "未建立";
  $("#iHandoff").textContent = state.handoffIndex + " / 3";
  $("#handoffSteps").innerHTML = STEP_TITLES.map(function(s, i){
    return '<div class="step ' + (i < state.handoffIndex ? "done" : "") + '">'
      + "<i>" + (i < state.handoffIndex ? "✓" : (i+1)) + "</i>"
      + "<div>" + esc(s[0]) + "<small>" + esc(s[1]) + "</small></div></div>";
  }).join("");
  $("#handoffBtn").disabled = !t || state.handoffIndex >= 3;
  $("#handoffBtn").textContent = state.handoffIndex >= 3 ? "已接力到下一设备" : "确认接力到下一设备";
}
function doHandoff(){
  if(!state.task || state.handoffIndex >= 3 || state.busy) return;
  state.busy = true;
  return MockCrossDevice.handoff({ task: state.task.title, step: state.handoffIndex+1 }).then(function(res){
    state.handoffIndex++;
    renderHandoff();
    state.busy = false;
    log("跨设备接力确认 · " + res.via + " · step " + state.handoffIndex);
    toast("已确认接力（用户显式确认，非自动同步）");
  });
}
/* ---------- 场景切换 ---------- */
function switchScene(){
  state.scene = state.scene === "cs" ? "lit" : "cs";
  $("#majorBtn").textContent = "场景：" + scene().label;
  $("#iMajor").textContent = scene().label;
  $("#courseChip").textContent = scene().course;
  $("#sessionTitle").textContent = "课堂记录 · 未开始";
  $("#sessionClock").textContent = "00:00";
  $("#recHint").textContent = "点击下方按钮模拟多模态采集";
  state.recording = false; state.seconds = 0;
  if(state.timer) clearInterval(state.timer);
  $("#wave").classList.remove("live");
  state.segments = []; state.note = null; state.kbResults = [];
  state.task = null; state.handoffIndex = 0;
  renderStream(); renderNote(); renderKB(); renderHandoff();
  $("#kbInput").value = ""; $("#kbQuestion").textContent = "";
  log("切换场景 → " + scene().label + "（异质材料样本）");
  toast("已切换到「" + scene().label + "」样本");
}
/* ---------- 一键演示 ---------- */
async function tour(){
  if(state.touring) return;
  state.touring = true;
  log("=== 一键演示闭环 开始 ===");
  state.segments = []; state.note = null; state.kbResults = [];
  state.task = null; state.handoffIndex = 0;
  renderStream(); renderNote(); renderKB(); renderHandoff();
  setStage("capture");
  if(!state.recording) startRec();
  for(var i=0;i<scene().capture.length;i++){
    await addCap(scene().capture[i].type);
    await sleep(160);
  }
  await structureNote();
  setStage("note");
  await sleep(700);
  setStage("knowledge");
  await doSearch("");
  await sleep(400);
  var first = document.querySelector(".kb-use");
  if(first) first.click();
  await sleep(500);
  for(var k=0;k<3;k++){
    if($("#handoffBtn").disabled) break;
    await doHandoff();
    await sleep(300);
  }
  state.touring = false;
  log("=== 一键演示闭环 完成 ===");
  toast("闭环演示完成：采集→理解→积累→复用→反馈");
}
/* ---------- 绑定 ---------- */
function bind(){
  $$(".tab").forEach(function(b){
    b.addEventListener("click", function(){ setStage(b.getAttribute("data-tab")); });
  });
  $$(".cap").forEach(function(b){
    b.addEventListener("click", function(){
      if(!state.recording) startRec();
      addCap(b.getAttribute("data-cap"));
    });
  });
  $("#structureBtn").addEventListener("click", structureNote);
  $("#kbBtn").addEventListener("click", function(){ doSearch($("#kbInput").value.trim()); });
  $("#kbInput").addEventListener("keydown", function(e){
    if(e.key === "Enter") doSearch($("#kbInput").value.trim());
  });
  $("#handoffBtn").addEventListener("click", doHandoff);
  $("#majorBtn").addEventListener("click", switchScene);
  $("#tourBtn").addEventListener("click", tour);
  $$("#loopList li").forEach(function(li){
    li.addEventListener("click", function(){ setStage(li.getAttribute("data-step")); });
  });
}
/* ---------- 启动 ---------- */
function init(){
  initWave();
  bind();
  setStage("capture");
  $("#majorBtn").textContent = "场景：" + scene().label;
  $("#iMajor").textContent = scene().label;
  $("#courseChip").textContent = scene().course;
  renderStream(); renderNote(); renderKB(); renderHandoff();
  log("[W1] 骨架就绪 · 双击 index.html 即可运行 · 零依赖");
}
document.addEventListener("DOMContentLoaded", init);
})();
