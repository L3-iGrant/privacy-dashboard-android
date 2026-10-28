package com.github.privacydashboardandroid

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.github.privacydashboard.PrivacyDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.github.privacydashboardandroid.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    Log.d("milna", data.getStringExtra("data_agreement_record") ?: "")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.container.btUserId.setOnClickListener {
            val apiKey = binding.container.etApiKey.text.toString()
            val baseUrl = binding.container.etBaseUrl.text.toString()
            if (apiKey.isEmpty() || baseUrl.isEmpty()) {
                binding.container.tvErrorMessage.text = "Please fill in all the fields"
            }
            else{
                binding.container.tvErrorMessage.text = ""
                GlobalScope.launch {
                    // Deprecated: a real app creates individuals in its backend.
                    @Suppress("DEPRECATION")
                    val userId = PrivacyDashboard.createAnIndividual(
                        apiKey = apiKey,
                        baseUrl = baseUrl
                    )
                    val id = extractIdFromResponse(userId)
                    val editableUserId = Editable.Factory.getInstance().newEditable(id)
                    withContext(Dispatchers.Main) {
                        binding.container.etUserId.text = editableUserId
                    }
                }
            }

        }

        binding.container.btPrivacyDashboard.setOnClickListener {
            val apiKey = binding.container.etApiKey.text.toString().trim()
            val baseUrl = binding.container.etBaseUrl.text.toString().trim()
            val organizationId = binding.container.etOrganizationId.text.toString().trim()
            val userId = binding.container.etUserId.text.toString().trim()

            if (apiKey.isEmpty() || baseUrl.isEmpty() || organizationId.isEmpty() || userId.isEmpty()) {
                binding.container.tvErrorMessage.text = "Please fill in all the fields"
            } else {
                binding.container.tvErrorMessage.text = ""
                PrivacyDashboard.showPrivacyDashboard()
                    .withApiKey(apiKey)
                    .withUserId(userId)
                    .withBaseUrl(baseUrl)
                    .withOrganisationId(organizationId)
                    .start(this)
            }
        }

        // Recommended: the app has no API key. The host backend gives a
        // short-lived individual token, and the SDK asks for it when necessary.
        binding.container.btPrivacyDashboardToken.setOnClickListener {
            val baseUrl = binding.container.etBaseUrl.text.toString().trim()
            val organizationId = binding.container.etOrganizationId.text.toString().trim()
            val tokenUrl = binding.container.etTokenUrl.text.toString().trim()

            if (baseUrl.isEmpty() || organizationId.isEmpty() || tokenUrl.isEmpty()) {
                binding.container.tvErrorMessage.text = "Please fill in BaseUrl, OrganizationId and the token URL"
            } else {
                binding.container.tvErrorMessage.text = ""
                val hostBackend = SampleHostBackend(tokenUrl)
                PrivacyDashboard.showPrivacyDashboard()
                    .withBaseUrl(baseUrl)
                    .withOrganisationId(organizationId)
                    .withTokenProvider { callback ->
                        hostBackend.privacyDashboardToken { token, error -> callback(token, error) }
                    }
                    .start(this)
            }
        }


    }

    private fun extractIdFromResponse(response: String?): String {
        return response?.let { json ->
            val regex = Regex("\"id\":\"([^\"]*)\"")
            regex.find(json)?.groupValues?.get(1) ?: ""
        } ?: ""
    }
}