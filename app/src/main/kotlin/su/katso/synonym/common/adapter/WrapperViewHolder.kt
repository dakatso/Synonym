package su.katso.synonym.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject
import su.katso.synonym.common.adapter.BaseAdapter.ItemInfo

abstract class WrapperViewHolder(parent: ViewGroup) {
    protected val context: Context = parent.context
    internal val itemView: View get() = holder.itemView
    protected fun <V : View> findViewById(id: Int): V = itemView.findViewById(id)
    protected fun inflate(content: Int, parent: ViewGroup): View = LayoutInflater.from(context)
        .inflate(content, parent, false)

    var clickSubject: PublishSubject<ItemInfo>? = null
    val holder = object : ViewHolder(this.onCreate(parent)) {
        override fun onBindViewHolder(any: Any) = onBind(any)
    }

    abstract fun onCreate(parent: ViewGroup): View
    abstract fun onBind(any: Any)

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBindViewHolder(any: Any)
    }

    fun View.setOnClickListener(item: Any?) {
        setOnClickListener { clickSubject?.onNext(ItemInfo(holder.adapterPosition, this, item)) }
    }
}
