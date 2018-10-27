package com.example.aufa.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.Toast
import com.example.aufa.project.data.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.login.*


/**
 * Created by aufa on 22/11/17.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR)
//        supportActionBar?.hide()
       // actionBar?.hide()
        setContentView(R.layout.login)

        if (!getUserLogin().isNullOrBlank()) {
            startMain()
            return
        }

        txtUsername.text
        txtPassword.text
        btnLogin.text
        tvBelumPunyaAkun.text

        tvBelumPunyaAkun.setOnClickListener {
            bukaMenuRegister()
        }

        btnLogin.setOnClickListener {
            login()
        }
    }


    fun bukaMenuRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun login() {
        val username = txtUsername.text.toString()
        val password = txtPassword.text.toString()
        val database = FirebaseDatabase.getInstance().getReference("users")

        database.child(username).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val user = p0?.getValue(User::class.java)

                if (!user?.username.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, "User dengan username : ${user?.username}, ditemukan", Toast.LENGTH_LONG).show()
                    if (user?.password.equals(password)) {
                        Toast.makeText(this@LoginActivity, "Passwordnya benar", Toast.LENGTH_LONG).show()
                        saveUser(username)
                        startMain()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "User dengan username : ${username} tidak ditemukan", Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    private fun startMain() {
        val intent = Intent(this@LoginActivity, HalamanUtama::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUser(username: String) {
        val editor = getSharedPreferences("userlogin", Context.MODE_PRIVATE).edit()
        editor.putString("username", username)
        editor.apply()
    }

    private fun getUserLogin(): String? = getSharedPreferences("userlogin", Context.MODE_PRIVATE)
            .getString("username", null)

}

