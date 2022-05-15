package com.beetlestance.paper.data.model

data class Note(
    val heading: String,
    val time: Long = 0,
    val body: List<Body>?
) {
    data class Body(
        val type: Int,
        val body: String
    ) {
        companion object {
            const val Text = 0
            const val Image = 1
        }
    }

    companion object {
        val Empty = Note(
            heading = "",
            time = System.currentTimeMillis(),
            body = null
        )
    }
}