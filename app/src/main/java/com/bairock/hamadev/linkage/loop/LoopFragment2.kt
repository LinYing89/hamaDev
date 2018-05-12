package com.bairock.hamadev.linkage.loop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.linkage.LinkageBaseFragment
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.loop.ZLoop
import java.lang.ref.WeakReference

class LoopFragment2 : LinkageBaseFragment(){
    override fun initLinkageHolder(){
        linkageHolder = HamaApp.DEV_GROUP.loopHolder
    }

    override fun addNewLinkage(name : String): Linkage? {
        val subChain = ZLoop()
        subChain.name = name
        linkageHolder.addLinkage(subChain)
        return subChain
    }

    override fun toLinkageActivity() {
        this.activity!!.startActivity(Intent(this.activity, EditLoopActivity::class.java))
    }

    override fun initHandler() {
        handler = MyHandler(this)
    }

    override fun getDefaultHeadName() : String{
        return "循环"
    }

    class MyHandler internal constructor(activity: LoopFragment2) : Handler() {
        internal var mActivity: WeakReference<LoopFragment2> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val theActivity = mActivity.get()
            when (msg.what) {
                REFRESH_LIST -> theActivity!!.adapterChain.notifyDataSetChanged()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                LoopFragment2().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }
}