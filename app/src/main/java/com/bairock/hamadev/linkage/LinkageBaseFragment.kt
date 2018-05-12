package com.bairock.hamadev.linkage

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast

import com.bairock.hamadev.R
import com.bairock.hamadev.adapter.RecyclerAdapterLinkageBase
import com.bairock.hamadev.app.MainActivity
import com.bairock.hamadev.database.LinkageDao
import com.bairock.hamadev.database.LinkageHolderDao
import com.bairock.iot.intelDev.linkage.Linkage
import com.bairock.iot.intelDev.linkage.LinkageHolder
import com.yanzhenjie.recyclerview.swipe.*
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration

/**
 * A simple [Fragment] subclass.
 *
 */
open class LinkageBaseFragment : Fragment() {

    private var param1: Int? = null
    companion object {
        const val ARG_PARAM1 = "param1"
        const val REFRESH_LIST = 1
        var LINKAGE: Linkage? = null
    }

    lateinit var switchEnable : Switch
    lateinit var btnAdd : Button
    lateinit var handler: Handler
    lateinit var linkageHolder: LinkageHolder
    lateinit var adapterChain: RecyclerAdapterLinkageBase

    lateinit var swipeMenuRecyclerViewChain : SwipeMenuRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(LinkageBaseFragment.ARG_PARAM1)
        }
        initHandler()
        initLinkageHolder()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chain, container, false)
        swipeMenuRecyclerViewChain = view.findViewById(R.id.swipeMenuRecyclerViewChain)

        swipeMenuRecyclerViewChain.layoutManager = LinearLayoutManager(this.context)
        swipeMenuRecyclerViewChain.addItemDecoration(DefaultItemDecoration(Color.LTGRAY))
        swipeMenuRecyclerViewChain.setSwipeMenuCreator(swipeMenuConditionCreator)

        switchEnable = view.findViewById(R.id.switchEnable)
        btnAdd = view.findViewById(R.id.btnAdd)
        initCbEnable()
        setListener()
        setListChain()
        return view
    }

    override fun onResume() {
        super.onResume()
        LINKAGE = null
    }

    open fun initLinkageHolder(){}

    open fun initHandler(){}

    open fun initCbEnable(){
        switchEnable.isChecked = linkageHolder.isEnable
    }

    private val swipeMenuConditionCreator = SwipeMenuCreator{ _, swipeRightMenu, _ ->
        val width = resources.getDimensionPixelSize(R.dimen.dp_70)
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        val renameItem = SwipeMenuItem(this.context)
                .setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.orange))
                .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(renameItem)
        val deleteItem = SwipeMenuItem(this.context)
                .setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height)
        swipeRightMenu.addMenuItem(deleteItem)
    }

    private fun setListener() {
        switchEnable.setOnCheckedChangeListener(onCheckedChangeListener)
        btnAdd.setOnClickListener(onClickListener)

        swipeMenuRecyclerViewChain.setSwipeItemClickListener(linkageSwipeItemClickListener)
        swipeMenuRecyclerViewChain.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener)
    }

    private fun setListChain() {
        adapterChain = RecyclerAdapterLinkageBase(this.context!!, linkageHolder)
        swipeMenuRecyclerViewChain.adapter = adapterChain
    }

    private val onCheckedChangeListener = { _: View, isChecked : Boolean ->
        linkageHolder.isEnable = isChecked
        LinkageHolderDao.get(this.context).update(linkageHolder)
    }

    private val onClickListener = {_: View ->
        showRenameDialog(null)
    }

    private fun showRenameDialog(oldName : String?) {
        val isRename = null != oldName
        val editNewName = EditText(this.context)
        val title : String
        title = if(isRename){
            editNewName.setText(oldName)
            this.context!!.getString(R.string.rename)
        }else {
            editNewName.setText(getDefaultName())
            "输入名称"
        }
        AlertDialog.Builder(this.context)
                .setTitle(title)
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure,
                        { _, _ ->
                            val value = editNewName.text.toString()
                            if (nameIsRepeat(value)) {
                                Toast.makeText(this.context, "名称重复", Toast.LENGTH_SHORT).show()
                            } else {
                                if(isRename){
                                    LINKAGE!!.name = value
                                    LinkageDao.get(this.context).add(LINKAGE, linkageHolder.id)
                                    adapterChain.notifyDataSetChanged()
                                }else {
                                    val linkage = addNewLinkage(value)
                                    if (null != linkage) {
                                        LinkageDao.get(this.context).add(linkage, linkageHolder.id)
                                        adapterChain.notifyDataSetChanged()
                                        LINKAGE = linkage
                                        toLinkageActivity()
                                    }
                                }
                            }
                        }).setNegativeButton(MainActivity.strCancel, null).create().show()
    }

    //条件列表点击事件
    private val linkageSwipeItemClickListener = SwipeItemClickListener { _, position ->
        LINKAGE = linkageHolder.listLinkage[position]
        toLinkageActivity()
    }

    private fun getDefaultName(): String {
        val name = getDefaultHeadName()
        var have: Boolean
        for (i in 1..999) {
            var newName = name
            have = false
            newName += i
            for (chain in linkageHolder.listLinkage) {
                if (chain.name == newName) {
                    have = true
                    break
                }
            }
            if (!have) {
                return newName
            }
        }
        return name
    }

    open fun getDefaultHeadName() : String{
        return "连锁"
    }

    private fun nameIsRepeat(name:String): Boolean{
        for(linkage in linkageHolder.listLinkage){
            if(linkage.name == name){
                return true
            }
        }
        return false
    }

    open fun addNewLinkage(name:String) : Linkage?{
        return null
    }

    open fun toLinkageActivity(){ }

    private val linkageSwipeMenuItemClickListener = SwipeMenuItemClickListener { menuBridge ->
        menuBridge.closeMenu()
        val adapterPosition = menuBridge.adapterPosition
        val menuPosition = menuBridge.position
        val linkage = linkageHolder.listLinkage[adapterPosition]

        LINKAGE = linkage
        when(menuPosition){
            0 ->{
                showRenameDialog(LINKAGE!!.name)
            }
            1 ->{
                linkageHolder.removeLinkage(linkage)
                linkage.isDeleted = true
                val linkageDevValueDao = LinkageDao.get(this.context)
                linkageDevValueDao.delete(linkage)
                adapterChain.notifyDataSetChanged()
            }
        }

    }
}
