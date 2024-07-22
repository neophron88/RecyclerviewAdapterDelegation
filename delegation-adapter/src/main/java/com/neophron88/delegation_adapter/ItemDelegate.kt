package com.neophron88.delegation_adapter

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import kotlin.reflect.KClass


class ItemDelegate<I : Any>(
    val itemClass: KClass<I>,
    @LayoutRes val layout: Int,
    val diffUtil: DiffUtil.ItemCallback<I>,
    val itemViewHolder: (view: View) -> ItemViewHolder<I>
) {
    val viewType: Int get() = layout
}