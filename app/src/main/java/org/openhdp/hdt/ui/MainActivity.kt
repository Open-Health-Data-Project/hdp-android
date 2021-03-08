package org.openhdp.hdt.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.dao.StopwatchDAO
import org.openhdp.hdt.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val ACTION_REFRESH_STOPWATCHES = "ACTION_REFRESH_STOPWATCHES"
        fun refreshTracking(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                action = ACTION_REFRESH_STOPWATCHES
            }
        }
    }

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var stopwatchDAO: StopwatchDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
        }
    }
}