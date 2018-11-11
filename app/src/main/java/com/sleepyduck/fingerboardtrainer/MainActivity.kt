package com.sleepyduck.fingerboardtrainer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val requestHandlers = mutableMapOf<Int, (Intent?) -> Unit>()

    companion object {
        const val REQUEST_CODE_SIGN_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(findNavController(R.id.navHostFragment))
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        requestHandlers.remove(REQUEST_CODE_SIGN_ID)?.invoke(data)
    }

    fun loggedInComplete() = findNavController(R.id.navHostFragment).navigate(R.id.listWorkoutsFragment)

    fun logInFailed(): Unit = TODO("Handle log in failure")

}

fun Fragment.asActivity(callback: MainActivity.() -> Unit) = (activity as? MainActivity)?.callback()
