package su.katso.synonym.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import org.koin.standalone.KoinComponent
import su.katso.synonym.R

class TasksController : LifecycleController(), KoinComponent {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): android.view.View {
        return inflater.inflate(R.layout.task_controller, container, false)
            .apply { initView() }
    }

    private fun View.initView() {

        val bottomNavigation = findViewById<AHBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW

        bottomNavigation.addItem(
            AHBottomNavigationItem(
                R.string.app_navigation_tasks,
                R.drawable.abc_ic_menu_share_mtrl_alpha,
                R.color.primary
            )
        )
        bottomNavigation.addItem(
            AHBottomNavigationItem(
                R.string.app_navigation_settings,
                R.drawable.abc_ic_menu_share_mtrl_alpha,
                R.color.primary
            )
        )
        bottomNavigation.isColored = true
    }
}



