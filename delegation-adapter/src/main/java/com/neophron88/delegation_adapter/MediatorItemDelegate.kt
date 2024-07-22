package com.neophron88.delegation_adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import kotlin.reflect.KClass

class MediatorItemDelegate<I : Any> private constructor(
    delegates: List<ItemDelegate<I>>
) {

    private val delegateByViewType = SparseArray<ItemDelegate<I>>()
    private val viewTypeByClass = HashMap<KClass<I>, Int>()
    private val delegateByClass = HashMap<KClass<I>, ItemDelegate<I>>()


    init {
        decomposeDelegates(delegates)
    }

    private fun decomposeDelegates(delegates: List<ItemDelegate<I>>) =
        delegates.forEach { itemDelegate ->
            delegateByViewType.put(itemDelegate.layout, itemDelegate)
            val itemClass = itemDelegate.itemClass
            viewTypeByClass[itemClass] = itemDelegate.layout
            delegateByClass[itemClass] = itemDelegate

        }

    fun createViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<I> {
        val inflater = LayoutInflater.from(parent.context)
        val itemDelegate = delegateByViewType[viewType]
        val view = inflater.inflate(itemDelegate.layout, parent, false)
        return itemDelegate.itemViewHolder(view)

    }

    fun getItemViewType(item: I): Int {
        val kClass = item::class
        return viewTypeByClass[kClass]
            ?: throw err(kClass)
    }

    val diffUtil = object : DiffUtil.ItemCallback<I>() {

        override fun areItemsTheSame(oldItem: I, newItem: I): Boolean {
            val oldClass = oldItem::class
            if (oldClass != newItem::class) return false
            val diffUtil = delegateByClass[oldClass]?.diffUtil ?: throw err(oldClass)
            return diffUtil.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: I, newItem: I): Boolean {
            val oldClass = oldItem::class
            if (oldClass != newItem::class) return false
            val diffUtil = delegateByClass[oldClass]?.diffUtil ?: throw err(oldClass)
            return diffUtil.areContentsTheSame(oldItem, newItem)
        }

        override fun getChangePayload(oldItem: I, newItem: I): Any? {
            val oldClass = oldItem::class
            if (oldClass != newItem::class) return null
            val diffUtil = delegateByClass[oldClass]?.diffUtil ?: throw err(oldClass)
            return diffUtil.getChangePayload(oldItem, newItem)
        }
    }

    fun err(klass: KClass<out I>) = IllegalStateException(
        "Can not find ItemDelegate for ${klass.simpleName} model"
    )

    class Builder {
        private val delegates = mutableListOf<ItemDelegate<Any>>()

        fun <I : Any> addItemDelegate(item: ItemDelegate<I>): Builder {
            delegates.add(item as ItemDelegate<Any>)
            return this
        }

        fun build() = MediatorItemDelegate(delegates)
    }

}