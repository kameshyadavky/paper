package com.beetlestance.paper.feature_noteeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beetlestance.paper.common.toDataClass
import com.beetlestance.paper.common.toJsonString
import com.beetlestance.paper.data.model.Note
import com.beetlestance.paper.data.NoteEditorRepository
import com.beetlestance.paper.editor.PaperEditorValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val noteEditorRepository: NoteEditorRepository
) : ViewModel() {

    private val selectedIndex = MutableStateFlow(0)
    private val bodyItems: MutableStateFlow<List<Any>> = MutableStateFlow(emptyList())
    private val note: MutableStateFlow<Note?> = MutableStateFlow(null)

    init {

        viewModelScope.launch {
            noteEditorRepository.observeNote().collect { note ->
                if (note == null) {
                    val startItem = NoteEditorValue.Empty
                    bodyItems.emit(listOf(startItem))
                } else {
                    val items = note.body!!.map {
                        if (it.type == Note.Body.Text) {
                            it.body.toDataClass<NoteEditorValue>()
                        } else {
                            it.body.toDataClass<NoteImage>()
                        }
                    }
                    this@NoteEditorViewModel.note.emit(note)
                    bodyItems.emit(items)
                }

            }
        }
    }

    val state: StateFlow<NoteEditorViewState> = combine(
        bodyItems,
        note,
        selectedIndex,
    ) { noteItems, note, index ->
        NoteEditorViewState(
            heading = note?.heading ?: "",
            bodyItems = noteItems,
            selectedIndex = index,
            note = note
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteEditorViewState.Empty)

    fun updateSelectedIndex(index: Int) {
        viewModelScope.launch {
            selectedIndex.emit(index)
        }
    }

    fun updateHeading(heading: String) {
        viewModelScope.launch {
            note.getAndUpdate { it?.copy(heading = heading) }
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
            val earlierNote = note.value ?: Note(
                body = null,
                heading = state.value.heading
            )

            noteEditorRepository.updateNote(
                earlierNote.copy(
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
                    }
                )
            )
        }
    }
}