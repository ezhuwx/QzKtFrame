package com.qz.frame.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.qz.frame.widget.FragmentPager2Adapter
import com.google.android.material.tabs.TabLayout
import java.lang.ref.WeakReference

/**
 * @author : ezhuwx
 * Describe :TabLayout 兼容 ViewPager2
 * Designed on 2024/3/12
 * E-mail : ezhuwx@163.com
 * Update on 16:12 by ezhuwx
 */

fun ViewPager2.setupWithViewPager2(tabLayout: TabLayout) {
    tabLayout.setPagerAdapter(this)
    // Add our custom OnPageChangeListener to the ViewPager
    val pageChangeListener = TabLayoutOnOnPageChangeCallback(tabLayout)
    pageChangeListener.reset()
    registerOnPageChangeCallback(pageChangeListener)
    // Now we'll add a tab selected listener to set ViewPager's current item
    val currentVpSelectedListener = ViewPagerOnTabSelectedListener(this)
    tabLayout.addOnTabSelectedListener(currentVpSelectedListener)
}

fun TabLayout.setPagerAdapter(viewPager: ViewPager2?) {
    val stateAdapter = viewPager?.adapter
    stateAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            if (viewPager.isAttachedToWindow) populateFromPagerAdapter(stateAdapter)
        }
    })
    populateFromPagerAdapter(stateAdapter)
}

fun <VH : RecyclerView.ViewHolder> TabLayout.populateFromPagerAdapter(stateAdapter: Adapter<VH>?) {
    removeAllTabs()
    if (stateAdapter != null && stateAdapter is FragmentPager2Adapter) {
        val adapterCount: Int = stateAdapter.itemCount
        for (i in 0 until adapterCount) {
            addTab(newTab().setText(stateAdapter.tabs.elementAt(i)), false)
        }
        if (adapterCount > 0) selectTab(getTabAt(0))
    }
}

class TabLayoutOnOnPageChangeCallback(tabLayout: TabLayout) : OnPageChangeCallback() {
    private val tabLayoutRef: WeakReference<TabLayout>
    private var previousScrollState = 0
    private var scrollState = 0

    init {
        tabLayoutRef = WeakReference(tabLayout)
    }

    override fun onPageScrollStateChanged(state: Int) {
        previousScrollState = scrollState
        scrollState = state
    }

    override fun onPageScrolled(
        position: Int, positionOffset: Float, positionOffsetPixels: Int
    ) {
        val tabLayout = tabLayoutRef.get()
        if (tabLayout != null) {
            // Only update the text selection if we're not settling, or we are settling after
            // being dragged
            val updateText =
                scrollState != ViewPager.SCROLL_STATE_SETTLING || previousScrollState == ViewPager.SCROLL_STATE_DRAGGING
            // Update the indicator if we're not settling after being idle. This is caused
            // from a setCurrentItem() call and will be handled by an animation from
            // onPageSelected() instead.
            val updateIndicator =
                !(scrollState == ViewPager.SCROLL_STATE_SETTLING && previousScrollState == ViewPager.SCROLL_STATE_IDLE)
            tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
        }
    }

    override fun onPageSelected(position: Int) {
        val tabLayout = tabLayoutRef.get()
        if (tabLayout != null && tabLayout.selectedTabPosition != position && position < tabLayout.tabCount) {
            // Select the tab, only updating the indicator if we're not being dragged/settled
            // (since onPageScrolled will handle that).
            val updateIndicator = (scrollState == ViewPager.SCROLL_STATE_IDLE
                    || (scrollState == ViewPager.SCROLL_STATE_SETTLING
                    && previousScrollState == ViewPager.SCROLL_STATE_IDLE))
            tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
        }
    }

    fun reset() {
        scrollState = ViewPager.SCROLL_STATE_IDLE
        previousScrollState = scrollState
    }
}


/**
 * A [TabLayout.OnTabSelectedListener] class which contains the necessary calls back to the
 * provided [ViewPager2] so that the tab position is kept in sync.
 */
class ViewPagerOnTabSelectedListener(private val viewPager: ViewPager2) :
    TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
}
