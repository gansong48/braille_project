package org.techtown.dotanddoc

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.*
import android.os.*
import android.os.Environment.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer


class TxtBrailleActivity : AppCompatActivity() {

    private var tryDownload: Timer? = null
    private val progressDialog = CustomProgressDialog()

    lateinit var failureAlert: AlertDialog.Builder
    private val handler = object : Handler(Looper.getMainLooper()) {
    }

    lateinit var uploadTxt: String
    lateinit var downloadKey: String

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.)

        Log.d("TxtBrailleActivity", "액티비티 넘어옴")

        //AlertDialog 선언
        failureAlert = AlertDialog.Builder(this)

        val resultTxt = intent.getStringExtra("resultTxt")
        uploadTxt = resultTxt.toString()

        awsS3controll(uploadTxt)
    }

    override fun onBackPressed() {
        tryDownload?.cancel()
        handler.post() {
            Toast.makeText(
                this,
                "다운로드가 취소되었습니다.", Toast.LENGTH_LONG
            ).show()
            val cancelIntent = Intent(this,MainActivity::class.java)
            cancelIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP) //스택제거
            startActivity(cancelIntent)
            finish()
        }
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    fun awsS3controll(intentTxt: String?) {

        progressDialog.show(this, "Please Wait...")

        val nowDate = nowDate()
        val fileName = "$nowDate.txt"
        uploadFile(intentTxt, fileName)

        downloadKey =
            "${nowDate.substring(0, 8)}/$nowDate.brf" // "yyyyMMdd/yyyyMMdd_HHmmss.brf"
        //다운로드 파일 함수 키 파라미터에 public 빼세요

        Handler().postDelayed({ //15초 후 downloadFile 함수 실행
            downloadFile(downloadKey)
        }, 15000)
    }

    fun nowDate(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename"
    }

    private fun uploadFile(txtContent: String?, fileName: String) {

        val uploadFile = File(applicationContext.filesDir, fileName)
        //파일명에 '/'붙이지 말자 오류난다

        val stringTxtContent = txtContent.toString()
        uploadFile.writeText(stringTxtContent)

        val options = StorageUploadFileOptions.defaultInstance()

        val uploadKey = "${fileName.substring(0,8)}/${fileName.substring(0,15)}.txt"
        Log.d("MyAmplifyApp", uploadKey)

        Amplify.Storage.uploadFile(uploadKey, uploadFile, options,
            { Log.i("MyAmplifyApp", "Fraction completed: ${it.fractionCompleted}") },
            { Log.i("MyAmplifyApp", "Successfully uploaded: ${it.key}") },
            { Log.e("MyAmplifyApp", "Upload failed", it) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private fun downloadFile(uploadedKey: String) {

        val fileDir = "${applicationContext.filesDir}/${downloadKey.substring(9)}"
        val file = File(fileDir)

        Log.d("MyAmplifyApp", "save: $fileDir")

        val FailureIntent = Intent(this, MainActivity::class.java)
        FailureIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        //실패해서 메인화면으로 넘어가는 경우 메인화면 외 모든 스택 제거

        val CompleteIntent = Intent(this, SaveCompleteActivity::class.java)

        val options = StorageDownloadFileOptions.defaultInstance()

        var trial = 0
        tryDownload = timer(period = 3000) {
            //3초에 한번씩 재시도
            trial++
            Log.d("MyAmplifyApp", trial.toString())

            Amplify.Storage.downloadFile(uploadedKey, file, options,
                { progress -> Log.d("MyAmplifyApp", "Fraction completed: $progress") },
                { success ->
                    Log.d("MyAmplifyApp", "Successfully downloaded: ${success.file.name}")
                    //로딩 dialog 삭제
                    progressDialog.dialog.dismiss()

                    //외부 저장소 저장
                    val existFile = openFileInput(uploadedKey.substring(9))
                    copyFile(existFile,
                        "${getExternalFilesDir(DIRECTORY_DOWNLOADS)}/new${uploadedKey.substring(9)}")

                    //뷰 전환
                    startActivity(CompleteIntent)
                    cancel()
                    this@TxtBrailleActivity.finish()
                    //현재 액티비티 스택에서 제거
                },
                { exception -> Log.d("MyAmplifyApp", "Download Failure", exception) }
            )

            if (trial == 90) {
                //90번 시도시 cancel()
                progressDialog.dialog.dismiss()
                cancel()

                //실패한 경우 alert show
                handler.post() {
                    failureAlert.setTitle("다운로드에 실패했습니다")
                    failureAlert.setMessage("다시 시도하시겠습니까?")
                    failureAlert.setPositiveButton("에") { dialogInterface: DialogInterface?, i: Int ->
                        Log.d("MyAmplifyApp", "다운로드 재시도")
                        awsS3controll(uploadTxt)
                    }
                    failureAlert.setNegativeButton("아니오") { dialogInterface: DialogInterface?, i: Int ->
                        startActivity(FailureIntent)
                        finish()
                    }
                    failureAlert.show()
                }
            }
        }
    }

    fun copyFile(inputStream: InputStream, filePath: String) {
        val saveFile = File(filePath)

        saveFile.outputStream().use { fileOutput ->
            inputStream.copyTo(fileOutput)
        }
    }
}