package f1d02310107.pemberd.pasien

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import f1d02310107.pemberd.pasien.model.LoginRequest
import f1d02310107.pemberd.pasien.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // inisialisasi view
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        // cek apakah sudah login atau belum (token ada)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (token != null) {
            // jika sudah login, langsung ke halaman pasien
            val name = prefs.getString("user_name", "")
            goToPasienActivity(name ?: "")
            return
        }

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // validasi
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }

        lifecycleScope.launch {
            showLoading(true)

            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        val token = loginResponse.data?.token ?: ""
                        val userName = loginResponse.data?.user?.name ?: ""

                        if (token.isNotEmpty()) {
                            // simpan token dan nama user ke SharedPreferences
                            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                            prefs.edit().putString("token", token).apply()
                            prefs.edit().putString("user_name", userName).apply()

                            showMessage("Login berhasil! Selamat datang, $userName")
                            goToPasienActivity(userName)
                        } else {
                            showMessage("Token tidak ditemukan")
                        }
                    } else {
                        showMessage(loginResponse?.message ?: "Login gagal")
                    }
                } else {
                    showMessage("Email atau password salah")
                }
            } catch (e: Exception) {
                showMessage("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun goToPasienActivity(userName: String) {
        val intent = Intent(this, PasienActivity::class.java)
        intent.putExtra("USER_NAME", userName)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}