package com.example.aufa.project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.aufa.project.data.Sales
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sales_history.*
import java.text.SimpleDateFormat
import java.util.*

class SalesHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_history)
        title = "History"

        val key = intent.getStringExtra("salesid")
        loadSales(key)
        loadSalesTugas(key)
        loadSalesMap(key)
    }

    private fun loadSalesMap(key: String?) {
        val currentDate = Calendar.getInstance()
        val tanggal = SimpleDateFormat("yyyyMMdd").format(currentDate.time)
        val db = FirebaseDatabase.getInstance().getReference("loc_his").child(key).child(tanggal)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                var path = ""
                var marker = ""
                p0?.children?.forEachIndexed { i, d ->
                    val lat = d.child("latitude").value.toString().toDouble()
                    val lng = d.child("longitude").value.toString().toDouble()
                    if (i == 0) {
                        marker += "&markers=color:blue%7Clabel:A%7C$lat,$lng"
                    }
                    if (i == p0.children.count()-1) {
                        marker += "&markers=color:green%7Clabel:B%7C$lat,$lng"
                    }
                    path += "$lat,$lng" + if (i < p0.children.count() -1) "|" else ""
                }
                val url = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&path=$path&sensor=false&key=AIzaSyCbMONY7r0WJLqSKV07xSZlKzAxs55GAo0$marker"

                android.util.Log.e("map url", url)
                Picasso.with(this@SalesHistoryActivity).load(url).into(imgPeta)
            }
        })
    }

    private fun loadSalesTugas(key: String?) {
        val db = FirebaseDatabase.getInstance().getReference("list_kerja")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                var tugas = ""
                p0?.children?.forEach {
                    if(it.child("penugasan")?.hasChild(key) ?: false) {
                        val status = it.child("penugasan").child(key).value.toString()
                        var alasan = ""
                        val icon = if (status == "0") String(Character.toChars(0x2B1C))
                        else if (status == "1") String(Character.toChars(0x2705))
                        else {
                            alasan = status
                            String(Character.toChars(0x274E))
                        }

                        tugas +=  icon + " " +
                                it.child("nama").value.toString() +
                                "${if (!alasan.isNullOrBlank()) " : "  +alasan else ""}\n"
                    }
                }
                tugasTugas.text = tugas
            }
        })
    }

    private fun loadSales(key: String?) {
        val db = FirebaseDatabase.getInstance().getReference("sales").child(key)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }


            override fun onDataChange(p0: DataSnapshot?) {
                val sales = p0?.getValue(Sales::class.java)
                txtNamaKaryawan.text = sales?.nama
                txtEmail.text = sales?.email
                if (!sales?.photoURL.isNullOrBlank()) {
                    Picasso.with(this@SalesHistoryActivity).load(sales?.photoURL).into(foto)
                } else {
                    Picasso.with(this@SalesHistoryActivity).load(R.drawable.placeholder_camera_green).into(foto)
                }

                title = "Detail History ${sales?.nama}"
            }
        })
    }
}
