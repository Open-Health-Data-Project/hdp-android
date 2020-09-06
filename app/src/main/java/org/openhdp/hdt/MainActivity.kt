package org.openhdp.hdt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import org.openhdp.hdt.data.dao.StopwatchDAO
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var stopwatchDAO: StopwatchDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("stopwatchDao","STPDAO: ${stopwatchDAO.hashCode()}")
    }
}