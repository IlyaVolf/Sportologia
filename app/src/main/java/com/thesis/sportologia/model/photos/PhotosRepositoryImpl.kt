package com.thesis.sportologia.model.photos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.photos.entities.Photo
import com.thesis.sportologia.model.photos.entities.UserPhotos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotosRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PhotosRepository {

    private val usersPhotos = mutableListOf(
        UserPhotos(
            "i_volf",
            mutableListOf(
                Photo(
                    0,
                    "https://kartinkin.net/pics/uploads/posts/2022-07/1658445549_58-kartinkin-net-p-shveitsarskie-alpi-priroda-krasivo-foto-63.jpg"
                ),
                Photo(
                    1,
                    "https://mykaleidoscope.ru/x/uploads/posts/2022-10/1666403296_34-mykaleidoscope-ru-p-alpi-tsugshpittse-pinterest-40.jpg"
                ),
                Photo(
                    2,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    3,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    4,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    5,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    6,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    7,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    8,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    9,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    10,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    11,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    12,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    13,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    14,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    15,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    16,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    17,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    18,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    19,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    20,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    21,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    22,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    23,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    24,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    25,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    26,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    27,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    28,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    29,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    30,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
                Photo(
                    31,
                    "https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"
                ),
            )
        ),
        UserPhotos(
            "i_chiesov",
            mutableListOf()
        ),
    )


    override suspend fun getPagedUserPhotos(userId: String): Flow<PagingData<Photo>> {
        val loader: PhotosPageLoader = { pageIndex, pageSize ->
            getUserPhotos(pageIndex, pageSize, userId)
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotosPagingSource(loader) }
        ).flow
    }

    private suspend fun getUserPhotos(pageIndex: Int, pageSize: Int, userId: String): List<Photo> =
        withContext(ioDispatcher) {
            delay(1000)

            val offset = pageIndex * pageSize

            val photos = usersPhotos.first { it.userId == userId }.photosUris
            if (offset >= photos.size) {
                return@withContext listOf<Photo>()
            } else if (offset + pageSize >= photos.size) {
                return@withContext photos.subList(offset, photos.size)
            } else {
                return@withContext photos.subList(offset, offset + pageSize)
            }
        }

    override suspend fun createPhoto(photo: Photo) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePhoto(photoId: Long) {
        TODO("Not yet implemented")
    }

    private companion object {
        const val PAGE_SIZE = 18
    }

}