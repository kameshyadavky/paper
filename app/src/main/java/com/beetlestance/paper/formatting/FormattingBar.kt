package com.beetlestance.paper.formatting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beetlestance.paper.R
import com.beetlestance.paper.editor.PaperEditorValue
import com.beetlestance.paper.editor.PaperParagraphToggle
import com.beetlestance.paper.editor.PaperSpanToggle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormattingBar(
    editorValue: PaperEditorValue?,
    onValueChange: (PaperEditorValue) -> Unit,
    modifier: Modifier = Modifier,
    onAddImage: (String) -> Unit
) {
    if (editorValue == null) return
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(fontWeight = FontWeight.Bold) },
                spanEqualPredicate = { style ->
                    style.fontWeight == FontWeight.Bold
                }
            ) { enabled, onToggle ->
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_bold),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    colorFilter = if (enabled) ColorFilter.tint(color = Color.Black) else null
                )
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(fontStyle = FontStyle.Italic) },
                spanEqualPredicate = { style ->
                    style.fontStyle == FontStyle.Italic
                }
            ) { enabled, onToggle ->
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_italic),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    colorFilter = if (enabled) ColorFilter.tint(color = Color.Black) else null
                )
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(textDecoration = TextDecoration.LineThrough) },
                spanEqualPredicate = { style ->
                    style.textDecoration == TextDecoration.LineThrough
                }
            ) { enabled, onToggle ->
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_strikethrough),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    colorFilter = if (enabled) ColorFilter.tint(color = Color.Black) else null
                )
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(textDecoration = TextDecoration.Underline) },
                spanEqualPredicate = { style ->
                    style.textDecoration == TextDecoration.Underline
                }
            ) { enabled, onToggle ->
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_underline),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    colorFilter = if (enabled) ColorFilter.tint(color = Color.Black) else null
                )
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(fontSize = 32.sp) },
                spanEqualPredicate = { style ->
                    style.fontSize == 32.sp
                }
            ) { enabled, onToggle ->
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_heading),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    colorFilter = if (enabled) ColorFilter.tint(color = Color.Black) else null
                )
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(color = Color.Red) },
                spanEqualPredicate = { style ->
                    style.color == Color.Red
                }
            ) { enabled, onToggle ->

                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (enabled) Color.White else Color.Transparent
                    ),
                    onClick = { onToggle() },
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(color = Color.Red, shape = CircleShape)
                    )
                }
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(color = Color.Blue) },
                spanEqualPredicate = { style ->
                    style.color == Color.Blue
                }
            ) { enabled, onToggle ->

                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (enabled) Color.White else Color.Transparent
                    ),
                    onClick = { onToggle() },
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(color = Color.Blue, shape = CircleShape)
                    )
                }
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(color = Color.Green) },
                spanEqualPredicate = { style ->
                    style.color == Color.Green
                }
            ) { enabled, onToggle ->
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (enabled) Color.White else Color.Transparent
                    ),
                    onClick = { onToggle() },
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(color = Color.Green, shape = CircleShape)
                    )
                }
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(color = Color.LightGray) },
                spanEqualPredicate = { style ->
                    style.color == Color.LightGray
                }
            ) { enabled, onToggle ->
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (enabled) Color.White else Color.Transparent
                    ),
                    onClick = { onToggle() },
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(color = Color.LightGray, shape = CircleShape)
                    )
                }
            }
        }

        item {
            PaperSpanToggle(
                value = editorValue,
                onValueChange = onValueChange,
                spanFactory = { SpanStyle(background = Color.Gray) },
                spanEqualPredicate = { style ->
                    style.background == Color.Gray
                }
            ) { enabled, onToggle ->

                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_highlighter),
                    contentDescription = "Bold text",
                    modifier = Modifier
                        .clickable { onToggle() }
                        .background(
                            if (enabled) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        item {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_image_choose),
                contentDescription = "Align text in center",
                modifier = Modifier
                    .clickable {
                        onAddImage("https://picsum.photos/300/200")
                    },
                colorFilter = ColorFilter.tint(Color.White)
            )

        }
    }
}