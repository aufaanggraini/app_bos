package com.example.aufa.project.data

/**
 * Created by aufa on 22/11/17.
 */
data class User(
        val username: String = "",
        val email: String = "",
        val password: String = ""
)

data class Kerjaan(
        var nama: String = "",
        var durasi: String? = "",
        var startTime: Long? = null,
        var endTime: Long? = null,
        var lokasi: String = "",
        var key: String? = null,
        var alasan: String = ""
)

data class Sales(
        var nama: String = "",
        var email: String = "",
        var password: String = "",
        var photoURL: String? = null,
        var username: String? =null,
        var key: String? = null
)

open class Kredit {
    var nama: String? = ""
    var alamat: String? = ""
    var idsales: String? = ""
    var photoUrl: String? = ""
    var dokumenLengkap: Int = 0
    var siapSurvey: Int = 0
    var key: String? = null
    var createdAt: Long = System.currentTimeMillis()
}

