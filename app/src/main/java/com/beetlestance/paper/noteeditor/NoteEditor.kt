package com.beetlestance.paper.noteeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.beetlestance.paper.R
import com.beetlestance.paper.common.rememberStateWithLifecycle
import com.beetlestance.paper.editor.PaperEditor
import com.beetlestance.paper.formatting.FormattingBar
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding


val fontFamily = FontFamily(
    Font(
        resId = R.font.work_sans_regular,
        weight = FontWeight.W400,
        style = FontStyle.Normal
    )
)

@Composable
fun NoteEditor() {
    val viewModel = hiltViewModel<NoteEditorViewModel>()
    NoteEditor(viewModel = viewModel)
}

@Composable
fun NoteEditor(viewModel: NoteEditorViewModel) {
    val state by rememberStateWithLifecycle(stateFlow = viewModel.state)
    NoteEditor(
        state = state,
        updateHeading = viewModel::updateHeading,
        updateBodyItem = viewModel::updateBodyItem,
        addImage = viewModel::addImage,
        updateImage = viewModel::updateImage,
        updateSelectedIndex = viewModel::updateSelectedIndex,
        removeImage = viewModel::removeImage
    )
}

@Composable
fun NoteEditor(
    state: NoteEditorViewState,
    updateHeading: (heading: String) -> Unit,
    updateBodyItem: (Int, NoteEditorValue) -> Unit,
    addImage: (Int, String) -> Unit,
    updateImage: (Int, String, Float) -> Unit,
    updateSelectedIndex: (Int) -> Unit,
    removeImage: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = Color(android.graphics.Color.parseColor("#353846")),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(paddingValues)
                .navigationBarsWithImePadding()
                .fillMaxSize()
        ) {
            TextField(
                value = state.heading,
                onValueChange = { updateHeading(it) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .background(color = Color.DarkGray),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color(android.graphics.Color.parseColor("#353846")),
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = "Lost",
                        color = Color.Gray,
                        style = MaterialTheme.typography.h5.copy(fontFamily = fontFamily)
                    )
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.h5.copy(fontFamily = fontFamily)
            )

            EditScrollingContent(
                bodyItems = state.bodyItems,
                updateBodyItem = { index, item ->
                    updateBodyItem(index, item)
                },
                updateSelectedIndex = updateSelectedIndex,
                removeImage = removeImage,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (state.bodyItems.getOrNull(state.selectedIndex) is NoteEditorValue) {
                FormattingBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(android.graphics.Color.parseColor("#353846")))
                        .padding(vertical = 12.dp),
                    onValueChange = { editorValue ->
                        updateBodyItem(state.selectedIndex, NoteEditorValue(editorValue))
                    },
                    editorValue = (state.bodyItems[state.selectedIndex] as? NoteEditorValue)?.editorValue,
                    onAddImage = { path ->
                        addImage(state.selectedIndex + 1, path)
                    }
                )
            }

            if (state.bodyItems.getOrNull(state.selectedIndex) is NoteImage) {
                Slider(
                    value = (state.bodyItems.getOrNull(state.selectedIndex) as NoteImage).widthPercentage,
                    onValueChange = {
                        val imageItem =
                            (state.bodyItems.getOrNull(state.selectedIndex) as NoteImage)
                        updateImage(state.selectedIndex, imageItem.path, it)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(android.graphics.Color.parseColor("#CED2F8")),
                        activeTrackColor = Color(android.graphics.Color.parseColor("#CED2F8"))
                    )
                )
            }


        }
    }
}

@Composable
private fun EditScrollingContent(
    bodyItems: List<Any>,
    updateBodyItem: (Int, NoteEditorValue) -> Unit,
    modifier: Modifier = Modifier,
    updateSelectedIndex: (Int) -> Unit,
    removeImage: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        bodyItems.forEachIndexed { index, noteItem ->
            if (noteItem is NoteEditorValue) {
                item {
                    key(index) {
                        PaperEditor(
                            value = noteItem.editorValue,
                            onValueChange = { newEditorValue ->
                                updateBodyItem(index, noteItem.copy(editorValue = newEditorValue))
                            }
                        ) { value, onValueChange ->

                            TextField(
                                value = value,
                                onValueChange = onValueChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (index == bodyItems.lastIndex) Modifier.defaultMinSize(
                                            minHeight = 200.dp
                                        ) else Modifier
                                    )
                                    .background(color = Color.DarkGray)
                                    .onFocusChanged {
                                        if (it.isFocused) {
                                            updateSelectedIndex(index)
                                        }
                                    },
                                colors = TextFieldDefaults.textFieldColors(
                                    textColor = Color.White,
                                    backgroundColor = Color(android.graphics.Color.parseColor("#353846")),
                                    cursorColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = {
                                    Text(
                                        text = if (index == 0) "Once upon a time..." else "",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.body1.copy(fontFamily = fontFamily)
                                    )
                                },
                                textStyle = MaterialTheme.typography.body1.copy(fontFamily = fontFamily),
                            )
                        }
                    }
                }
            }

            if (noteItem is NoteImage) {
                item {
                    key(noteItem.path, index) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = noteItem.path,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillParentMaxWidth(fraction = noteItem.widthPercentage)
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp)
                                    .aspectRatio(3 / 2f)
                                    .clip(shape = RoundedCornerShape(12.dp))
                                    .align(Alignment.Center)
                                    .clickable {
                                        updateSelectedIndex(index)
                                    },
                                contentScale = ContentScale.FillWidth
                            )

                            Image(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_reomve),
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .clickable { removeImage(index) }
                            )
                        }

                    }
                }
            }
        }

    }

}