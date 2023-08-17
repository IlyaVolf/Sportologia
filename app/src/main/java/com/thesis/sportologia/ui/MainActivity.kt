package com.thesis.sportologia.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.thesis.sportologia.R
import com.thesis.sportologia.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * Container for all screens in the app.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // view-model is used for observing username to be displayed in the toolbar
    private val viewModel by viewModels<MainActivityViewModel>()

    // nav controller of the current screen
    private var navController: NavController? = null

    private val topLevelDestinations = setOf(getTabsDestination(), getSignInDestination())

    // fragment listener is sued for tracking current nav controller
    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        val locale = Locale("ru", "RU")
        Locale.setDefault(locale)

        val navController = getRootNavController()
        prepareRootNavController(isSignedIn(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        MapKitFactory.setApiKey("9eb8aa69-aac3-42cb-b67f-fbe4c5bff23b")
        //MapKitFactory.initialize(this)

        /*navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id) {
                R.id.profileFragment -> {
                    val argument = NavArgument.Builder().setDefaultValue(CurrentAccount().id).build()
                    destination.addArgument("userId", argument)
                }
            }
        }*/

        /*val fragment = FilterFragment()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }*/

        /*val tf = Typeface.createFromAsset(
            assets,
            "fonts/SourceSansPro/SourceSansPro-Bold.ttf"
        )
        val tv1 = findViewById<View>(R.id.title) as TextView
        val tv2 = findViewById<View>(R.id.account_type) as TextView
        tv1.typeface = tf
        tv2.typeface = tf*/

        // access the spinner
        /*val accountTypes = listOf(getString(R.string.athlete), getString(R.string.organization))
        val spinnerBlockAccountType = findViewById<SpinnerBasicView>(R.id.spinner_account_type)
        spinnerBlockAccountType.initAdapter(accountTypes, getString(R.string.hint_account_type))

        val genders = listOf(getString(R.string.male), getString(R.string.female))
        val spinnerBlockGender = findViewById<SpinnerBasicView>(R.id.spinner_gender)
        spinnerBlockGender.initAdapter(genders, getString(R.string.hint_gender))*/

    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isStartDestination(navController?.currentDestination)) {
            super.onBackPressed()
        } else {
            navController?.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        (navController?.navigateUp() ?: false) || super.onSupportNavigateUp()

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if (isSignedIn) {
                getTabsDestination()
            } else {
                getSignInDestination()
            }
        )
        navController.graph = graph
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private fun getRootNavController(): NavController {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        return navHost.navController
    }

    private val destinationListener =
        NavController.OnDestinationChangedListener { _, destination, arguments ->
            supportActionBar?.title = destination.label
            supportActionBar?.setDisplayHomeAsUpEnabled(!isStartDestination(destination))
        }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestinations = topLevelDestinations + graph.startDestinationId
        return startDestinations.contains(destination.id)
    }

    private fun isSignedIn(): Boolean {
        val bundle = intent.extras ?: throw IllegalStateException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isSignedIn
    }

    private fun getMainNavigationGraphId(): Int = R.navigation.main_graph

    private fun getTabsDestination(): Int = R.id.tabsFragment

    private fun getSignInDestination(): Int = R.id.signInFragment
}