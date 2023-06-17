package com.mawumbo.mystoryapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.mawumbo.mystoryapp.data.preferences.LoginSession
import com.mawumbo.mystoryapp.databinding.ActivityMainBinding
import com.mawumbo.mystoryapp.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var loginSession: LoginSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        navController = navHostFragment.navController

        if (checkLoginSession()) {
            navController.navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }

    }

    private fun checkLoginSession() = runBlocking(Dispatchers.IO) {
        (loginSession.loginSessionFlow.first().isEmpty())

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

fun NavController.checkCurrentDestinationBeforeNavigateAction(
    currentDestinationId: Int,
    action: NavDirections
) {
    if (currentDestination == findDestination(currentDestinationId)) {
        navigate(action)
    }
}