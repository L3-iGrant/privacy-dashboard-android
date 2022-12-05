package io.igrant.igrant_sdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import io.igrant.igrant_org_sdk.activity.LoginActivity
import io.igrant.igrant_org_sdk.utils.IgrantSdk

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var myBtn: Button = findViewById<Button>(R.id.btnSdk) as Button

        myBtn.setOnClickListener {
            IgrantSdk().withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiI2MDNlNjdkYjY5ZGQ3MjAwMDFjNzRmOTAiLCJleHAiOjE2NDU4ODI0NjN9.9GxRitStk3eaggQL-lCAfY1z2cRMZlq3TPqwdTq0aSQ")
                .withUserId("605cb3508f373f00016b65f9")
                .withOrgId("603e683c69dd720001c74f93").start(this)
        }
    }
}
