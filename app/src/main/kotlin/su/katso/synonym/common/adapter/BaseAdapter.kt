package su.katso.synonym.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import io.reactivex.subjects.PublishSubject
import kotlin.reflect.KClass

open class BaseAdapter : Adapter<ViewHolder>() {
    val clickSubject = PublishSubject.create<ItemInfo>()

    private val selector = TypeSelector(clickSubject)
    private val items: MutableList<Any> = mutableListOf()

    fun setItems(items: List<Any>) {
        this.items.clear()
        this.items.addAll(items)
    }

    protected fun addTypes(vararg types: Pair<KClass<*>, (ViewGroup) -> WrapperViewHolder>) =
        types.forEach { selector.addType(it.first, it.second) }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = selector.getItemViewType(items[position])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = selector.onCreateViewHolder(parent, viewType)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = selector.onBindViewHolder(holder, items[position])
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) = clickSubject.onComplete()

    class ItemInfo(val position: Int, val view: View, val item: Any?)
}