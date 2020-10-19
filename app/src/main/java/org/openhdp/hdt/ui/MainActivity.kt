package org.openhdp.hdt.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.dao.StopwatchDAO
import org.openhdp.hdt.databinding.ActivityMainBinding
import org.openhdp.hdt.other.Constants
import org.openhdp.hdt.other.Constants.ACTION_START_SERVICE
import org.openhdp.hdt.services.StopwatchService
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var stopwatchDAO: StopwatchDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.d("STPDAO: ${stopwatchDAO.hashCode()}")

        setupNavigation()

        sendCommandToService(ACTION_START_SERVICE, 10)
    }

    private fun setupNavigation() {
        val navHostFragment = requireNotNull(
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        )
        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )
    }

    private fun sendCommandToService(action: String, stopwatchId: Int) =
        Intent(this, StopwatchService::class.java).also {
            it.action = action
            it.putExtra(Constants.STOPWATCH_ID, stopwatchId)
            this.startService(it)
        }
}