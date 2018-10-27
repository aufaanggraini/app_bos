package com.example.aufa.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.aufa.project.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_halaman_utama.*
import kotlinx.android.synthetic.main.app_bar_halaman_utama.*
import kotlinx.android.synthetic.main.nav_header_halaman_utama.*


class HalamanUtama : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_halaman_utama)
        setSupportActionBar(toolbar)

        val prefs = getSharedPreferences("userlogin", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "")

//        val username = intent.getStringExtra("username")

        val database = FirebaseDatabase.getInstance().getReference("users")

        database.child(username).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val user = p0?.getValue(User::class.java)

                if (!user?.username.isNullOrEmpty()) {
                    val u = user?.username
                    val e = user?.email
                    txtNamaLogin.text = u
                    txtEmailLogin.text = e
                } else {

                }
            }

        })

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.halaman_utama, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logout()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_sales -> {
                listSales()

            }
            R.id.nav_manage -> {
                maps()

            }
            R.id.list_kerja -> {
                list_kerja()
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun listSales() {
        val intent = Intent(this@HalamanUtama, ListSales::class.java)
        startActivity(intent)
    }

    fun maps() {
        val intent = Intent(this@HalamanUtama, MapsActivity::class.java)
        startActivity(intent)
    }
//
//    fun tambahKerja(){
//        val intent = Intent(this@HalamanUtama,TambahKerja::class.java)
//        startActivity(intent)
//    }

    fun logout() {
        val editor = getSharedPreferences("userlogin", Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun list_kerja() {
        val intent = Intent(this@HalamanUtama, ListPekerjaan::class.java)
        startActivity(intent)
    }


}
