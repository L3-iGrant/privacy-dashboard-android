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
            IgrantSdk().withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiI1ZjVhOTRkYmM2NzAwMTAwMDEwMGZhMmYiLCJleHAiOjE2MzA5MDg4NTN9.7yzf-iSB2ju7Rs-tJQPwcv7N2IK9Yp-qXsVfjbYpqQ4")
                .withUserId("605cb3508f373f00016b65f9").start(this)
        }
    }
}
