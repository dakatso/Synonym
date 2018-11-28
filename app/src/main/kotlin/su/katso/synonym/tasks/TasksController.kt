package su.katso.synonym.tasks

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import su.katso.synonym.R
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.PresentationModel.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.common.utils.klog
import su.katso.synonym.settings.SettingsController
import su.katso.synonym.tasks.adapter.TasksAdapter

class TasksController(args: Bundle = Bundle.EMPTY) : BaseController(args), TasksViewController {
    override val content: Int = R.layout.tasks_controller
    override val presentationModel = TasksPresentationModel()
        .also { it.bindToLifecycle(this) }

    private lateinit var adapter: TasksAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTasks: RecyclerView

    override fun View.initView() {

        with(findViewById<Toolbar>(R.id.toolbar)) {
            inflateMenu(R.menu.menu_tasks)
            setOnMenuItemClickListener(::onOptionsItemSelected)
        }

        rvTasks = findViewById(R.id.rvTasks)

        adapter = TasksAdapter()
        rvTasks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvTasks.adapter = adapter

        progressBar = findViewById(R.id.progressBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {

            router.pushController(
                RouterTransaction.with(SettingsController())
            )

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun itemRecycleView() = adapter.clickSubject

    override fun render(viewState: TasksViewState) {

        klog(Log.DEBUG, viewState)

        adapter.setItems(viewState.tasks)
        adapter.notifyDataSetChanged()

        progressBar.isVisible = viewState.isLoading
        rvTasks.isVisible = !viewState.isLoading
    }

    override fun react(command: Command) {
        when (command) {
            is ToastCommand -> Toast.makeText(applicationContext, command.text, Toast.LENGTH_LONG).show()
        }
    }
}



