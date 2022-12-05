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
            IgrantSdk().withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiI2MzhkZTMzMDJmNWQxNzAwMDE0NDMxZjMiLCJvcmdpZCI6IiIsImVudiI6IiIsImV4cCI6MTcwMTM0NzQ2N30.2q7ENyEIXPRpQ1aF70jcF4XiQJs7YqOHwIogWXt1x5g")
                .withUserId("638de3302f5d1700014431f3")
                .withOrgId("638dd3b12f5d1700014431ec").start(this)
        }
    }
}
