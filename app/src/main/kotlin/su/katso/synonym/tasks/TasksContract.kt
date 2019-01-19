package su.katso.synonym.tasks

import io.reactivex.Observable
import su.katso.synonym.common.adapter.BaseAdapter
import su.katso.synonym.common.arch.MvcView

class TasksContract {
    interface View : MvcView<TasksModel> {
        fun recycleViewItemClicks(): Observable<BaseAdapter.ItemInfo>
        fun floatingButtonClicks(): Observable<Unit>
    }
}