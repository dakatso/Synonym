package su.katso.synonym.common.app

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import su.katso.synonym.R
import su.katso.synonym.auth.AuthView
import su.katso.synonym.tasks.TasksView

class SynonymActivity : AppCompatActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity)

        val container = findViewById<ViewGroup>(R.id.controller_container)
        router = Conductor.attachRouter(this, container, savedInstanceState)

        if (!router.hasRootController()) {
            val bundle = intent?.data?.let { bundleOf(EXTRA_URI to it.toString()) } ?: Bundle.EMPTY
            setRootView(AuthView(bundle))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        router.popToRoot()
        val view = router.backstack[router.backstack.lastIndex].controller()
        val bundle = intent?.data?.let { bundleOf(EXTRA_URI to it.toString()) } ?: Bundle.EMPTY
        setRootView(if (view is AuthView) AuthView(bundle) else TasksView(bundle))
    }

    private fun setRootView(view: Controller) {
        router.setRoot(RouterTransaction.with(view))
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
