package com.example.aufa.project

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.aufa.project.data.Kerjaan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_list_kerja_sales.*
import kotlinx.android.synthetic.main.kerjaan_lyout.view.*
import java.text.SimpleDateFormat


/**
 * Created by aufa on 13/03/18.
 */
class ListPekerjaan : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_kerja_sales)

        viewManager = LinearLayoutManager(this)


        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_kerjaan).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }
        progress.visibility = View.VISIBLE
        listPekerjaan()

        addJob.setOnClickListener {
            val intent = Intent(this, TambahKerja::class.java)
            startActivity(intent)
        }
    }

    fun listPekerjaan() {
        val database = FirebaseDatabase.getInstance().getReference("list_kerja")

        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                progress.visibility = View.GONE
                val item = mutableListOf<Kerjaan>()
                p0?.children?.forEach {
                    val content = (it.value as? HashMap<String, Any>)
//                    val content = data?.values?.firstOrNull() as? HashMap<String, Any>
                    val kerjaan = Kerjaan(
                            content?.get(Kerjaan::nama.name)?.toString() ?: "",
                            content?.get(Kerjaan::durasi.name)?.toString() ?: "",
                            content?.get(Kerjaan::startTime.name)?.toString()?.toLong() ?: -1,
                            content?.get(Kerjaan::endTime.name)?.toString()?.toLong() ?: -1,
                            content?.get(Kerjaan::lokasi.name)?.toString() ?: "",
                            it.key
                    )
                    item.add(kerjaan)
                    Log.e("list", "${content?.get("nama")}")
                }

                if (item.isEmpty()) {
                    noPekerjaan.visibility = View.VISIBLE
                } else {
                    noPekerjaan.visibility = View.GONE
                }

                recyclerView.adapter = MyAdapter(item)
            }
        })
    }


    class MyAdapter(private val myDataset: List<Kerjaan>) :
            RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun setJob(job: Kerjaan) {
                view.txtNama.text = job.nama
                view.txtLokasi.text = job.lokasi
                view.txtDurasi.text = job.durasi
                getPenugasan(view.txtPenugasan, job)
                view.setOnClickListener {
                    val i = Intent(view.context, PenugasanSalesActivity::class.java)
                    i.putExtra("jobid", job.key)
                    view.context.startActivity(i)
                }

                view.setOnLongClickListener {
                    AlertDialog.Builder(view.context)
                            .setTitle(job.nama)
                            .setMessage("Apa yang ingin anda lakukan dengan job ini?")
                            .setPositiveButton("Edit", { dialog, which ->
                                val intent = Intent(view.context, TambahKerja::class.java)
                                intent.putExtra("key", job.key)
                                view.context.startActivity(intent)
                            })
                            .setNegativeButton("Hapus", { dialog, which ->
                                FirebaseDatabase.getInstance().getReference("list_kerja").child(job.key).setValue(null)
                            })
                            .show()
                    true
                }
            }

            private fun getPenugasan(txtPenugasan: TextView, job: Kerjaan) {
                val database = FirebaseDatabase.getInstance().getReference("list_kerja").child(job.key).child("penugasan")

                database.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {}

                    override fun onDataChange(p0: DataSnapshot?) {
                        var nama = "Penugasan: "
                        p0?.children?.forEach {
                            nama += it.value.toString() + ", "
                        }
                        txtPenugasan.text = nama
                    }
                })
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.kerjaan_lyout, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setJob(myDataset[position])
            if (position % 2 == 0) {
                holder.view.setBackgroundColor(Color.rgb(240, 240, 240))
            } else {
                holder.view.setBackgroundColor(Color.rgb(255, 255, 255))

            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }


}



