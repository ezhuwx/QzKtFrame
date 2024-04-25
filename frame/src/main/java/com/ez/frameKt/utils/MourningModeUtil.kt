package com.ez.frameKt.utils

import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import java.lang.reflect.Field
import java.util.ArrayList

/**
 *灰调模式
 */
@SuppressLint("DiscouragedPrivateApi")
fun mourningMode(enable: Boolean) {
    if (enable) {
        try {
            //灰色调Paint
            val mPaint = Paint()
            val mColorMatrix = ColorMatrix()
            mColorMatrix.setSaturation(0f)
            mPaint.colorFilter = ColorMatrixColorFilter(mColorMatrix)
            //反射获取windowManagerGlobal
            @SuppressLint("PrivateApi") val windowManagerGlobal =
                Class.forName("android.view.WindowManagerGlobal")
            @SuppressLint("DiscouragedPrivateApi") val getInstanceMethod =
                windowManagerGlobal.getDeclaredMethod("getInstance")
            getInstanceMethod.isAccessible = true
            val windowManagerGlobalInstance = getInstanceMethod.invoke(windowManagerGlobal)
            //反射获取mViews
            val mViewsField: Field = windowManagerGlobal.getDeclaredField("mViews")
            mViewsField.isAccessible = true
            val mViewsObject = mViewsField.get(windowManagerGlobalInstance) as Collection<View>
            //创建具有数据感知能力的ObservableArrayList
            val observerArrayList = ListenArrayList<View>()
            observerArrayList.addOnListChangedListener(object :
                ListenArrayList.OnListChangeListener {
                override fun onChange(list: ArrayList<*>?, index: Int, count: Int) {
                }

                override fun onAdd(list: ArrayList<*>?, start: Int, count: Int) {
                    // 拿到DecorView触发重绘
                    val view = list?.get(start) as View?
                    view?.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint)
                }

                override fun onRemove(list: ArrayList<*>?, start: Int, count: Int) {
                }

            })
            //将原有的数据添加到新创建的list
            observerArrayList.addAll(mViewsObject)
            //替换掉原有的mViews
            mViewsField.set(windowManagerGlobalInstance, observerArrayList)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

class ListenArrayList<T> : ArrayList<T>() {
    private val mListeners: MutableList<OnListChangeListener>? = ArrayList()
    fun addOnListChangedListener(listener: OnListChangeListener) {
        mListeners?.add(listener)
    }

    fun removeOnListChangedListener(listener: OnListChangeListener) {
        mListeners?.remove(listener)
    }

    override fun add(element: T): Boolean {
        super.add(element)
        notifyAdd(size - 1, 1)
        return true
    }

    override fun add(index: Int, element: T) {
        super.add(index, element)
        notifyAdd(index, 1)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val oldSize = size
        val added = super.addAll(elements)
        if (added) {
            notifyAdd(oldSize, size - oldSize)
        }
        return added
    }

    override fun clear() {
        val oldSize = size
        super.clear()
        if (oldSize != 0) {
            notifyRemove(0, oldSize)
        }
    }

    override fun removeAt(index: Int): T {
        val result = super.removeAt(index)
        notifyRemove(index, 1)
        return result
    }

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index >= 0) {
            removeAt(index)
            true
        } else {
            false
        }
    }

    override fun set(index: Int, element: T): T {
        val result = super.set(index, element)
        if (mListeners != null) {
            for (listener in mListeners) {
                listener.onChange(this, index, 1)
            }
        }
        return result
    }

    override fun removeRange(fromIndex: Int, toIndex: Int) {
        super.removeRange(fromIndex, toIndex)
        notifyRemove(fromIndex, toIndex - fromIndex)
    }

    private fun notifyAdd(start: Int, count: Int) {
        if (mListeners != null) {
            for (listener in mListeners) {
                listener.onAdd(this, start, count)
            }
        }
    }

    private fun notifyRemove(start: Int, count: Int) {
        if (mListeners != null) {
            for (listener in mListeners) {
                listener.onRemove(this, start, count)
            }
        }
    }

    interface OnListChangeListener {
        fun onChange(list: ArrayList<*>?, index: Int, count: Int)
        fun onAdd(list: ArrayList<*>?, start: Int, count: Int)
        fun onRemove(list: ArrayList<*>?, start: Int, count: Int)
    }
}