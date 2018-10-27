package com.example.aufa.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.aufa.project.data.Sales
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.tambah_sales.*
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


/**
 * Created by aufa on 10/12/17.
 */
class TambahSales : AppCompatActivity() {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    var sales: Sales? = null
    var selectedPhoto: Bitmap? = null
    var defaultPhotoURL: String? = null
    var storage = FirebaseStorage.getInstance()
    val database = FirebaseDatabase.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambah_sales)
        val key = intent.getStringExtra("key")
        verifyStoragePermissions(this)


        key?.let {
            getSales(it)
        }

        btnTambah.setOnClickListener {
            tambahSales()
        }


        btnCancel.setOnClickListener {
            finish()
        }

        pilihPhoto.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 99)
        }

        resetPhoto.setOnClickListener {
            pilihPhoto.setImageResource(R.drawable.placeholder_camera_green)
            selectedPhoto = null
        }
        disableAllViews(true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99 && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            selectedPhoto = BitmapFactory.decodeFile(picturePath)
            pilihPhoto.setImageBitmap(selectedPhoto)
        }
    }

    private fun getSales(key: String) {
        val db = database.getReference("sales")

        db.child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(sl: DataSnapshot?) {
                val data = (sl?.value as? HashMap<String, Any>)

                Log.e("sales", "$data")

                sales = Sales(
                        username = data?.get("username")?.toString() ?: "",
                        nama = data?.get("nama")?.toString() ?: "",
                        email = data?.get("email")?.toString() ?: "",
                        password = data?.get("password")?.toString() ?: "",
                        photoURL = data?.get("photoURL")?.toString() ?: "",
                        key = sl?.key
                )
                txtdafusername.setText(sales?.username)
                txtdafNama.setText(sales?.nama)
                txtdafEmail.setText(sales?.email)
                txtdafPassword.setText(sales?.password)
                btnTambah.text = "Ubah"
                defaultPhotoURL = sales?.photoURL

                if (sales?.photoURL != null) {
                    Picasso.with(this@TambahSales).load(defaultPhotoURL).into(pilihPhoto)
                }
                if (sales?.photoURL == null) {
                    Picasso.with(this@TambahSales).load(R.drawable.placeholder_camera_green).into(pilihPhoto)
                }
            }

        })
    }

    var uploading: Boolean = false

    fun tambahSales() {
        if (!valid()) return

        val salesid = database.getReference("sales").push().key

        if (selectedPhoto != null) {
            // upload photo
            uploading = true
            uploadPhoto(sales?.key ?: salesid)
        }
        simpanData(sales?.key ?: salesid)
    }

    private fun valid(): Boolean {
        if (txtdafNama.text.toString().isBlank()) {
            Toast.makeText(this, "Nama tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }

        if (txtdafusername.text.toString().isBlank()) {
            Toast.makeText(this, "User Name sales tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }
        if (txtdafEmail.text.toString().isBlank()) {
            Toast.makeText(this, "Username sales tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }
        if (txtdafPassword.text.toString().isBlank()) {
            Toast.makeText(this, "Password sales tidak boleh Kosong", Toast.LENGTH_LONG).show()
            return false
        }


        return true
    }

    private fun simpanData(s: String) {
        if (uploading) return
        val username = txtdafusername.text.toString()
        val nama = txtdafNama.text.toString()
        val email = txtdafEmail.text.toString()
        val password = txtdafPassword.text.toString()

        val myRef = database.getReference("sales").child("${email.replace(".", "")}")

        sales?.let {
            it.username = username
            it.nama = nama
            it.email = email
            it.password = password
            it.key = null
            it.photoURL = defaultPhotoURL
        } ?: kotlin.run {
            sales = Sales(
                    nama = nama,
                    email = email,
                    password = password,
                    photoURL = defaultPhotoURL,
                    username = username

            )
        }

        myRef.setValue(sales)

        txtdafNama.text.clear()
        txtdafEmail.text.clear()
        txtdafPassword.text.clear()

        finish()

    }

    private fun uploadPhoto(salesid: String) {
        disableAllViews()
        progress.visibility = View.VISIBLE
        pilihPhoto.visibility = View.GONE
        val baos = ByteArrayOutputStream()
        selectedPhoto?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imageRef = storage.reference.child("sales/${salesid}.jpg")

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
            progress.visibility = View.GONE
            pilihPhoto.visibility = View.VISIBLE
            Toast.makeText(this, "gagal upload", Toast.LENGTH_LONG).show()
            uploading = false
        }).addOnSuccessListener {
            uploading = false
            progress.visibility = View.GONE
            pilihPhoto.visibility = View.VISIBLE
            defaultPhotoURL = it.downloadUrl.toString()
            simpanData(salesid)
        }
    }

    private fun disableAllViews(enable: Boolean = false) {
        pilihPhoto.isEnabled = enable
        resetPhoto.isEnabled = enable
        txtdafEmail.isEnabled = enable
        txtdafNama.isEnabled = enable
        txtdafPassword.isEnabled = enable
        btnTambah.isEnabled = enable
        btnCancel.isEnabled = enable
    }

    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}



