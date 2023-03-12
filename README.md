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

### 3.Creating ViewHolders, need to extend ItemViewHolder class
```kotlin
class SomeTextItemViewHolder(
    view: View,
) : ItemViewHolder<SomeClass>(view) {

    private val binding = TextItemBinding.bind(view)

    override fun onBind(item: SomeClass) {
        binding.textView.text = item.text
    }
}


class SomeAnotherTextItemViewHolder(
    view: View,
    onItemClick: (itemId: Long) -> Unit
) : ItemViewHolder<SomeAnotherClass>(view) {

    private val binding = TextItemBinding.bind(view)

    init {
        binding.root.setOnClickListener { onItemClick(item.id) }
    }

    override fun onBind(item: SomeAnotherClass) {
        binding.textView.text = item.anotherText
    }

}
```
### 3.Creating Adapter, need to create ItemsAdapter class and provide ItemDelegates
```kotlin
class SomeFragment : Fragment(R.layout.list_fragment) {

    private val binding: ListFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemsAdapter = setupAdapter()
        binding.recyclerView.adapter = itemsAdapter
    }

    private fun setupAdapter() = ItemsAdapter(
        ItemDelegate(
            layout = R.layout.text_item,
            diffUtil = ItemDiffUtil(itemsTheSameValue = SomeClass::id),
            VHProducer = { view -> SomeTextItemViewHolder(view) }
        ),
        ItemDelegate(
            layout = R.layout.text_item,
            diffUtil = ItemDiffUtil(itemsTheSameValue = SomeAnotherClass::id),
            VHProducer = { view ->
                SomeAnotherTextItemViewHolder(view = view, onItemClick = { /* Some Action */ })
            }
        )
    )
    
}

```

