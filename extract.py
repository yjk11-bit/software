# -*- coding: utf-8 -*-
"""从本目录下的官方附件 PDF 提取纯文本（.txt），供检索 / 引用。

可移植：以脚本所在目录为基准 Path(__file__).resolve().parent，
        不写死任何盘符 / 绝对路径——整个「软创」文件夹搬到任意磁盘或目录后仍可直接重跑。
命名兼容：附件实际为「附件N_XXX.pdf」，输出的 .txt 用去前缀的干净名，与仓库内 5 份 txt 对齐。
依赖：PyMuPDF（pip install pymupdf）；同时兼容旧版 import fitz。
用法：python extract.py
"""
from pathlib import Path
try:
    import pymupdf as fitz
except ImportError:
    import fitz

HERE = Path(__file__).resolve().parent
TARGETS = ["赛题详情", "参赛项目阶段评分标准", "报名方法", "流程安排", "可支持能力清单"]


def find_pdf(clean_name):
    hits = sorted(HERE.glob("*_" + clean_name + ".pdf")) or sorted(HERE.glob(clean_name + ".pdf"))
    return hits[0] if hits else None


def main():
    for name in TARGETS:
        pdf = find_pdf(name)
        if pdf is None:
            print("SKIP (未找到 PDF):", name)
            continue
        try:
            doc = fitz.open(pdf)
            text = "\n".join(page.get_text() for page in doc)
            out = HERE / (name + ".txt")
            out.write_text(text, encoding="utf-8")
            print(f"{pdf.name} -> {out.name}  pages={len(doc)} chars={len(text)}")
        except Exception as e:
            print("ERR", pdf.name, e)


if __name__ == "__main__":
    main()
