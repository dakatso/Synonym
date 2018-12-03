package su.katso.synonym.tasks

import io.reactivex.Observable
import su.katso.synonym.common.adapter.BaseAdapter.ItemInfo
import su.katso.synonym.common.arch.ViewController

interface TasksViewController : ViewController<TasksViewState> {
    fun recycleViewItemClicks(): Observable<ItemInfo>
    fun floatingButtonClicks(): Observable<Unit>
}

