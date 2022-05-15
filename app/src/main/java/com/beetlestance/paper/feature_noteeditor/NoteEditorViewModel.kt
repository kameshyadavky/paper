package com.beetlestance.paper.feature_noteeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beetlestance.paper.common.toDataClass
import com.beetlestance.paper.common.toJsonString
import com.beetlestance.paper.data.NoteEditorRepository
import com.beetlestance.paper.data.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteEditorRepository: NoteEditorRepository
) : ViewModel() {

    private var noteId: Long = 0
    private val selectedIndex = MutableStateFlow(0)
    private val bodyItems: MutableStateFlow<List<Any>> = MutableStateFlow(emptyList())
    private val heading: MutableStateFlow<String> = MutableStateFlow("")

    init {

        viewModelScope.launch {
            noteEditorRepository.observeNote().collect { note ->
                val startItem = NoteEditorValue.Empty
                if (note == null) {
                    bodyItems.emit(listOf(startItem))
                } else {
                    noteId = note.id
                    val items = note.body?.map {
                        if (it.type == Note.Body.Text) {
                            it.body.toDataClass<NoteEditorValue>()
                        } else {
                            it.body.toDataClass<NoteImage>()
                        }
                    }
                    heading.emit(note.heading)
                    bodyItems.emit(items ?: listOf(startItem))
                }

            }
        }
    }

    val state: StateFlow<NoteEditorViewState> = combine(
        bodyItems,
        heading,
        selectedIndex,
    ) { noteItems, heading, index ->
        NoteEditorViewState(
            heading = heading,
            bodyItems = noteItems,
            selectedIndex = index,
            note = null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteEditorViewState.Empty)

    fun updateSelectedIndex(index: Int) {
        viewModelScope.launch {
            selectedIndex.emit(index)
        }
    }

    fun updateHeading(heading: String) {
        viewModelScope.launch {
            this@NoteEditorViewModel.heading.emit(heading)
        }
    }

    fun updateBodyItem(
        index: Int,
        editorBody: NoteEditorValue
    ) {
        viewModelScope.launch {
            selectedIndex.emit(index)
            bodyItems.getAndUpdate {
                it.mapIndexed { mapIndex, any ->
                    if (index == mapIndex) editorBody else any
                }
            }
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
            this@NoteEditorViewModel.bodyItems.emit(bodyItems)
        }
    }

    fun updateImage(
        index: Int,
        image: String,
        widthPercent: Float
    ) {
        viewModelScope.launch {
            bodyItems.getAndUpdate {
                it.mapIndexed { mapIndex, any ->
                    if (mapIndex == index)
                        NoteImage(path = image, widthPercentage = widthPercent)
                    else
                        any
                }
            }
        }
    }

    fun removeImage(index: Int) {
        viewModelScope.launch {
            bodyItems.getAndUpdate {
                it.mapIndexedNotNull { mapIndex, any ->
                    if (mapIndex == index)
                        null
                    else
                        any
                }
            }
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            noteEditorRepository.updateNote(
                Note(
                    id = noteId,
                    body = bodyItems.value.mapNotNull {
                        when (it) {
                            is NoteEditorValue -> {
                                Note.Body(
                                    type = Note.Body.Text,
                                    body = it.toJsonString()
                                )
                            }
                            is NoteImage -> {
                                Note.Body(
                                    type = Note.Body.Image,
                                    body = it.toJsonString()
                                )
                            }
                            else -> null
                        }
                    },
                    heading = heading.value
                )
            )
        }
    }
}