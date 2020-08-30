
package com.example.mvvmdemo.main.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmdemo.R
import com.example.mvvmdemo.databinding.MainFragmentBinding
import com.example.mvvmdemo.main.pojo.Gank
import com.example.mvvmdemo.main.pojo.TodayGanks
import com.example.mvvmdemo.main.vm.MainFragmentViewModel



class MainFragment :Fragment(){
    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel:MainFragmentViewModel
    private lateinit var adapter : GankRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         viewModel=ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.main_fragment, container, false)
        binding.lifecycleOwner = this // 必须设置这个, 不然viewModel中的livedata改变不会收到通知
        adapter = GankRecyclerAdapter(object : ItemClickCallback {
            override fun onClick(gank: Gank) {
                onGankClick(gank)
            }
        })
        binding.viewModel = viewModel
        binding.gankList.adapter = adapter
        viewModel.error.observe(this, Observer<Throwable> { onError(it) })
        viewModel.todayGanks.observe(this, Observer<TodayGanks> { adapter.todayGanks = it.toList() })
        return binding.root
    }
    fun onGankClick(gank: Gank) {
        if(gank.url.isNotEmpty()) {
//            val fragment = WebViewFragment().apply {
//                arguments = Bundle()
//                    .apply {
//                        //                        putString(WebViewFragment.URL, gank.url)
//                        // 不要吐槽传数据的方式...只是为了演示用
//                        putString(WebViewFragment.GANK_ID, gank.gankId)
//                    }
//            }
//            (activity as MainActivity).goToFragment(fragment)
        }
    }

    fun onError(t: Throwable) {
        Log.e("MVVM", "load failed", t)
        Toast.makeText(activity, "Load Error", Toast.LENGTH_LONG).show()
    }
}