package su.katso.synonym.common.app

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import su.katso.synonym.R
import su.katso.synonym.auth.AuthView
import su.katso.synonym.common.utils.klog

class SynonymActivity : AppCompatActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

        klog(Log.DEBUG, intent?.action)
        klog(Log.DEBUG, intent?.data)

        val container = findViewById<ViewGroup>(R.id.controller_container)
        router = Conductor.attachRouter(this, container, savedInstanceState)

        if (!router.hasRootController()) {

            val bundle = intent?.data?.let { bundleOf(EXTRA_URI to it) } ?: Bundle.EMPTY

            router.setRoot(
                RouterTransaction.with(AuthView(bundle))
            )
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_URI = "extra_uri"
    }
}
