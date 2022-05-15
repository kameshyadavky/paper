package com.beetlestance.paper

import com.beetlestance.paper.editor.PaperEditorValue

sealed class PaperItem(
    public val ind: Int
) {
    data class Editor(val index: Int, val editorValue: PaperEditorValue) : PaperItem(index)
    data class Image(val index: Int, val url: String) : PaperItem(index)
}