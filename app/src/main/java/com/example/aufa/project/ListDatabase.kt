package com.example.aufa.project

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.aufa.project.data.Kredit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list_database.*
import kotlinx.android.synthetic.main.database_layout.view.*

/**
 * Created by aufa on 19/07/18.
 */
class ListDatabase : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var id :String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_database)
        viewManager = LinearLayoutManager(this)
        id = intent.getStringExtra("id")

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_database).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        listKredit()

    }
    fun listKredit() {
        val database = FirebaseDatabase.getInstance().getReference("kredit")

        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(p0: DataSnapshot?) {
                val item = mutableListOf<Kredit>()
                p0?.children?.forEach {
                    val content = (it.value as? HashMap<String, Any>)

                    val k = Kredit()
                    k.idsales = content?.get(Kredit::idsales.name).toString()
                    k.alamat = content?.get(Kredit::alamat.name).toString()
                    k.nama = content?.get(Kredit::nama.name).toString()
                    k.dokumenLengkap = content?.get(Kredit::dokumenLengkap.name)?.toString()?.toInt() ?: 0
                    k.siapSurvey = content?.get(Kredit::siapSurvey.name)?.toString()?.toInt() ?: 0
                    k.photoUrl = content?.get(Kredit::photoUrl.name).toString()
                    k.key = it.key

                    Log.e("list", "${content?.get("nama")}")
                    Log.e("list", "${content?.get("idsales")}")

                    if (k.idsales.equals(id))
                        item.add(k)
                }

                if(item.isEmpty()) {
                    progress.visibility = View.GONE
                    nodatabse.visibility = View.VISIBLE
                }else {
                    recyclerView.adapter = ListDatabase.MyAdapter(item)
                    progress.visibility = View.GONE
                    nodatabse.visibility = View.GONE
                }
            }
        })
    }
    class MyAdapter(private val myDataset: List<Kredit>) :
            RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun setJob(krd: Kredit) {
                view.txtNamaKreditor.text = krd.nama
                view.txtAlamat.setText( krd.alamat)
                view.txtAlamat.setTextColor(ContextCompat.getColor(view.context,R.color.hitem))
                if (!krd.photoUrl.isNullOrBlank()) {
                    Log.e("list", "${krd.photoUrl}")
                    Picasso.with(this.view.context).load(krd.photoUrl).into(view.foto)
                } else {
                    Picasso.with(this.view.context).load(R.drawable.placeholder_camera_green).into(view.foto)
                }
                krd.idsales?.let {
                    loadSales(it, view.txtNamaSales)
                    view.txtNamaSales.setTextColor(ContextCompat.getColor(view.context,R.color.hitem))

                    Log.e("list", "${it}")
                }
                /*view.setOnClickListener {
                    val intent = Intent(view.context, TambahKredit::class.java)
                    intent.putExtra("key", krd.key)
                    view.context.startActivity(intent)
                }*/

                view.checkDocument.visibility = if(krd.dokumenLengkap == 1) View.VISIBLE else View.GONE
                view.checkSurvey.visibility = if(krd.siapSurvey == 1) View.VISIBLE else View.GONE


            }

            fun loadSales(salesID: String, listener: TextView) {
                listener.text = salesID
                val database = FirebaseDatabase.getInstance().getReference("sales").child(salesID.replace(".", ""))

                database.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        val sales = p0?.child("nama")?.value?.toString()
//                        Log.e("sales", sales)
                        sales?.let {
                            listener.text = it
                        }
                    }
                })
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): MyAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.database_layout, parent, false)
            return MyAdapter.ViewHolder(v)
        }


        override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
            holder.setJob(myDataset[position])

            if (position % 2 == 1) {
                holder.view.setBackgroundColor(Color.rgb(240, 240, 240))
            } else {
                holder.view.setBackgroundColor(Color.rgb(255, 255, 255))

            }

        }

        override fun getItemCount() = myDataset.size
    }


   /* private fun getUserLogin(): String? = getSharedPreferences("userlogin", Context.MODE_PRIVATE)
            .getString("email", null)*/



}