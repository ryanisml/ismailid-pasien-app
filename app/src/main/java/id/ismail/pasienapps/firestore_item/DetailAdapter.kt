package id.ismail.pasienapps.firestore_item

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TableRow
import id.ismail.pasienapps.R
import android.widget.TextView
import java.util.ArrayList

class DetailAdapter(private var itemList: ArrayList<DetailItem>?) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    @JvmName("setItemList1")
    fun setItemList(itemList: ArrayList<DetailItem>?) {
        this.itemList = itemList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList!![position]
        holder.tvNomor.text = item.nomor_antrian
        if (item.tanggal_pemeriksaan != null) {
            if (item.tanggal_pemeriksaan == "") {
                holder.tvTglPeriksa.text = item.tanggal_reservasi
            } else {
                holder.tvTglPeriksa.text = item.tanggal_pemeriksaan
            }
        } else {
            holder.tvTglPeriksa.text = item.tanggal_pemeriksaan
        }
        holder.tvKeluhan.text = item.keluhan
        reservasiStatus(holder.tvStatus, item.status_reservasi)
        if (item.status_reservasi == 4) {
            holder.trObat.visibility = View.VISIBLE
            holder.tvObat.text = item.obat
            holder.trHasil.visibility = View.VISIBLE
            holder.tvHasil.text = item.saran
            holder.trDokter.visibility = View.VISIBLE
            holder.tvDokter.text = item.nama_dokter
        } else {
            holder.trObat.visibility = View.GONE
            holder.trHasil.visibility = View.GONE
            holder.trDokter.visibility = View.GONE
        }
    }

    private fun reservasiStatus(tv_status: TextView, status_reservasi: Int) {
        var warna = R.color.btn_default
        var status = "Gagal load data"
        when (status_reservasi) {
            1 -> {
                warna = R.color.btn_info
                status = "Menunggu Panggilan"
            }
            2 -> {
                warna = R.color.btn_primary
                status = "Sedang dipanggil"
            }
            3 -> {
                warna = R.color.btn_danger
                status = "Tidak Datang"
            }
            4 -> {
                warna = R.color.btn_success
                status = "Telah Selesai"
            }
            5 -> {
                warna = R.color.btn_warning
                status = "Dibatalkan"
            }
        }
        tv_status.setBackgroundResource(warna)
        tv_status.text = status
    }

    override fun getItemCount(): Int {
        return if (itemList == null) {
            0
        } else {
            itemList!!.size
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNomor: TextView
        var tvObat: TextView
        var tvHasil: TextView
        var tvKeluhan: TextView
        var tvStatus: TextView
        var tvTglPeriksa: TextView
        var tvDokter: TextView
        var trHasil: TableRow
        var trObat: TableRow
        var trTanggal: TableRow
        var trDokter: TableRow

        init {
            tvNomor = itemView.findViewById(R.id.tv_nomor)
            tvObat = itemView.findViewById(R.id.tv_obat)
            tvHasil = itemView.findViewById(R.id.tv_hasil)
            tvKeluhan = itemView.findViewById(R.id.tv_keluhan)
            tvStatus = itemView.findViewById(R.id.tv_status)
            tvTglPeriksa = itemView.findViewById(R.id.tv_tanggal_periksa)
            tvDokter = itemView.findViewById(R.id.tv_dokter)
            trHasil = itemView.findViewById(R.id.tr_hasil)
            trObat = itemView.findViewById(R.id.tr_obat)
            trTanggal = itemView.findViewById(R.id.tr_tanggal)
            trDokter = itemView.findViewById(R.id.tr_dokter)
        }
    }
}