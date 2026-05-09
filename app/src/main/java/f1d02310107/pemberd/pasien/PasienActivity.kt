package f1d02310107.pemberd.pasien

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import f1d02310107.pemberd.pasien.adapter.PasienAdapter
import f1d02310107.pemberd.pasien.network.RetrofitClient
import kotlinx.coroutines.launch

class PasienActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tvUserName: TextView
    private lateinit var rvPasien: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var btnLogout: Button
    private lateinit var adapter: PasienAdapter

    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasien)

        // inisialisasi view
        toolbar = findViewById(R.id.toolbar)
        tvUserName = findViewById(R.id.tvUserName)
        rvPasien = findViewById(R.id.rvPasien)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnLogout = findViewById(R.id.btnLogout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Menampilkan tombol panah kembali
        supportActionBar?.setDisplayShowHomeEnabled(true)  // Menampilkan ikon home/back

        // ambil nama user dari Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "User"
        tvUserName.text = userName

        // ambil token dari SharedPreferences
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        userToken = prefs.getString("token", "") ?: ""

        if (userToken.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // setup RecyclerView
        adapter = PasienAdapter()
        rvPasien.layoutManager = LinearLayoutManager(this)
        rvPasien.adapter = adapter

        // load data pasien
        loadPasien()

        // tombol logout
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // panggil fungsi back
        return true
    }

    // kalo tombol Back fisik di HP diklik, tampilkan konfirmasi
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Kembali")
            .setMessage("Apakah Anda yakin ingin kembali ke halaman login?")
            .setPositiveButton("Ya") { _, _ ->
                // kembali ke login tanpa logout (token tetap ada)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadPasien() {
        lifecycleScope.launch {
            showLoading(true)
            tvEmpty.visibility = View.GONE

            try {
                val bearerToken = "Bearer $userToken"
                val response = RetrofitClient.apiService.getPasien(bearerToken)

                if (response.isSuccessful) {
                    val pasienResponse = response.body()
                    if (pasienResponse?.success == true) {
                        val pasienList = pasienResponse.data
                        if (pasienList.isNotEmpty()) {
                            adapter.setData(pasienList)
                        } else {
                            tvEmpty.visibility = View.VISIBLE
                            tvEmpty.text = "Belum ada data pasien"
                        }
                    } else {
                        showMessage(pasienResponse?.message ?: "Gagal mengambil data")
                        tvEmpty.visibility = View.VISIBLE
                        tvEmpty.text = "Gagal mengambil data"
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            showMessage("Token tidak valid, silakan login ulang")
                            logout()
                        }
                        else -> showMessage("Error: ${response.code()}")
                    }
                    tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
                tvEmpty.visibility = View.VISIBLE
                tvEmpty.text = "Gagal terhubung ke server"
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar? Anda harus login kembali untuk mengakses data.")
            .setPositiveButton("Logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // hapus token dari SharedPreferences
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit().clear().apply()

        // kembali ke login
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        rvPasien.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}