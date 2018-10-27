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
import android.widget.AdapterView
import com.example.aufa.project.data.Sales
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_list_karyawan.*
import kotlinx.android.synthetic.main.karyawan_layout.view.*
import kotlinx.android.synthetic.main.tambah_sales.*

/**
 * Created by aufa on 17/03/18.
 */
class ListSales : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_karyawan)
        viewManager = LinearLayoutManager(this)


        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_karyawan).apply {
            setHasFixedSize(true)
            layoutManager = viewManager

        }
        listSales()

        addkaryawan.setOnClickListener {
            val intent = Intent(this, TambahSales::class.java)
            startActivity(intent)
        }

    }

    fun listSales() {
        val database = FirebaseDatabase.getInstance().getReference("sales")

        database.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val item = mutableListOf<Sales>()
                p0?.children?.forEach {
                    val content = (it.value as? HashMap<String, Any>)

                    val sales = Sales(
                            content?.get("nama")?.toString() ?: "",
                            content?.get("email")?.toString() ?: "",
                            photoURL = content?.get("photoURL")?.toString() ?: "",
                            key = it.key
                    )
                    item.add(sales)

                    Log.e("list", "${content?.get("username")}")

                }
                recyclerView.adapter = ListSales.MyAdapter(item)
            }

        })
    }



    class MyAdapter(private val myDataset: List<Sales>) :
            RecyclerView.Adapter<ListSales.MyAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun setJob(job: Sales) {
                view.txtNamaKaryawan.text = job.nama
                view.txtEmail.text = job.email
                if (!job.photoURL.isNullOrBlank()) {
                    Picasso.with(this.view.context).load(job.photoURL).into(view.foto)
                } else {
                    Picasso.with(this.view.context).load(R.drawable.placeholder_camera_green).into(view.foto)
                }
                view.setOnClickListener{
                   val i = Intent(view.context,ListDatabase::class.java)
                    i.putExtra("id",job.email)
                    view.context.startActivity(i)

                }
                view.setOnLongClickListener {
                    AlertDialog.Builder(view.context)

                            .setTitle(job.nama)
                            .setMessage("Apa yang ingin anda lakukan dengan Sales ini?")
                            .setPositiveButton("Edit", { dialog, which ->
                                val intent = Intent(view.context, TambahSales::class.java)
                                intent.putExtra("key", job.key)
                                view.context.startActivity(intent)
                            })
                            .setNegativeButton("Hapus", { dialog, which ->
                                FirebaseDatabase.getInstance().getReference("sales").child(job.key).setValue(null)
                            })

                            .show()
                    true
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ListSales.MyAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.karyawan_layout, parent, false)
            return ListSales.MyAdapter.ViewHolder(v)
        }


        override fun onBindViewHolder(holder: ListSales.MyAdapter.ViewHolder, position: Int) {
            holder.setJob(myDataset[position])

            if (position % 2 == 1) {
                holder.view.setBackgroundColor(Color.rgb(240, 240, 240))
            } else {
                holder.view.setBackgroundColor(Color.rgb(255, 255, 255))

            }

        }

        override fun getItemCount() = myDataset.size
    }

}
