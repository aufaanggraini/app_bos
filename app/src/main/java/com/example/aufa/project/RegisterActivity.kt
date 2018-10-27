package com.example.aufa.project

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.aufa.project.data.User
import kotlinx.android.synthetic.main.register.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.database_layout.view.*


/**
 * Created by aufa on 22/11/17.
 */
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        tvSudahPunyaAkun.text
        txtRegPassword.text
        txtRegEmail.text
        txtRegNama.text

        tvSudahPunyaAkun.setOnClickListener {
            bukaMenuLogin()

        }

        btnRegister.setOnClickListener {
            simpanUserBaru()


        }

    }

    fun bukaMenuLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun simpanUserBaru() {
        val nama = txtRegNama.text.toString()
        val email = txtRegEmail.text.toString()
        val password = txtRegPassword.text.toString()

        if (nama.isNullOrBlank()) {
            Toast.makeText(this@RegisterActivity, "Tidak boleh kosong", Toast.LENGTH_LONG).show()
        }else if (email.isNullOrBlank()) {
            Toast.makeText(this@RegisterActivity, "Tidak boleh Kosong", Toast.LENGTH_LONG).show()
        } else if(password.isNullOrBlank()) {
            Toast.makeText(this@RegisterActivity, "Tidak boleh Kosong", Toast.LENGTH_LONG).show()
        } else {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("users").child("$nama")

            myRef.setValue(User(nama, email, password))

            txtRegNama.text.clear()
            txtRegEmail.text.clear()
            txtRegPassword.text.clear()
        }

    }

}
