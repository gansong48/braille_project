package org.techtown.dotanddoc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SaveCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.braille_download)
        //뷰 추가해서 수정해주세요 - 추가완료

        val finishDownload = findViewById<Button>(R.id.finish_download)

        finishDownload.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
        }

        /*
        setOnclickListener {
            saveDownloadedFile()
        }

         */
    }
    //공용저장소 저장 기능 구현 예정
    /*
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    fun saveDownloadedFile() {
        try {
            val newFileName = "new" + nowDate() + ".brf"

            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/braille"
                putExtra(Intent.EXTRA_TITLE, newFileName)
            }
            startActivityForResult(intent, CREATE_FILE)
        } catch(e: IOException) {
            handler.showAlert()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CREATE_FILE -> if (resultCode == RESULT_OK && data != null) {
                writeFile(data)
                } else {
                    handler.showAlert()
            }
        }
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun writeFile(data: Intent?) {
        val uri = data?.data

        uri.let {
            val takeFlags = (intent.flags
                and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            if (it != null) {
                contentResolver.takePersistableUriPermission(it, takeFlags)
            }
        }

        uri?.toFile()?.let {
            File("${applicationContext.filesDir}/${downloadKey.substring(9)}").copyTo(
                it, true
            )
        }
    }
     */
}