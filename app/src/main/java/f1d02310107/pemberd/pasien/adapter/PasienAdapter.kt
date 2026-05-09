package f1d02310107.pemberd.pasien.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import f1d02310107.pemberd.pasien.R
import f1d02310107.pemberd.pasien.model.Pasien

class PasienAdapter : RecyclerView.Adapter<PasienAdapter.PasienViewHolder>() {

    private var pasienList = mutableListOf<Pasien>()

    fun setData(list: List<Pasien>) {
        pasienList.clear()
        pasienList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasienViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pasien, parent, false)
        return PasienViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasienViewHolder, position: Int) {
        holder.bind(pasienList[position])
        holder.itemView.setOnClickListener {
            val pasien = pasienList[position]
            Toast.makeText(
                holder.itemView.context,
                "Klik: ${pasien.nama}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = pasienList.size

    inner class PasienViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        private val tvTanggalLahir: TextView = itemView.findViewById(R.id.tvTanggalLahir)
        private val tvJenisKelamin: TextView = itemView.findViewById(R.id.tvJenisKelamin)
        private val tvAlamat: TextView = itemView.findViewById(R.id.tvAlamat)
        private val tvNoTelepon: TextView = itemView.findViewById(R.id.tvNoTelepon)

        fun bind(pasien: Pasien) {
            tvNama.text = "Nama: ${pasien.nama}"
            tvTanggalLahir.text = "Tgl Lahir: ${pasien.tanggal_lahir}"

            // mengonversi jenis kelamin L/P menjadi Laki-laki/Perempuan
            val gender = if (pasien.jenis_kelamin == "L") "Laki-laki" else "Perempuan"
            tvJenisKelamin.text = "Jenis Kelamin: $gender"

            tvAlamat.text = "Alamat: ${pasien.alamat}"
            tvNoTelepon.text = "Telepon: ${pasien.no_telepon}"
        }
    }
}