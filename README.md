# RecyclerviewAdapterDelegation
## The adapter makes it a little easier to create RV Adapter with multiple viewtypes 
## Basic usage

### 1.Creating pojos
```kotlin

data class SomeClass(
    val id: Long,
    val text: String
)

data class SomeAnotherClass(
    val id: Long,
    val anotherText: String
)

```
### 2.Creating layouts ...

### 3.Creating Adapter
```kotlin
class SomeFragment : Fragment(R.layout.list_fragment) {

    private val binding: ListFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvAdapter = Adapter()
        binding.apply {
            recyclerView.adapter = rvAdapter
        }
    }

    private fun Adapter() = ItemsAdapter {

        item<SomeClass, TextItemBinding> {
            layout { R.layout.text_item }
            diffUtil {
                areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
                areContentsTheSame { oldItem, newItem -> oldItem == newItem }
            }
            viewHolder {
                viewBinding(TextItemBinding::bind)
                viewBindingCreated {
                    // setup item view (set click listener etc)
                }
            }
            onBind {
                binding.textView.text = item.text
            }
        }

        // another view type
        item<SomeAnotherClass, SomeAnotherTextItemBinding> {
            layout { R.layout.some_another_text_item }
            diffUtil {
                areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
                areContentsTheSame { oldItem, newItem -> oldItem == newItem }
            }
            viewHolder {
                viewBinding(SomeAnotherTextItemBinding::bind)
                viewBindingCreated {
                    // setup item view (set click listener etc)
                }
            }
            onBind {
                binding.textView.text = item.text
            }
        }
    }

}

```

## Integration with other libraries

```kotlin
class PagingAdapter(
    private val mediator: MediatorItemDelegate<Any>
) : PagingDataAdapter<Any, ItemViewHolder<Any>>(mediator.diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<Any> =
        mediator.createViewHolder(parent, viewType)


    override fun onBindViewHolder(holder: ItemViewHolder<Any>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int =
        mediator.getItemViewType(getItem(position).require())

}


```

Just replace `ItemsAdapter` with `MediatorItemsDelegate`.

```kotlin
class SomeFragment : Fragment(R.layout.list_fragment) {

    private val binding: ListFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvAdapter = PagingAdapter(MediatorItemsDelegate())
        binding.apply {
            recyclerView.adapter = rvAdapter
        }
    }

    private fun MediatorItemsDelegate() = MediatorItemsDelegate {
        
        item<SomeClass, TextItemBinding> {
            layout { R.layout.text_item }
            diffUtil {
                areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
                areContentsTheSame { oldItem, newItem -> oldItem == newItem }
            }
            viewHolder {
                viewBinding(TextItemBinding::bind)
                viewBindingCreated {
                    // setup item view (set click listener etc)
                }
            }
            onBind {
                binding.textView.text = item.text
            }
        }
    }

}
```

