package com.thesis.sportologia.model.photos

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thesis.sportologia.model.photos.entities.Photo

typealias PhotosPageLoader = suspend (pageIndex: Int, pageSize: Int) -> List<Photo>

@Suppress("UnnecessaryVariable")
class PhotosPagingSource(
    private val loader: PhotosPageLoader,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        // get the index of page to be loaded (it may be NULL, in this case let's load the first page with index = 0)
        val pageIndex = params.key ?: 0

        return try {
            // loading the desired page of users
            val photos = loader.invoke(pageIndex, params.loadSize)
            // success! now we can return LoadResult.Page
            return LoadResult.Page(
                data = photos,
                // index of the previous page if exists
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                // index of the next page if exists;
                // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
                nextKey = if (photos.size == params.loadSize) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            // failed to load users -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        // get the most recently accessed index in the users list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

}