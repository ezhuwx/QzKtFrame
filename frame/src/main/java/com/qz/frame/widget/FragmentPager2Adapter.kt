package com.qz.frame.widget

import android.R.attr.fragment
import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author : ezhuwx
 * Describe :FragmentAdapter
 * Designed on 2021/11/17
 * E-mail : ezhuwx@163.com
 * Update on 15:41 by ezhuwx
 */
open class FragmentPager2Adapter(manager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        manager, lifecycle
    ) {
    private var fragments = mutableSetOf<Fragment>()
    var tabs = mutableSetOf<String>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments.elementAt(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFragments(fragments: MutableSet<Fragment>, tabs: MutableSet<String>) {
        this.fragments = fragments
        this.tabs = tabs
        notifyDataSetChanged()
    }

    fun addFragment(fragment: Fragment, tab: String) {
        fragments.add(fragment)
        tabs.add(tab)
        notifyItemChanged(fragments.size)
    }

    fun removeFragment(fragment: Fragment) {
        remove(fragments.indexOf(fragment))
    }

    fun remove(index: Int) {
        if (index >= 0) {
            fragments.remove(fragments.elementAt(index))
            if (index < tabs.size) tabs.remove(tabs.elementAt(index))
            notifyItemRemoved(index)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        fragments.clear()
        tabs.clear()
        notifyDataSetChanged()
    }

    fun getFragmentSet() = fragments
    fun getFragment(position: Int) = fragments.elementAtOrNull(position)
}