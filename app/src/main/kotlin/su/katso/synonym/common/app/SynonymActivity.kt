package su.katso.synonym.common.app

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import su.katso.synonym.R
import su.katso.synonym.auth.AuthController

class SynonymActivity : AppCompatActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

        val container = findViewById<ViewGroup>(R.id.controller_container)
        router = Conductor.attachRouter(this, container, savedInstanceState)

        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(AuthController()))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}
