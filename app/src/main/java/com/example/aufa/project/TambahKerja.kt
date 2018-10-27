package com.example.aufa.project

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.example.aufa.project.data.Kerjaan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_list_kerja.*
import kotlinx.android.synthetic.main.app_bar_halaman_utama.*
import kotlinx.android.synthetic.main.tambah_sales.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by aufa on 16/02/18.
 */
class TambahKerja : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

    var kerjaan: Kerjaan? = null
    var pickedDate: String? = null
    var selectedDateStart: Long? = null
    var selectedDateEnd: Long? = null


    override fun onDateSet(dialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        pickedDate = "$dayOfMonth/$monthOfYear/$year"
        val startCal = Calendar.getInstance().apply {
            set(year, monthOfYear, dayOfMonth, 8, 0, 0)
        }

        val endCal = Calendar.getInstance().apply {
            set(year, monthOfYear, dayOfMonth, 17, 0, 0)
        }
        selectedDateStart = startCal.time.time
        selectedDateEnd = endCal.time.time

        txtDurasiKerja.text = pickedDate
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_kerja)
        setSupportActionBar(toolbar)

        val key = intent.getStringExtra("key")

        key?.let {
            getJob(it)
        }

        btnTambahKerja.setOnClickListener {
            tambahkerja()

        }
        btnCancelKerja.setOnClickListener {

            finish()

        }

        val now = Calendar.getInstance()

        txtDurasiKerja.setOnClickListener {
            val date = DatePickerDialog.Builder(
                    this@TambahKerja,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH))
                    /* ... Set additional options ... */
                    .build()
            date.show(supportFragmentManager, "picker")

        }
    }

    private fun getJob(key: String) {
        val database = FirebaseDatabase.getInstance().getReference("list_kerja")

        database.child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(po: DataSnapshot?) {
                val data = (po?.value as? HashMap<String, Any>)

                kerjaan = Kerjaan(
                        data?.get(Kerjaan::nama.name)?.toString() ?: "",
                        data?.get(Kerjaan::durasi.name)?.toString() ?: "",
                        data?.get(Kerjaan::startTime.name)?.toString()?.toLong() ?: -1,
                        data?.get(Kerjaan::endTime.name)?.toString()?.toLong() ?: -1,
                        data?.get(Kerjaan::lokasi.name)?.toString() ?: "",
                        po?.key
                )

                txtNamaPekerjaan.setText(kerjaan?.nama)
                txtLokasiPekerjaan.setText(kerjaan?.lokasi)
                txtDurasiKerja.text = kerjaan?.durasi
                pickedDate = kerjaan?.durasi
                btnTambahKerja.text = "Ubah"
            }

        })
    }


    fun tambahkerja() {
        if (!valid()) return
        val nama = txtNamaPekerjaan.text.toString()
        val durasi = pickedDate
        val lokasi = txtLokasiPekerjaan.text.toString()
        val database = FirebaseDatabase.getInstance()
        val jobid = database.getReference("list_kerja").push().key

        val myRef = database.getReference("list_kerja").child(kerjaan?.key ?: jobid)

        kerjaan?.let {
            it.nama = nama
            it.durasi = durasi
            it.startTime = selectedDateStart
            it.endTime = selectedDateEnd
            it.lokasi = lokasi
            it.key = null
        } ?: kotlin.run {
            kerjaan = Kerjaan(nama, durasi,selectedDateStart, selectedDateEnd, lokasi)
        }



        myRef.setValue(kerjaan)

        txtNamaPekerjaan.text.clear()
        txtDurasiKerja.text = "Pilih Tanggal"

        finish()

    }

    private fun valid(): Boolean {
        if (txtNamaPekerjaan.text.toString().isBlank()) {
            Toast.makeText(this, "Nama Pekerjan tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }
        if (txtDurasiKerja.text.toString().isBlank()) {
            Toast.makeText(this, "Durasi Pekerjaan tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }
        if (txtLokasiPekerjaan.text.toString().isBlank()) {
            Toast.makeText(this, "Lokasi Pekerjaan tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /*fun cekDate(curent: Long): Boolean {
        val str = "12/08/2018"
        val startTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("$str 08:00:00")
        val startEnd = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("$str 17:00:00")*/

//        return (curent >= startTime.time && curent <= startEnd.time)
    }



/*
fun String.toDateLong(): Long? {
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy").parse(this)
        return sdf.time
    } catch (e: Exception) {
        return null
    }
}*/
