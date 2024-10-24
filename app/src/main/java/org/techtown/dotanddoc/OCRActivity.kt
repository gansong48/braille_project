package org.techtown.dotanddoc

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import kotlinx.android.synthetic.main.edit.*
import java.io.IOException

class OCRActivity : AppCompatActivity() {

    lateinit var text_info : EditText
    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)
        text_info = findViewById(R.id.content) //edit.xml의 EditTextView

        //intent.getParcelableExtra<Bitmap>("bitImage") 비트맵으로 받아올 경우.

        var ocrImage = intent.getStringExtra("ocrImage")
        val uri: Uri = Uri.parse(ocrImage) //이 uri 변수 사용하면 됩니다!!!

        imageFromPath(this, uri)

        edit_finish_btn.setOnClickListener({
            val TxtIntent = Intent(this, BeforeTxtBrailleActivity::class.java)
            startActivity(TxtIntent)
        })
    }

    //uri로부터 이미지 가져오기
    private fun imageFromPath(context: Context, uri: Uri) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            recognizeText(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //텍스트 인식
    private fun recognizeText(image: InputImage) {
        //이미지를 process method로 보냄
        val result = recognizer.process(image)
            .addOnSuccessListener {
                // Task completed successfully
                // [START_EXCLUDE]
                Log.d("ML KIT", "uri 전달 성공")
                val visionText = it
                processTextBlock(visionText)
                // [END get_text]
                // [END_EXCLUDE]
            }
            .addOnFailureListener {
                //이미지 인식이 실패했을 때
                //사용자에게 alert 작성
                Toast.makeText(this,"이미지 인식에 실패했습니다.",Toast.LENGTH_SHORT).show()
                //전 화면으로 돌아가는 코드 작성
                val noImageIntent = Intent(this, MainActivity::class.java)
                startActivity(noImageIntent)
            }
    }

    //process에서 받은 텍스트 블록 텍스트 추출
    private fun processTextBlock(result: Text) {
        val resultIntent = Intent(this, TxtBrailleActivity::class.java)

        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
        text_info.setText(resultText)
        resultIntent.putExtra("resultTxt", resultText)
    }
}