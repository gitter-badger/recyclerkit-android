package io.inchtime.recyclerkit

import android.content.Context
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.*

typealias OnModelViewClick = (index: Int, viewModel: RecyclerAdapter.ViewModel) -> Unit
typealias OnModelViewLongClick = (index: Int, viewModel: RecyclerAdapter.ViewModel) -> Unit
typealias OnModelViewBind = (index: Int, viewModel: RecyclerAdapter.ViewModel, viewHolder: RecyclerAdapter.ViewHolder) -> Unit
typealias OnEmptyViewBind = (viewHolder: RecyclerAdapter.EmptyViewHolder) -> Unit

class RecyclerAdapter(private val context: Context, private val spanCount: Int = 1)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {

    companion object {
        const val VIEW_TYPE_EMPTY = Int.MAX_VALUE
    }

    data class ViewModel(
        val layout: Int,
        val spanSize: Int,
        val type: ModelType,
        val value: Any)

    /**
     * identify the position of item in the items
     */
    enum class ModelType(val value: Int) {

        LEADING(0x01),
        MIDDLE(0x02),
        TRAILING(0x04),
        LEADING_TRAILING(0x05);

        companion object {
            fun valueOf(index: Int, @NonNull list: List<*>): ModelType {
                if (index < 0) throw IndexOutOfBoundsException()
                if (list.size <= 1) return LEADING_TRAILING
                if (index == 0) return LEADING
                if (index == list.size - 1) return TRAILING
                return MIDDLE
            }
        }
    }

    private val models = ArrayList<ViewModel>()

    private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var recyclerView: RecyclerView

    var onModelViewClick: OnModelViewClick? = null

    var onModelViewLongClick: OnModelViewLongClick? = null

    var onModelViewBind: OnModelViewBind? = null

    var onEmptyViewBind: OnEmptyViewBind? = null

    var emptyViewVisibility: Boolean = true

    var emptyView: Int = RecyclerKit.defaultEmptyView

    /**
     * set the items of recycler adapter
     * @param items items to display
     */
    fun setItems(items: ArrayList<ViewModel>) {
        models.clear()
        models.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * add the items of recycler adapter
     * @param items items to add
     */
    fun addItems(items: ArrayList<ViewModel>) {
        models.addAll(items)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return if (models.isEmpty()) {
            VIEW_TYPE_EMPTY
        } else models[position].layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {

        return if (VIEW_TYPE_EMPTY == type) {
            val view = inflater.inflate(emptyView, parent, false)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            view.visibility = if (emptyViewVisibility) View.VISIBLE else View.INVISIBLE
            EmptyViewHolder(context, view)
        } else {
            // type is layout
            // see fun getItemViewType
            val view = inflater.inflate(type, parent, false)
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            ViewHolder(context, view)
        }
    }

    override fun getItemCount(): Int {
        return if (models.isNotEmpty()) models.size else 1
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is ViewHolder) {
            val model = models[position]
            onModelViewBind?.invoke(position, model, viewHolder)
        }

        if (viewHolder is EmptyViewHolder) {
            onEmptyViewBind?.invoke(viewHolder)
        }

    }

    override fun onClick(view: View) {
        val position = recyclerView.getChildAdapterPosition(view)

        if (!models.isEmpty() && position >= 0) {
            val model = models[position]
            onModelViewClick?.invoke(position, model)
        }
    }

    override fun onLongClick(view: View): Boolean {
        val position = recyclerView.getChildAdapterPosition(view)

        if (!models.isEmpty() && position >= 0) {
            val model = models[position]
            onModelViewLongClick?.invoke(position, model)
        }
        return true
    }

    fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // empty spanCount must equal to GridLayoutManager's spanCount
                return if (models.isEmpty()) spanCount else models[position].spanSize
            }
        }
    }

    class ViewHolder(val context: Context, val view: View) : RecyclerView.ViewHolder(view) {

        private val views: SparseArray<View> = SparseArray()

        fun <T: View> findView(key: Int): T {
            var v = views[key]
            if (v == null) {
                v = view.findViewById<T>(key)
                views.put(key, v)
            }
            @Suppress("UNCHECKED_CAST")
            return v as T
        }
    }

    class EmptyViewHolder(val context: Context, val view: View) : RecyclerView.ViewHolder(view)

}