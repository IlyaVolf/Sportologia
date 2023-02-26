package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentProfileBinding
import com.thesis.sportologia.ui.views.*
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val contentTabs = binding.root.findViewById<ContentTabsView>(R.id.contentTabs)
        contentTabs.setListener {
            
        }

        contentTabs.setButtonText(1, "Игорь")
        contentTabs.setCount(1, 23)

        val choices = listOf(getString(R.string.posts_all), getString(R.string.posts_upcoming))
        val spinner = binding.root.findViewById<SpinnerOnlyOutlinedView>(R.id.spinner)
        spinner.initAdapter(choices)

        val post = binding.root.findViewById<ItemPostView>(R.id.item_post)
        post.setListener {  }
        post.setUsername("Игорь Чиёсов")
        post.setText("Привет")
        post.setLikes(311331, true)
        post.setFavs(true)
        post.setAvatar("https://i.imgur.com/tGbaZCY.jpg")


        val review = binding.root.findViewById<ItemReviewView>(R.id.item_review)
        review.setListener {  }
        review.setUsername("Игорь Чиёсов")
        review.setTitle("Много воды")
        review.setDescription("Я Игорь, я люблю Андрея")
        review.setRating(2)
        review.setAvatar("https://i.imgur.com/tGbaZCY.jpg")

        val event = binding.root.findViewById<ItemEventView>(R.id.item_event)
        event.setListener {  }
        event.setLikes(25363636, true)
        event.setFavs(false)
        event.setOrganizerName("Игорь Чиёсов")
        event.setDescription(getString(R.string.test_text))
        event.setPrice("0", getString(R.string.ruble_abbreviation))
        event.setAvatar("https://i.imgur.com/tGbaZCY.jpg")

        val service = binding.root.findViewById<ItemServiceView>(R.id.item_service)
        service.setListener {  }
        service.setFavs(false)
        service.setOrganizerName("Игорь Чиёсов")
        service.setDescription(getString(R.string.test_text))
        service.setPrice("4224", getString(R.string.ruble_abbreviation))
        service.setAvatar("https://i.imgur.com/tGbaZCY.jpg")

        return binding.root
    }
}