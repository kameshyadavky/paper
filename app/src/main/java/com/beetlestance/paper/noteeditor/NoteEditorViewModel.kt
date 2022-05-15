package com.beetlestance.paper.noteeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beetlestance.paper.common.toDataClass
import com.beetlestance.paper.common.toJsonString
import com.beetlestance.paper.data.Note
import com.beetlestance.paper.data.NoteEditorRepository
import com.beetlestance.paper.editor.PaperEditorValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteEditorRepository: NoteEditorRepository
) : ViewModel() {

    private val selectedIndex = MutableStateFlow(0)

    val state: StateFlow<NoteEditorViewState> = combine(
        noteEditorRepository.noteItems,
        selectedIndex
    ) { noteItems, index ->
        NoteEditorViewState(
            heading = noteItems.heading,
            bodyItems = noteItems.body?.map {
                if (it.type == Note.Body.Text) {
                    it.body.toDataClass<NoteEditorValue>()
                } else {
                    it.body.toDataClass<NoteImage>()
                }
            }
                ?: listOf(NoteEditorValue(editorValue = PaperEditorValue())),
            selectedIndex = index
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteEditorViewState.Empty)

    fun updateSelectedIndex(index: Int) {
        viewModelScope.launch {
            selectedIndex.emit(index)
        }
    }

    fun updateHeading(heading: String) {
        viewModelScope.launch {
            noteEditorRepository.updateNote(
                Note(
                    heading = heading,
                    body = state.value.bodyItems.map { noteItem ->
                        when {
                            noteItem is NoteImage -> {
                                Note.Body(
                                    type = Note.Body.Image,
                                    body = noteItem.toJsonString()
                                )
                            }
                            else -> {
                                Note.Body(
                                    type = Note.Body.Text,
                                    body = noteItem.toJsonString()
                                )
                            }
                        }
                    }
                )
            )
        }
    }

    fun updateBodyItem(
        index: Int,
        editorBody: NoteEditorValue
    ) {
        viewModelScope.launch {
            selectedIndex.emit(index)
            val noteBody =
                state.value.bodyItems.mapIndexed { mapIndex, noteItem ->
                    when {
                        noteItem is NoteImage -> {
                            Note.Body(
                                type = Note.Body.Image,
                                body = noteItem.toJsonString()
                            )
                        }
                        noteItem is NoteEditorValue && index == mapIndex -> {
                            Note.Body(
                                type = Note.Body.Text,
                                body = editorBody.toJsonString()
                            )
                        }
                        else -> {
                            Note.Body(
                                type = Note.Body.Text,
                                body = noteItem.toJsonString()
                            )
                        }
                    }
                }

            noteEditorRepository.updateNote(
                Note(
                    heading = state.value.heading,
                    body = noteBody
                )
            )
        }
    }

    fun addImage(
        index: Int,
        image: String
    ) {
        viewModelScope.launch {
            val bodyItems = state.value.bodyItems.toMutableList()
            bodyItems.add(index, NoteImage(path = image))
            if (bodyItems.lastIndex <= index) {
                bodyItems.add(index + 1, NoteEditorValue.Empty)
            }

            val items = bodyItems.map { noteItem ->
                when {
                    noteItem is NoteImage -> {
                        Note.Body(
                            type = Note.Body.Image,
                            body = noteItem.toJsonString()
                        )
                    }
                    else -> {
                        Note.Body(
                            type = Note.Body.Text,
                            body = noteItem.toJsonString()
                        )
                    }
                }
            }
            noteEditorRepository.updateNote(Note(heading = state.value.heading, body = items))
        }
    }

    fun updateImage(
        index: Int,
        image: String,
        widthPercent: Float
    ) {
        val bodyItems = state.value.bodyItems.mapIndexed { mapIndex, any ->
            if (mapIndex == index)
                NoteImage(path = image, widthPercentage = widthPercent)
            else
                any
        }
        val items = bodyItems.map { noteItem ->
            when {
                noteItem is NoteImage -> {
                    Note.Body(
                        type = Note.Body.Image,
                        body = noteItem.toJsonString()
                    )
                }
                else -> {
                    Note.Body(
                        type = Note.Body.Text,
                        body = noteItem.toJsonString()
                    )
                }
            }
        }
        noteEditorRepository.updateNote(Note(heading = state.value.heading, body = items))
    }

    fun removeImage(index: Int) {
        val bodyItems = state.value.bodyItems.mapIndexedNotNull { mapIndex, any ->
            if (mapIndex == index)
                null
            else
                any
        }
        val items = bodyItems.map { noteItem ->
            when {
                noteItem is NoteImage -> {
                    Note.Body(
                        type = Note.Body.Image,
                        body = noteItem.toJsonString()
                    )
                }
                else -> {
                    Note.Body(
                        type = Note.Body.Text,
                        body = noteItem.toJsonString()
                    )
                }
            }
        }
        noteEditorRepository.updateNote(Note(heading = state.value.heading, body = items))
    }

}