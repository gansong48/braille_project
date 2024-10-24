package org.techtown.dotanddoc

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.main_toolbar.*
import java.util.*

class BookmarkBraille : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var bookmarkBrailleAdapter: BookmarkBrailleListAdapter? = null
    private var bookmarkBrailleList: ArrayList<BookmarkBrailleListItem>? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var user_title: String? = null
    private var mContext: Context? = null

    private var bookmarkBrailleList_tmp: ArrayList<BookmarkBrailleListItem>? = null
    private val LOAD_DATA_SIZE = 50
    private var current_position = 0

    //private var isLoading = false
    //private var isRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_braille)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back) // 홈버튼 이미지 변경
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f

        mContext = this
        val action_bar_title = findViewById<View>(R.id.action_bar_title) as TextView
        action_bar_title.text = "즐겨찾는 점자파일"
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        bookmarkBrailleList = ArrayList<BookmarkBrailleListItem>()
        bookmarkBrailleList_tmp = ArrayList<BookmarkBrailleListItem>()
    }
}