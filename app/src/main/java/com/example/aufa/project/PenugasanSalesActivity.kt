package com.example.aufa.project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aufa.project.data.Sales
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_penugasan_sales.*
import kotlinx.android.synthetic.main.karyawan_item_checkbox.view.*

class PenugasanSalesActivity : AppCompatActivity() {
    var listKaryawan: List<Sales> = listOf()
    var selectedKaryawan: MutableSet<Sales> = mutableSetOf()
    var selectedKaryawanKeys: MutableSet<String> = mutableSetOf()

    var adapter: SalesAdapter = SalesAdapter()
    lateinit var jobID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penugasan_sales)

        title = "Penugasan Job"
        jobID = intent.getStringExtra("jobid")
        if (jobID == null) return

        btnBatal.setOnClickListener {
            finish()
        }

        btnSimpan.setOnClickListener {
            simpanPenugasan()
        }

        loadDataKaryawan()
        loadPenugasan()
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

    private fun simpanPenugasan(){
        val data = mutableMapOf<String, String>()
        selectedKaryawan.forEach {
            data.put(it.email.replace(".", ""), "0")
        }
        val myRef = FirebaseDatabase.getInstance().getReference("list_kerja").child(jobID)
        myRef.child("penugasan").setValue(data)
        finish()
    }

    fun loadPenugasan() {
        val myRef = FirebaseDatabase.getInstance().getReference("list_kerja").child(jobID)
        myRef.child("penugasan").addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        p0?.children?.forEach { ch ->
                            selectedKaryawanKeys.add(ch.key)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
        )
    }


    fun loadDataKaryawan() {
        val database = FirebaseDatabase.getInstance().getReference("sales")

        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val item = mutableListOf<Sales>()
                p0?.children?.forEach {
                    val content = it.getValue(Sales::class.java)
                    if (content!= null)
                        item.add(content)
                }
                listKaryawan = item
                adapter.notifyDataSetChanged()
            }
        })
    }

    inner class SalesAdapter: RecyclerView.Adapter<SalesAdapter.VH>() {
        override fun getItemCount(): Int = listKaryawan.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
            return VH(LayoutInflater.from(this@PenugasanSalesActivity).inflate(R.layout.karyawan_item_checkbox, parent, false))
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            holder?.setKaryawan(listKaryawan[position])
        }

        inner class VH(val view: View): RecyclerView.ViewHolder(view) {
            fun setKaryawan(sales: Sales) {
                if(selectedKaryawanKeys.filter {
                    it == sales.email.replace(".","")
                }.isNotEmpty()) {
                    selectedKaryawan.add(sales)
                }

                view.namaKaryawan.text = sales.nama
                view.checkbox.isChecked = selectedKaryawanKeys.filter {
                    it == sales.email.replace(".","")
                }.isNotEmpty()

                view.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked)
                        selectedKaryawan.add(sales)
                    else
                        selectedKaryawan.remove(sales)
                }
            }
        }
    }
}
