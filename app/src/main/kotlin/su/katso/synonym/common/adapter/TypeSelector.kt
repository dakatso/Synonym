package su.katso.synonym.common.adapter

import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject
import su.katso.synonym.common.adapter.BaseAdapter.ItemInfo
import kotlin.reflect.KClass

class TypeSelector(private val clickSubject: PublishSubject<ItemInfo>? = null) {

    private val types = ArrayMap<KClass<*>, ItemType>()

    fun addType(itemClass: KClass<*>, factory: (ViewGroup) -> WrapperViewHolder): TypeSelector {
        return this.also { types[itemClass] = ItemType(types.size, factory) }
    }

    fun getItemViewType(itemClass: Any): Int {
        return types[itemClass::class]?.viewType
            ?: throw NullPointerException("No type added ${itemClass::class.java}")
    }

    private fun get(viewType: Int): ItemType {
        return types.values.firstOrNull { it.viewType == viewType }
            ?: throw NullPointerException("No type added $viewType")
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return get(viewType).onCreateViewHolder(parent).also { it.clickSubject = clickSubject }.holder
    }

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        (holder as WrapperViewHolder.ViewHolder).onBindViewHolder(item)
    }

    class ItemType(val viewType: Int, private val factory: (ViewGroup) -> WrapperViewHolder) {
        fun onCreateViewHolder(parent: ViewGroup) = factory(parent)
    }
}

