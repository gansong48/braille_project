package org.techtown.dotanddoc

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

//유정이 머지 파일에 코드 합친 거
class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener{

    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99

    val FLAG_REQ_CAMERA = 101
    val FLAG_REQ_STORAGE = 102

    var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        main_navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        findViewById<View>(R.id.img_select_btn).setOnClickListener { view ->
            val popupMenu = PopupMenu(applicationContext, view)
            menuInflater.inflate(R.menu.img_select, popupMenu.getMenu())

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.gallery) {
                        if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
                            openGallery()
                        }
                        return true


                    } else if (menuItem.itemId == R.id.camera) {
                        if (checkPermission(STORAGE_PERMISSION, FLAG_PERM_STORAGE)) {
                            openCamera()
                        }
                    }
                    return false
                }
            })
            popupMenu.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val homeIntent = Intent(this, MainActivity::class.java)

        val transDocIntent = Intent(this, TransformedDocActivity::class.java)

        val transBrailleIntent = Intent(this, TransformedBrailleActivity::class.java)

        val bookmarkIntent = Intent(this, BookmarkActivity::class.java)

        val setIntent = Intent(this, SetActivity::class.java)

        when(item.itemId){
            R.id.item1-> startActivity(homeIntent)
            R.id.item2-> startActivity(transDocIntent)
            R.id.item3-> startActivity(transBrailleIntent)
            R.id.item4-> startActivity(bookmarkIntent)
            R.id.item5-> startActivity(setIntent)
        }
        return false
    }

    fun setViews() {
        fun onMenuItemClick(menuItem: MenuItem): Boolean {
            if (menuItem.itemId == R.id.gallery) {
                openGallery()
                return true
            } else if (menuItem.itemId == R.id.camera) {
                openCamera()
            }
            return false
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, FLAG_REQ_STORAGE)

    }

    fun createImageUri(filename: String, mimeType: String) : Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    // 카메라 원본이미지 Uri를 저장할 변
    var photoURI: Uri? = null
    private fun dispatchTakePictureIntent() {
        // 카메라 인텐트 생성
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        createImageUri(newFileName(), "image/jpg")?.let { uri ->
            photoURI = uri
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, FLAG_REQ_CAMERA)
        }
    }

    fun openCamera() {
        if (checkPermission(CAMERA_PERMISSION, FLAG_PERM_CAMERA)) {
            dispatchTakePictureIntent()
        }
    }

    fun loadBitmapFromMediaStoreBy(photoUri: Uri): Bitmap? {
        var image: Bitmap? = null
        try {
            image = if (Build.VERSION.SDK_INT > 27) { // Api 버전별 이미지 처리
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(this.contentResolver, photoUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val OcrIntent = Intent(this, OCRActivity::class.java)
            when (requestCode){
                FLAG_REQ_CAMERA -> {
                        if (photoURI != null) {
                            val bitmap = loadBitmapFromMediaStoreBy(photoURI!!)
                            photoURI = null // 사용 후 null 처리
                            val uri = bitmap?.let { saveImageFile(newFileName(), "image/jpg", it) }
                            OcrIntent.putExtra("ocrImage", uri.toString())
                            startActivity(OcrIntent)
                        }
                    }

                FLAG_REQ_STORAGE -> {

                        list.clear()

                        if (data?.clipData != null) { // 사진 여러개 선택한 경우
                            val count = data.clipData!!.itemCount
                            if (count > 10) {
                                Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
                                return
                            }
                            for (i in 0 until count) {

                                val imageUri = data.clipData!!.getItemAt(i).uri

                                if (imageUri != null) {
                                    list.add(imageUri.toString())

                                    val multiIntent = Intent(this, MultiImageOCRActivity::class.java)
                                    multiIntent.putExtra("multiImage", list)
                                    startActivity(multiIntent)
                                }
                            }

                        } else { // 단일 선택

                            data?.data?.let { uri ->
                                val imageUri : Uri? = data?.data
                                if (imageUri != null) {
                                    list.add(imageUri.toString())

                                    val multiIntent = Intent(this, MultiImageOCRActivity::class.java)
                                    multiIntent.putExtra("multiImage", list)
                                    startActivity(multiIntent)
                                }
                            }
                        }

                    }

                }
            }
    }


    fun saveImageFile(filename:String, mimeType:String, bitmap: Bitmap) : Uri? {
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        try {
            if (uri != null) {
                var descriptor = contentResolver.openFileDescriptor(uri, "w")
                if (descriptor != null) {
                    val fos = FileOutputStream(descriptor.fileDescriptor)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(uri, values, null, null)
                    }
                }
            }
        } catch (e:java.lang.Exception) {
            Log.e("File", "error=${e.localizedMessage}")
        }
        Log.e("DATA?", uri.toString())
        return uri
    }

    fun newFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())

        return "$filename.jpg"
    }

    fun checkPermission(permissions: Array<out String>, flag: Int) : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, flag)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FLAG_PERM_STORAGE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "저장소 권한을 승인해야지만 앱을 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                }
                setViews()
            }

            FLAG_PERM_CAMERA -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "카메라 권한을 승인 해야지만 카메라를 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                        return
                    }
                }
                openCamera()
            }
        }
    }
}








