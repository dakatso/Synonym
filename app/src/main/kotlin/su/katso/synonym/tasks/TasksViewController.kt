package su.katso.synonym.tasks

import io.reactivex.Observable
import su.katso.synonym.common.adapter.BaseAdapter
import su.katso.synonym.common.adapter.BaseAdapter.ItemInfo
import su.katso.synonym.common.arch.ViewController

interface TasksViewController : ViewController<TasksViewState> {
    fun itemRecycleView(): Observable<ItemInfo>
}

