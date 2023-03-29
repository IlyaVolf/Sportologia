package com.thesis.sportologia.sources

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.thesis.sportologia.di.IoDispatcher
import com.thesis.sportologia.model.services.entities.Service
import com.thesis.sportologia.model.services.entities.FilterParamsServices
import com.thesis.sportologia.model.services.entities.ServiceDetailed
import com.thesis.sportologia.model.services.entities.ServiceType
import com.thesis.sportologia.model.users.entities.UserType
import com.thesis.sportologia.utils.Categories
import com.thesis.sportologia.utils.TrainingProgrammesCategories
import com.thesis.sportologia.utils.containsAnyCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

class ServicesSource  {

    private val serviceSample = ServiceDetailed(
        id = 0L,
        name = "Программа быстрого похудения",
        type = ServiceType.TRAINING_PROGRAM,
        generalDescription = "Результат уже через 5 недель",
        authorId = "stroitel",
        authorName = "Тренажёрный зал Строитель",
        authorType = UserType.ORGANIZATION,
        profilePictureUrl = null,
        price = 3200f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair(TrainingProgrammesCategories.KEEPING_FORM, false),
            Pair(TrainingProgrammesCategories.LOSING_WEIGHT, true),
            Pair(TrainingProgrammesCategories.GAINING_MUSCLES_MASS, false),
            Pair(TrainingProgrammesCategories.ELSE, false),
        ),
        isFavourite = false,
        isAcquired = false,
        generalPhotosUrls = mutableListOf("https://best5supplements.com/wp-content/uploads/2017/11/bicep-before-after-1024x536.jpg"),
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
        detailedDescription = "Тренер Наталья. Для связи используйте WhatsApp",
        detailedPhotosUrls = null,
        exercises = listOf()
    )

    private val serviceSample2 = ServiceDetailed(
        id = 1L,
        name = "Набор мышечной массы гантялями",
        type = ServiceType.TRAINING_PROGRAM,
        generalDescription = "Нет денег на зал, но есть гантели дома? - не беда.",
        authorId = "i_volf",
        authorName = "Илья Вольф",
        authorType = UserType.ATHLETE,
        profilePictureUrl = null,
        price = 0f,
        currency = "Rubles",
        categories = hashMapOf(
            Pair(TrainingProgrammesCategories.KEEPING_FORM, false),
            Pair(TrainingProgrammesCategories.LOSING_WEIGHT, false),
            Pair(TrainingProgrammesCategories.GAINING_MUSCLES_MASS, true),
            Pair(TrainingProgrammesCategories.ELSE, false),
        ),
        isFavourite = false,
        isAcquired = true,
        generalPhotosUrls = null,
        rating = 5.0F,
        acquiredNumber = 13,
        reviewsNumber = 2,
        detailedDescription = "Делать надо качсетвенно. Отписываться сюда: ***.com",
        detailedPhotosUrls = null,
        exercises = listOf()
    )

    private val servicesDetailed = mutableListOf(
        serviceSample,
        serviceSample2,
        serviceSample.copy(id = 2L, isFavourite = true, isAcquired = false),
        serviceSample.copy(id = 3L),
        serviceSample.copy(id = 4L),
        serviceSample.copy(id = 5L),
        serviceSample.copy(id = 6L),
        serviceSample.copy(id = 7L),
        serviceSample.copy(id = 8L),
        serviceSample.copy(id = 9L),
        serviceSample.copy(id = 10L),
        serviceSample.copy(id = 11L),
        serviceSample.copy(id = 12L),
        serviceSample.copy(id = 13L),
        serviceSample.copy(id = 14L),
        serviceSample.copy(id = 15L),
        serviceSample.copy(id = 16L),
        serviceSample.copy(id = 17L),
        serviceSample.copy(id = 18L),
        serviceSample.copy(id = 19L),
        serviceSample.copy(id = 20L),
        serviceSample.copy(id = 21L),
        serviceSample.copy(id = 22L),
        serviceSample.copy(id = 23L),
        serviceSample.copy(id = 24L),
        serviceSample.copy(id = 25L),
    )

    private val followersIds = mutableListOf("i_chiesov", "stroitel", "nikita")

    fun getServicesDetailed(
        offset: Int,
        pageSize: Int,
        userId: String
    ): List<ServiceDetailed> {
        Log.d("LSVM", "getServicesDetailed")
        val filteredServices = servicesDetailed.filter { it.authorId == userId }

        return if (offset >= filteredServices.size) {
            listOf()
        } else if (offset + pageSize >= filteredServices.size) {
            filteredServices.subList(offset, filteredServices.size)
        } else {
            filteredServices.subList(offset, offset + pageSize)
        }
    }

    fun setIsFavourite(
        userId: String,
        serviceId: Long,
        isFavourite: Boolean
    ) {
        servicesDetailed.find { it.id == serviceId }?.isFavourite = isFavourite
    }


}