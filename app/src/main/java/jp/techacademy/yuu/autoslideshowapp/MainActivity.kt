package jp.techacademy.yuu.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),View.OnClickListener {

    private var cursor:Cursor? = null
    private var running_flg = 0
    private var fieldIndex:Int = 0
    private var id:Long = 0
    private var imageUri: Uri? = null
    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BT_START_STOP.text = "再生"
        running_flg = 0

        BT_START_STOP.setOnClickListener(this)
        BT_NEXT.setOnClickListener(this)
        BT_PREVIOUS.setOnClickListener(this)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContensInfoFirst()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContensInfoFirst()
        }
    }

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<String>,grantResults:IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContensInfoFirst()
                }
        }
    }

    override fun onClick(v:View){

        when(v.id) {
            R.id.BT_START_STOP ->
                if (running_flg == 0) {
                    getContentsInfoAuto()
                } else {
                    stopContentsInfo()
                }

            R.id.BT_NEXT ->
                getContensInfoNext()

            R.id.BT_PREVIOUS ->
                getContensInfoPrevious()
        }
    }

    private fun getContensInfoFirst(){

        val resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()){

            // indexからIDを取得し、そのIDから画像のURIを取得する
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            id = cursor!!.getLong(fieldIndex)
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            Log.d("ANDROID", "URI : " + imageUri.toString())

        }
    }

    private fun getContentsInfoAuto(){

        BT_START_STOP.text = "停止"
        BT_NEXT.isClickable = false
        BT_PREVIOUS.isClickable = false
        running_flg = 1

        if (mTimer == null){
            mTimer = Timer()
            mTimer!!.schedule(object:TimerTask(){
                override fun run(){
                    mHandler.post{

                        getContensInfoNext()

                    }
                }
            },2000,2000)
        }
    }

    private fun stopContentsInfo(){

        BT_START_STOP.text = "再生"
        BT_NEXT.isClickable = true
        BT_PREVIOUS.isClickable = true
        running_flg = 0

        if (mTimer != null){
            mTimer!!.cancel()
            mTimer = null
        }
    }

    private fun getContensInfoNext(){

        if (cursor!!.moveToNext()){

            // indexからIDを取得し、そのIDから画像のURIを取得する
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            id = cursor!!.getLong(fieldIndex)
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            Log.d("ANDROID", "URI : " + imageUri.toString())

        } else {
            if (cursor!!.moveToFirst()) {

                // indexからIDを取得し、そのIDから画像のURIを取得する
                fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                id = cursor!!.getLong(fieldIndex)
                imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())

            }
        }
    }

    private fun getContensInfoPrevious(){

        if (cursor!!.moveToPrevious()){

            // indexからIDを取得し、そのIDから画像のURIを取得する
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            id = cursor!!.getLong(fieldIndex)
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
            Log.d("ANDROID", "URI : " + imageUri.toString())

        } else {
            if (cursor!!.moveToLast()) {

                // indexからIDを取得し、そのIDから画像のURIを取得する
                fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                id = cursor!!.getLong(fieldIndex)
                imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
                Log.d("ANDROID", "URI : " + imageUri.toString())

            }
        }
    }
}