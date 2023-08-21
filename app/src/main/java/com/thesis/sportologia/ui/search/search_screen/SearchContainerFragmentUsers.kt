package com.thesis.sportologia.ui.search.search_screen

/**import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.FragmentSearchContainerBinding
import com.thesis.sportologia.ui.map.MapFragment
import com.thesis.sportologia.ui.users.list_users_screen.ListUsersFragmentSearch

/ class SearchContainerFragmentUsers : Fragment() {

    private lateinit var userId: String
    private lateinit var binding: FragmentSearchContainerBinding

    private lateinit var listFragment: ListUsersFragmentSearch
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = arguments?.getString("userId") ?: throw Exception()

        listFragment = ListUsersFragmentSearch.newInstance(userId)
        mapFragment = MapFragment.newInstance(SWITCH_TO_LIST_REQUEST_CODE_USERS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchContainerBinding.inflate(inflater, container, false)

        initContainerView()
        initOnResultListener()

        return binding.root
    }

    private fun initContainerView() {
        childFragmentManager.beginTransaction().add(
            R.id.fragmentContainerView,
            mapFragment
        ).commit()
    }

    private fun initOnResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(
            SWITCH_TO_LIST_REQUEST_CODE_USERS,
            viewLifecycleOwner
        ) { _, _ ->
            childFragmentManager.beginTransaction().replace(
                R.id.fragmentContainerView,
                listFragment
            ).commit()
        }
    }

    companion object {
        // TODO можно создать переменную: обновлять ли адаптер в прицнипе. А также скрывать при переходе в другой экран для оптимизации
        fun newInstance(userId: String): SearchContainerFragmentUsers {
            val myFragment = SearchContainerFragmentUsers()
            val args = Bundle()
            args.putString("userId", userId)
            myFragment.arguments = args
            return myFragment
        }

        const val SWITCH_TO_LIST_REQUEST_CODE_USERS = "SWITCH_TO_LIST_REQUEST_CODE_USERS"
    }


}*/