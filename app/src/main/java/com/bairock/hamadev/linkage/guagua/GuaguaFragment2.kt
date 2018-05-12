package com.bairock.hamadev.linkage.guagua

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.linkage.LinkageBaseFragment
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.SubChain
import java.lang.ref.WeakReference

class GuaguaFragment2 : LinkageBaseFragment(){

    override fun initLinkageHolder(){
        linkageHolder = HamaApp.DEV_GROUP.guaguaHolder
    }

    override fun addNewLinkage(name : String): Linkage? {
        val subChain = SubChain()
        subChain.name = name
        linkageHolder.addLinkage(subChain)
        return subChain
    }

    override fun toLinkageActivity() {
        this.activity!!.startActivity(Intent(this.activity, EditGuaguaActivity::class.java))
    }

    override fun initHandler() {
        handler = MyHandler(this)
    }

    override fun getDefaultHeadName() : String{
        return "呱呱"
    }

    class MyHandler internal constructor(activity: GuaguaFragment2) : Handler() {
        internal var mActivity: WeakReference<GuaguaFragment2> = WeakReference(activity)

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
                GuaguaFragment2().apply {
                    arguments = Bundle().apply {
                        putInt(LinkageBaseFragment.ARG_PARAM1, param1)
                    }
                }
    }
}