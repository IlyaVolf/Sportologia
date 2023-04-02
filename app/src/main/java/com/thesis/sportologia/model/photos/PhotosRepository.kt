package com.thesis.sportologia.model.photos

import androidx.paging.PagingData
import com.thesis.sportologia.model.photos.entities.Photo
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {

    suspend fun getPagedUserPhotos(userId: String): Flow<PagingData<Photo>>

    suspend fun createPhoto(photo: Photo)

    suspend fun deletePhoto(photoId: Long)

}