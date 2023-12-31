package com.tech.foodzilla.util.extention

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tech.foodzilla.R
import com.tech.foodzilla.presentation.MainActivity

fun Activity.hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}

fun MainActivity.showBottomNav(){
    val navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    if(!navigation.isShown)
        navigation.show()
}

fun MainActivity.hideBottomNav(){
    val navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    navigation.hide()
}

fun Activity.hideKeyboard(editText: EditText){
    editText.clearFocus()
    val inputMethodManager: InputMethodManager? =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
}