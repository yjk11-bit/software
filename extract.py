
import fitz
dir = r"C:\\Users\\gaogo\\Desktop\\软创"
for f in ["赛题详情.pdf","参赛项目阶段评分标准.pdf","报名方法.pdf","流程安排.pdf","可支持能力清单.pdf"]:
    try:
        doc = fitz.open(dir + "\\" + f)
        txt = []
        for p in doc:
            txt.append(p.get_text())
        out = dir + "\\" + f.replace(".pdf",".txt")
        open(out,"w",encoding="utf-8").write("\n".join(txt))
        print(f, "pages:", len(doc), "chars:", sum(len(t) for t in txt))
    except Exception as e:
        print("ERR", f, e)
