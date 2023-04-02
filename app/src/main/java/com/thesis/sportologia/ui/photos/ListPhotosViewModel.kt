package com.thesis.sportologia.ui.photos


import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thesis.sportologia.model.photos.PhotosRepository
import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.ui.base.BaseViewModel
import com.thesis.sportologia.ui.photos.entities.PhotoListItem
import com.thesis.sportologia.utils.*
import com.thesis.sportologia.utils.logger.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

class ListPhotosViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val photosRepository: PhotosRepository,
    logger: Logger
) : BaseViewModel(logger) {

    private val searchLive = MutableLiveData("")

    private val _errorEvents = MutableLiveEvent<Int>()
    val errorEvents = _errorEvents.share()

    private val _scrollEvents = MutableLiveEvent<Unit>()
    val scrollEvents = _scrollEvents.share()

    private var _invalidateEvents = MutableLiveEvent<Unit>()
    val invalidateEvents = _invalidateEvents.share()

    val photosFlow: Flow<PagingData<PhotoListItem>>


    init {
        val originPhotosFlow = this.getDataFlow()

        photosFlow = originPhotosFlow.map { it.map { photo -> PhotoListItem(photo.photoUrl) } }
    }

    private fun getDataFlow(): Flow<PagingData<Photo>> {
        return searchLive.asFlow()
            .flatMapLatest {
                photosRepository.getPagedUserPhotos(userId)
            }.cachedIn(viewModelScope)
    }

    fun refresh() {
        this.searchLive.value = this.searchLive.value
    }

    private fun showError(@StringRes errorMessage: Int) {
        _errorEvents.publishEvent(errorMessage)
    }

    private fun scrollListToTop() {
        _scrollEvents.publishEvent(Unit)
    }

    private fun invalidateList() {
        _invalidateEvents.publishEvent(Unit)
    }

    @AssistedFactory
    interface Factory {
        fun create(userId: String): ListPhotosViewModel
    }

}