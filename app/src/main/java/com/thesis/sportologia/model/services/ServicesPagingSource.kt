package com.thesis.sportologia.model.services

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thesis.sportologia.model.services.entities.ServiceDataEntity

typealias ServicesPageLoader = suspend (lastTimestamp: Long?, pageIndex: Int, pageSize: Int) -> List<ServiceDataEntity>

@Suppress("UnnecessaryVariable")
class ServicesPagingSource(
    private val loader: ServicesPageLoader,
) : PagingSource<Int, ServiceDataEntity>() {

    var lastTimestamp: Long? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServiceDataEntity> {
        // get the index of page to be loaded (it may be NULL, in this case let's load the first page with index = 0)
        val pageIndex = params.key ?: 0

        return try {
            // loading the desired page of users
            val posts = loader.invoke(lastTimestamp, pageIndex, params.loadSize)

            lastTimestamp = posts.lastOrNull()?.dateCreatedMillis
            // success! now we can return LoadResult.Page
            return LoadResult.Page(
                data = posts,
                // index of the previous page if exists
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                // index of the next page if exists;
                // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
                nextKey = if (posts.size == params.loadSize) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            // failed to load users -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ServiceDataEntity>): Int? {
        // get the most recently accessed index in the users list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

}