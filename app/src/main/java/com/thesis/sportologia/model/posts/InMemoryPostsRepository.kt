package com.thesis.sportologia.model.posts

import com.thesis.sportologia.model.posts.entities.Post
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryPostsRepository @Inject constructor() : PostsRepository {

    private val posts = mutableListOf(
        Post(
            0L,
            1,
            "https://i.imgur.com/tGbaZCY.jpg",
            "Hello!",
            5,
            isLiked = true,
            isAddedToFavourites = true,
            postedDate = Calendar.getInstance(),
            photosUrls = null
        ),
        Post(
            1L,
            2,
            null,
            "Игорь любит Андрея",
            0,
            isLiked = false,
            isAddedToFavourites = false,
            postedDate = Calendar.getInstance(),
            photosUrls = null
        )
    )

    private val followersIds = mutableListOf(2, 3)

    override suspend fun getUserPosts(userId: Int): List<Post> {
        delay(1000)
        return posts.filter { it.authorId == userId }
    }

    override suspend fun getUserSubscribedOnPosts(userId: Int): List<Post> {
        delay(1000)
        val res = mutableListOf<Post>()

        followersIds.forEach {
            res.addAll(getUserPosts(it))
        }

        return res
    }

    override suspend fun getUserFavouritePosts(userId: Int): List<Post> {
        delay(1000)
        return posts.filter { it.authorId == userId && it.isAddedToFavourites }
    }

    override suspend fun createPost(post: Post) {
        delay(1000)
        posts.add(post)

        //throw Exception("Ошибка подключения: проверьте соединение с интернетом.")
    }

    override suspend fun updatePost(post: Post) {
        delay(1000)
        deletePost(post)
        createPost(post)
    }

    override suspend fun deletePost(post: Post) {
        delay(1000)
        posts.removeIf { it.id == post.id }
    }

    override suspend fun likePost(userId: Int, post: Post) {
        delay(1000)
        updatePost(post.copy(isLiked = true, likesCount = post.likesCount + 1))
    }

    override suspend fun unlikePost(userId: Int, post: Post) {
        delay(1000)
        updatePost(post.copy(isLiked = false, likesCount = post.likesCount - 1))
    }

    override suspend fun addPostToFavourites(userId: Int, post: Post) {
        delay(1000)
        updatePost(post.copy(isAddedToFavourites = true))
    }

    override suspend fun removePostFromFavourites(userId: Int, post: Post) {
        delay(1000)
        updatePost(post.copy(isAddedToFavourites = false))
    }


}