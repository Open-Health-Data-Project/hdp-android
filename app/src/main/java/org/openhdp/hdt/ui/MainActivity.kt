package org.openhdp.hdt.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.R
import org.openhdp.hdt.data.dao.StopwatchDAO
import org.openhdp.hdt.other.Constants
import org.openhdp.hdt.other.Constants.ACTION_START_SERVICE
import org.openhdp.hdt.services.StopwatchService
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var stopwatchDAO: StopwatchDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("STPDAO: ${stopwatchDAO.hashCode()}")
        sendCommandToService(ACTION_START_SERVICE,10)
    }
    private fun sendCommandToService(action: String, stopwatchId: Int) =
        Intent(this, StopwatchService::class.java).also {
            it.action = action
            it.putExtra(Constants.STOPWATCH_ID, stopwatchId)
            this.startService(it)
        }
}