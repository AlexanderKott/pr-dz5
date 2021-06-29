package dz.mydz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StatesView>(R.id.states)
        val runnable = object : Runnable {
            override fun run() {
                view.data = arrayOf(
                    200F, 200F, 190F, 190F
                )
              //  Handler(Looper.getMainLooper()).postDelayed(this, 2500)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}