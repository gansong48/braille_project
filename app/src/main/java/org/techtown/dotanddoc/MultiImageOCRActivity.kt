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

class MultiImageOCRActivity : AppCompatActivity() {

    lateinit var resultIntent : Intent

    lateinit var text_info : EditText
    val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

    var realResult:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)
        text_info = findViewById(R.id.content) //edit.xml의 EditTextView

        resultIntent=Intent(this, BeforeTxtBrailleActivity::class.java)

        //intent.getParcelableExtra<Bitmap>("bitImage") 비트맵으로 받아올 경우.

        val imageArr:ArrayList<String>? = intent.getStringArrayListExtra("multiImage")

        if (imageArr != null) {
            for (i in imageArr){
                val mUri: Uri = Uri.parse(i)
                imageFromPath(this, mUri)
            }

            Log.d("text", realResult)
        }

        edit_finish_btn.setOnClickListener({
            startActivity(resultIntent)
        })
    }

    //uri로부터 이미지 가져오기
    private fun imageFromPath(context: Context, mUri: Uri): String {

        var a:String=""
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, mUri)
            a = recognizeText(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return a
    }

    //텍스트 인식
    private fun recognizeText(image: InputImage): String {

        var a:String=""

        //이미지를 process method로 보냄
        val result = recognizer.process(image)
            //비동기 처리이기 때문에 .addOnSuccessListener는 따로 논다.
            .addOnSuccessListener {
                // Task completed successfully
                // [START_EXCLUDE]

                Log.d("ML KIT", "uri 전달 성공")
                val visionText = it
                a = processTextBlock(visionText)
                realResult += a
                text_info.setText(realResult)
                resultIntent.putExtra("resultTxt", realResult)

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
        return a
    }

    //process에서 받은 텍스트 블록 텍스트 추출
    private fun processTextBlock(result: Text): String {

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
        return resultText
    }
}