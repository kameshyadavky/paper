package com.beetlestance.paper.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle

@Immutable
class PaperString(
    val text: String,
    val spanStyles: List<Range<SpanStyle>> = emptyList(),
    val paragraphStyles: List<Range<ParagraphStyle>> = emptyList(),
    val annotations: List<Range<String>> = emptyList()
) {
    init {
        spanStyles.forEach { range ->
            require(0 <= range.start && range.end <= text.length) {
                "span style is out of boundary (style=${range.toReadableString()}, text length=${text.length})"
            }
        }

        paragraphStyles.forEach { range ->
            require(0 <= range.start && range.end <= text.length) {
                "paragraph style is out of boundary (style=${range.toReadableString()}, text length=${text.length})"
            }
        }
    }

    @Immutable
    data class Range<out T>(
        val item: T,
        val start: Int, val end: Int,
        val startInclusive: Boolean, val endInclusive: Boolean
    ) {
        init {
            require(start <= end) {
                "invalid range ($start > $end)"
            }
        }

        val length: Int = end - start

        operator fun contains(index: Int): Boolean =
            (start < index || (startInclusive && start == index))
                    && (index < end || (endInclusive && end == index))

        operator fun contains(other: Range<*>): Boolean =
            contains(other.start) && contains(other.end)
    }

    fun copy(
        text: String = this.text,
        spanStyles: List<Range<SpanStyle>> = this.spanStyles,
        paragraphStyles: List<Range<ParagraphStyle>> = this.paragraphStyles,
        annotations: List<Range<String>> = this.annotations
    ): PaperString = PaperString(
        text = text,
        spanStyles = spanStyles,
        paragraphStyles = paragraphStyles,
        annotations = annotations
    )
}

// This is added to help with the logs and debugging
internal fun PaperString.Range<*>.toReadableString(): String =
    "${if (startInclusive) '[' else '('}$start..$end${if (endInclusive) ']' else ')'}"

fun PaperString.toAnnotatedString(): AnnotatedString = AnnotatedString(
    text = text,
    spanStyles = spanStyles.mapNotNull {
        if (it.start == it.end) {
            // AnnotatedString might behave wrongly if there are some zero-length spans
            null
        } else {
            it.toAnnotatedStringRange()
        }
    },
    paragraphStyles = paragraphStyles.map { it.toAnnotatedStringRange() }
)

fun <T> PaperString.Range<T>.toAnnotatedStringRange(): AnnotatedString.Range<T> =
    AnnotatedString.Range(
        item = item,
        start = start, end = end
    )

/**
 * Checks if [this] list of range fills [[start]..[end]) with items which meets [predicate].
 * This helps us to determine if a predicate span is active in this range
 */
internal fun <T> Iterable<PaperString.Range<T>>.fillsRange(
    start: Int,
    end: Int,
    predicate: (T) -> Boolean
): Boolean {
    val ranges = filter { predicate(it.item) }.sortedBy { it.start }
    var leftover = start..end

    for (range in ranges) {
        if (range.end < leftover.first) continue

        if (leftover.first < range.start) return false
        if (end <= range.end) return true

        leftover = range.end..end
    }

    return false
}

private val NonEmptyRangePredicate: (PaperString.Range<*>) -> Boolean = {
    it.start != it.end
}

/**
 * Removes spans in range [[start], [endExclusive]) whose span meets [predicate].
 */
internal inline fun <T> List<PaperString.Range<T>>.minusSpansInRange(
    start: Int,
    endExclusive: Int,
    predicate: (T) -> Boolean = { true }
): List<PaperString.Range<T>> = flatMap { range ->
    // if nothing matches the span return exact range
    if (!predicate(range.item)) return@flatMap listOf(range)

    // this removes all the span from selection
    if (start <= range.start && range.end < endExclusive) {
        emptyList()
    } else if (range.start <= start && endExclusive <= range.end) {
        // SELECTION:      -----
        // RANGE    :    ----------
        // REMAINDER:    __     ___
        listOf(
            range.copy(end = start, endInclusive = false),
            range.copy(start = endExclusive)
        ).filter(NonEmptyRangePredicate)
    } else if (start in range) {
        // SELECTION:     ---------
        // RANGE    : --------
        // REMAINDER: ____
        listOf(range.copy(end = start, endInclusive = false))
            .filter(NonEmptyRangePredicate)
    } else if (endExclusive in range) {
        // SELECTION: ---------
        // RANGE    :      --------
        // REMAINDER:          ____
        listOf(range.copy(start = endExclusive))
            .filter(NonEmptyRangePredicate)
    } else {
        listOf(range)
    }
}

/**
 * This still has a bug, paragraph style can not intersect, so at a time only one paragraph style can be
 * active that means we have to create cases here to support new paragraphs
 */
internal inline fun <T> List<PaperString.Range<T>>.removeIntersectingWithRange(
    start: Int,
    end: Int,
    predicate: (T) -> Boolean
): List<PaperString.Range<T>> = mapNotNull { range ->
    if (!predicate(range.item)) return@mapNotNull null

    if (range.contains(start) || range.contains(end)) {
        null
    } else {
        range
    }
}
