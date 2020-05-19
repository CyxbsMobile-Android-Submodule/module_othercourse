package com.mredrock.cyxbs.discover.othercourse.pages.stusearch

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.othercourse.AutoWrapAdapter
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.pages.stulist.StuListActivity
import com.mredrock.cyxbs.discover.othercourse.room.History
import com.mredrock.cyxbs.discover.othercourse.snackbar
import kotlinx.android.synthetic.main.othercourse_other_course_search_fragment.*
import org.jetbrains.anko.startActivity


class OtherCourseSearchFragment(private val type: Int) : BaseViewModelFragment<OtherCourseSearchViewModel>() {
    private var lastSearch: String? = null
    private val funcList = listOf("同学课表", "老师课表")
    fun getTitle(): String {
        if (type < funcList.size && type >= 0) {
            return funcList[type]
        }
        return ""
    }

    fun getType(): Int = type
    override val viewModelClass: Class<OtherCourseSearchViewModel> = OtherCourseSearchViewModel.create(type)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.othercourse_other_course_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserve()
        setSearch()
        initHistory()
    }

    private fun initObserve() {

        viewModel.mList.observeNotNull {

            if (it.isNotEmpty()) {
                lastSearch?.let { text ->
                    viewModel.addHistory(History(text))

                }
                context?.startActivity<StuListActivity>("stu_list" to it)
            } else {
                snackbar("查无此人")
            }
        }
    }

    private fun setSearch() {
        et_discover_other_course_search.setOnEditorActionListener { v, actionId, event ->
            val text = et_discover_other_course_search.text.toString()
            if (text.isEmpty()) {
                snackbar("输入为空")
                return@setOnEditorActionListener false
            } else {
                viewModel.getPerson(text)
                lastSearch = text
                et_discover_other_course_search.setText("")
                return@setOnEditorActionListener true
            }
        }
    }

    private fun initHistory() {
        viewModel.getHistory()
        viewModel.mHistory.observe {
            it ?: return@observe
            aw_other_course_fragment.adapter = AutoWrapAdapter(it) { text ->
                viewModel.getPerson(text)
            }
        }
    }

}
