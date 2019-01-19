package su.katso.synonym.tasks

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jakewharton.rxbinding3.view.clicks
import su.katso.synonym.R
import su.katso.synonym.common.arch.BaseView
import su.katso.synonym.common.arch.Command
import su.katso.synonym.common.arch.ToastCommand
import su.katso.synonym.settings.SettingsView
import su.katso.synonym.tasks.adapter.TasksAdapter

class TasksView(args: Bundle = Bundle.EMPTY) : BaseView(args), TasksContract.View {
    override val content: Int = R.layout.tasks_view
    override val presentationModel = TasksController()
        .also { it.bindToLifecycle(this) }

    private lateinit var adapter: TasksAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTasks: RecyclerView
    private lateinit var fabCreate: FloatingActionButton

    override fun View.initView() {

        with(findViewById<Toolbar>(R.id.toolbar)) {
            inflateMenu(R.menu.menu_tasks)
            setOnMenuItemClickListener(::onOptionsItemSelected)
        }

        rvTasks = findViewById(R.id.rvTasks)

        adapter = TasksAdapter()
        rvTasks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvTasks.adapter = adapter

        fabCreate = findViewById(R.id.fabCreate)
        progressBar = findViewById(R.id.progressBar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {

            router.pushController(
                RouterTransaction.with(SettingsView())
            )

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun recycleViewItemClicks() = adapter.clickSubject
    override fun floatingButtonClicks() = fabCreate.clicks()

    override fun render(model: TasksModel) {
        adapter.setItems(model.tasks)
        adapter.notifyDataSetChanged()

        progressBar.isVisible = model.isLoading
        rvTasks.isVisible = !model.isLoading
    }

    override fun react(command: Command) {
        when (command) {
            is ToastCommand -> Toast.makeText(applicationContext, command.text, Toast.LENGTH_LONG).show()
        }
    }
}



