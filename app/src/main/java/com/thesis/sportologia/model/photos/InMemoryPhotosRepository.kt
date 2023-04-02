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
class InMemoryPhotosRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PhotosRepository {

    private val usersPhotos = mutableListOf(
        UserPhotos(
            "i_volf",
            mutableListOf(
                Photo("https://kartinkin.net/pics/uploads/posts/2022-07/1658445549_58-kartinkin-net-p-shveitsarskie-alpi-priroda-krasivo-foto-63.jpg"),
                Photo("http://www.fotosselect.ru/wp-content/uploads/2016/01/Горы_Альпы_Alps_Alpes_Alpi_коровы_пасутся_у_Альп_в_горах_Европа.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
                Photo("https://img2.goodfon.com/original/2048x1365/d/39/gory-alpy-italiya-dolina-les.jpg"),
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

    suspend fun getUserPhotos(pageIndex: Int, pageSize: Int, userId: String): List<Photo> =
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

    override suspend fun deletePhoto(photo: Photo) {
        TODO("Not yet implemented")
    }

    private companion object {
        const val PAGE_SIZE = 15
    }

}