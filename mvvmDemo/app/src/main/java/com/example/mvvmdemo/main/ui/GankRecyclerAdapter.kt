
package com.example.mvvmdemo.main.ui

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmdemo.R
import com.example.mvvmdemo.databinding.GankFuliItemBinding
import com.example.mvvmdemo.databinding.GankFuliItemBindingImpl
import com.example.mvvmdemo.databinding.GankItemBinding
import com.example.mvvmdemo.databinding.GankItemBindingImpl
import com.example.mvvmdemo.main.pojo.Gank


class GankRecyclerAdapter(val clickCallback: ItemClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // 这里也可以用LiveData, 不过感觉没啥必要
    var todayGanks = emptyList<Gank>() // 空
        set(value) {
            if(todayGanks.isEmpty()) {
                field = value
                notifyItemRangeInserted(0, field.size)
            } else {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return field.size
                    }

                    override fun getNewListSize(): Int {
                        return value.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return TextUtils.equals(field[oldItemPosition].gankId ,value[newItemPosition].gankId)
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return TextUtils.equals(field[oldItemPosition].gankId ,value[newItemPosition].gankId)
                                && field[oldItemPosition].viewed == value[newItemPosition].viewed
                    }
                })

                field = value
                result.dispatchUpdatesTo(this);
            }
        }

    override fun getItemCount(): Int {
        return todayGanks.size
    }

    private fun getItem(position: Int) : Gank {
        return todayGanks[position]
    }
//
//    override fun getItemId(position: Int): Long {
//        return getItem(position).gankIdLong
//    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).typeId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            Gank.GROUP_FULI, Gank.GROUP_ANDROID, Gank.GROUP_FRONT_END, Gank.GROUP_VIDEO -> {
                GroupVH(View.inflate(parent.context, R.layout.group_item, null) as TextView)
            }
            Gank.FULI -> {
                FuliVH(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.gank_fuli_item,
                        parent,
                        false
                    )
                )
            }
            Gank.ANDROID, Gank.FRONT_END, Gank.VIDEO -> {
                DefaultVH(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.gank_item,
                        parent,
                        false
                    )
                )
            }
            else -> { TODO() } // impossible
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            Gank.GROUP_FULI -> (holder as GroupVH).titleView.text = "福利"
            Gank.GROUP_ANDROID -> (holder as GroupVH).titleView.text = "Android"
            Gank.GROUP_FRONT_END -> (holder as GroupVH).titleView.text = "前端"
            Gank.GROUP_VIDEO -> (holder as GroupVH).titleView.text = "休息视频"
            Gank.FULI -> {
                (holder as FuliVH).binding.run {
                    gank = getItem(position)
                    executePendingBindings()
                }
            }
            Gank.ANDROID, Gank.FRONT_END, Gank.VIDEO -> {
                (holder as DefaultVH).binding.run {
                    gank = getItem(position)
                    onClick = clickCallback
                    executePendingBindings()
                }
            }
        }
    }

    // 分组
    class GroupVH(val titleView: TextView) : RecyclerView.ViewHolder(titleView)

    // 福利
    class FuliVH(val binding : GankFuliItemBindingImpl) : RecyclerView.ViewHolder(binding.root)

    // 其他
    class DefaultVH(val binding : GankItemBindingImpl) : RecyclerView.ViewHolder(binding.root)

}