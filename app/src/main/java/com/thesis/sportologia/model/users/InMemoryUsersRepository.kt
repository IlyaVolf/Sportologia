package com.thesis.sportologia.model.users

import android.util.Log
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.users.entities.Athlete
import com.thesis.sportologia.model.users.entities.Organization
import com.thesis.sportologia.model.users.entities.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryUsersRepository @Inject constructor() : UsersRepository {

    val users = mutableListOf(
        Athlete(
            true,
            null,
            "i_volf",
            "Илья Вольф",
            "-----",
            "https://i.imgur.com/tGbaZCY.jpg",
            5,
            10,
            hashMapOf(
                Pair("Аэробика", true),
                Pair("Бег", false)
            ),
            false,
        ),
        Athlete(
            true,
            null,
            "i_chiesov",
            "Игорь Чиёсов",
            "-----",
            null,
            1,
            0,
            hashMapOf(
                Pair("Аэробика", true),
                Pair("Бег", true)
            ),
            true,
        ),
        Organization(
            null,
            "stroitel",
            "Тренажёрный зал Строитель",
            "=====",
            null,
            2993,
            302,
            hashMapOf(
                Pair("Аэробика", false),
                Pair("Бег", false)
            ),
            false,
        ),

        )

    override suspend fun getUser(userId: String): User? {
        delay(1000)

        // throw Exception()

        return if (users.none { it.id == userId }) return null else users.filter { it.id == userId }[0]
    }

    override suspend fun setIsSubscribe(
        followerId: String,
        followingId: String,
        isSubscribed: Boolean
    ) {
        delay(1000)

        //throw Exception()

        Log.d("BUGFIX", "$followerId $followingId ${users.filter { it.id == followingId }}")

        val follower = users.filter { it.id == followerId }
        val following = users.filter { it.id == followingId }
        if (isSubscribed) {
            follower.forEach { it.followingsCount++ }
            following.forEach { it.followersCount++ }
        } else {
            follower.forEach { it.followingsCount-- }
            following.forEach { it.followersCount-- }
        }
        following.forEach { it.isSubscribed = isSubscribed }

    }

}