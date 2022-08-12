package id.ismail.pasienapps

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ismail.pasienapps.lib.SharedPasien
import com.google.firebase.firestore.FirebaseFirestore
import id.ismail.pasienapps.firestore_item.DetailAdapter
import id.ismail.pasienapps.firestore_item.DetailItem
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import id.ismail.pasienapps.lib.LibHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private var tvNoktp: TextView? = null
    private var tvBpjs: TextView? = null
    private var tvNama: TextView? = null
    private var tvJumlah: TextView? = null
    private var rvItem: RecyclerView? = null
    private var ivNodata: ImageView? = null
    private var sharedPasien: SharedPasien? = null
    private var mContext: Context? = null
    private var loading: Dialog? = null
    private var db: FirebaseFirestore? = null
    private var detailAdapter: DetailAdapter? = null
    private val detailItems = ArrayList<DetailItem>()
    private var idpas = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        mContext = this
        sharedPasien = SharedPasien(mContext as HistoryActivity)
        db = FirebaseFirestore.getInstance()
        initToolbar()
        tvNoktp = findViewById(R.id.tv_noktp)
        tvBpjs = findViewById(R.id.tv_bpjs)
        tvNama = findViewById(R.id.tv_nama)
        tvJumlah = findViewById(R.id.tv_jumlah)
        rvItem = findViewById(R.id.rv_item)
        ivNodata = findViewById(R.id.iv_nodata)
        loading = LibHelper.inisiasiLoading(mContext)
        initComponents()
    }

    @SuppressLint("SetTextI18n")
    private fun initComponents() {
        rvItem!!.layoutManager = LinearLayoutManager(mContext)
        detailAdapter = DetailAdapter(detailItems)
        rvItem!!.adapter = detailAdapter
        idpas = sharedPasien!!.spIduser!!
        tvNoktp!!.text = sharedPasien!!.spNoKtp
        tvBpjs!!.text = "(Tidak ada)"
        tvNama!!.text = sharedPasien!!.spNama
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ivBack = toolbar.findViewById<ImageView>(R.id.iv_back)
        val ivRefresh = toolbar.findViewById<ImageView>(R.id.iv_refresh)
        val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
        ivBack.setOnClickListener { onBackPressed() }
        ivRefresh.setOnClickListener { data }
        tvTitle.text = "History Reservasi".uppercase(Locale.getDefault())
    }

    @get:SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private val data: Unit
        get() {
            tvJumlah!!.text = "0 Reservasi"
            loading!!.show()
            db!!.collection("tb_reservasi").whereEqualTo("idpasien", idpas)
                .orderBy("tanggal_reservasi").orderBy("nomor_antrian")
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        rvItem!!.visibility = View.GONE
                        ivNodata!!.visibility = View.VISIBLE
                        Toast.makeText(
                            mContext,
                            "Listen failed. " + error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        println("Listen " + error.message)
                        if (loading != null) {
                            loading!!.dismiss()
                        }
                        return@addSnapshotListener
                    }
                    detailItems.clear()
                    detailAdapter!!.setItemList(detailItems)
                    detailAdapter!!.notifyDataSetChanged()
                    if (value != null) {
                        if (value.size() > 0) {
                            rvItem!!.visibility = View.VISIBLE
                            ivNodata!!.visibility = View.GONE
                            for (documentSnapshot in value) {
                                val idreservasi = documentSnapshot.id
                                val idpasien =
                                    Objects.requireNonNull(documentSnapshot.data["idpasien"])
                                        .toString()
                                val keluhan =
                                    Objects.requireNonNull(documentSnapshot.data["keluhan"])
                                        .toString()
                                val nomorantrian = Objects.requireNonNull(
                                    documentSnapshot.data["nomor_antrian"]
                                ).toString()
                                val statusreservasi = Objects.requireNonNull(
                                    documentSnapshot.data["status_reservasi"]
                                ).toString().toInt()
                                val tanggalreservasi = Objects.requireNonNull(
                                    documentSnapshot.data["tanggal_reservasi"]
                                ).toString()
                                if (statusreservasi == 4) {
                                    getDetail(
                                        idreservasi,
                                        nomorantrian,
                                        statusreservasi,
                                        tanggalreservasi
                                    )
                                } else {
                                    val ri = DetailItem(
                                        idreservasi,
                                        idpasien,
                                        keluhan,
                                        nomorantrian,
                                        statusreservasi,
                                        tanggalreservasi,
                                        "",
                                        "",
                                        "",
                                        "",
                                        ""
                                    )
                                    detailItems.add(ri)
                                    detailAdapter!!.setItemList(detailItems)
                                    detailAdapter!!.notifyDataSetChanged()
                                }
                            }
                            tvJumlah!!.text = value.size().toString() + " Reservasi"
                        } else {
                            rvItem!!.visibility = View.GONE
                            ivNodata!!.visibility = View.VISIBLE
                            Toast.makeText(mContext, "Tidak ada data ditemukan", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                    if (loading != null) {
                        loading!!.dismiss()
                    }
                }
        }

    private fun getDetail(
        idreservasi: String,
        nomorantrian: String,
        statusreservasi: Int,
        tanggalreservasi: String
    ) {
        db!!.collection("tb_hasil_pemeriksaan").whereEqualTo("id_reservasi", idreservasi).limit(1)
            .get().addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        if (task.result!!.size() > 0) {
                            for (documentSnapshot in task.result!!) {
                                val iddokter = Objects.requireNonNull(
                                    documentSnapshot.data["id_dokter"]
                                ).toString()
                                val keluhan = Objects.requireNonNull(
                                    documentSnapshot.data["keluhan"]
                                ).toString()
                                val obat =
                                    Objects.requireNonNull(documentSnapshot.data["obat"]).toString()
                                val saran = Objects.requireNonNull(
                                    documentSnapshot.data["saran"]
                                ).toString()
                                val tanggalpemeriksaan = Objects.requireNonNull(
                                    documentSnapshot.data["tanggal_pemeriksaan"]
                                ).toString()
                                getDokter(
                                    idreservasi,
                                    keluhan,
                                    nomorantrian,
                                    statusreservasi,
                                    tanggalreservasi,
                                    iddokter,
                                    obat,
                                    saran,
                                    tanggalpemeriksaan
                                )
                                break
                            }
                        }
                    }
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDokter(
        idreservasi: String,
        keluhan: String,
        nomorantrian: String,
        statusreservasi: Int,
        tanggalreservasi: String,
        iddokter: String,
        obat: String,
        saran: String,
        tanggal_pemeriksaan: String
    ) {
        db!!.collection("tb_dokter").document(iddokter).get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val ri = DetailItem(
                            idreservasi,
                            idpas,
                            keluhan,
                            nomorantrian,
                            statusreservasi,
                            tanggalreservasi,
                            iddokter,
                            Objects.requireNonNull(task.result!!["nama"]).toString(),
                            obat,
                            saran,
                            tanggal_pemeriksaan
                        )
                        detailItems.add(ri)
                        detailAdapter!!.setItemList(detailItems)
                        detailAdapter!!.notifyDataSetChanged()
                    }
                }
            }
    }
}