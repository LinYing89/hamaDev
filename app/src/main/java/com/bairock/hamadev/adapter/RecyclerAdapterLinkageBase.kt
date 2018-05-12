package com.bairock.hamadev.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import com.bairock.hamadev.R
import com.bairock.hamadev.app.HamaApp
import com.bairock.hamadev.database.LinkageDao
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.LinkageHolder

class RecyclerAdapterLinkageBase(var context: Context, private var linkageHolder: LinkageHolder) : RecyclerView.Adapter<RecyclerAdapterLinkageBase.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return if (linkageHolder.listLinkage == null) 0 else linkageHolder.listLinkage.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterLinkageBase.ViewHolder {
        return RecyclerAdapterLinkageBase.ViewHolder(mInflater.inflate(R.layout.adapter_linkage_holder, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerAdapterLinkageBase.ViewHolder, position: Int) {
        holder.setData(linkageHolder.listLinkage[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var linkage: Linkage? = null
        private val txtLinkageName: TextView = itemView.findViewById(R.id.txtLinkageName)
        private val switchEnable: Switch = itemView.findViewById(R.id.switchEnable)

        var onCheckedChangeListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            linkage!!.isEnable = isChecked
            val linkageDevValueDao = LinkageDao.get(HamaApp.HAMA_CONTEXT)
            linkageDevValueDao.update(linkage, null)
        }

        fun setData(linkage: Linkage) {
            this.linkage = linkage
            init()
        }

        private fun init() {
            txtLinkageName.text = linkage!!.name
            switchEnable.isChecked = linkage!!.isEnable
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener)
        }
    }
}