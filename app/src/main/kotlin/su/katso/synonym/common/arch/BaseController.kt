package su.katso.synonym.common.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import org.koin.standalone.KoinComponent

abstract class BaseController(args: Bundle) : LifecycleController(args), KoinComponent {
    abstract val content: Int
    abstract val presentationModel: BasePresentationModel<*, *>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(content, container, false)
            .apply { initView() }
    }

    abstract fun View.initView()
}
