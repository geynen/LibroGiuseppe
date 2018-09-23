package com.geynen.librogiuseppe

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AnimacionActivity : AppCompatActivity() {

    @JvmField @BindView(R.id.main_container)
    var mainContainer: ConstraintLayout? = null
    @JvmField @BindView(R.id.imgv_photo)
    var imgvPhoto: SimpleDraweeView? = null
    @JvmField @BindView(R.id.fab_share)
    var fabSharePhoto: FloatingActionButton? = null
    @JvmField @BindView(R.id.imgv_lineas)
    var imgvLineas: SimpleDraweeView? = null

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animacion)
        ButterKnife.bind(this)

        var uri = Uri.parse("res:///" + R.mipmap.lineas_carretera)
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imgvLineas?.controller)
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build()
        imgvLineas?.controller = controller

        imgvPhoto?.setOnClickListener {
            //val bitmap = loadBitmapFromView(findViewById(R.id.imgv_photo), 350, 450)
            //saveImage(bitmap)

            validatePermissions()
        }
        fabSharePhoto?.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                //putExtra(Intent.EXTRA_STREAM, uriToImage)
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report?.areAllPermissionsGranted()==true){
                            launchCamera()
                        }else{
                            Snackbar.make(mainContainer!!,
                                    R.string.storage_permission_denied_message,
                                    Snackbar.LENGTH_LONG)
                                    .show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?,
                                                                    token: PermissionToken?) {
                        AlertDialog.Builder(this@AnimacionActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({ token?.cancelPermissionRequest() })
                                .show()
                    }
                })
                .check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)

        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)

        val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions(width, height))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imgvPhoto?.controller)
                .setImageRequest(request)
                .build()
        imgvPhoto?.controller = controller
    }

    companion object {

        fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
            Log.e("loadBitmapFromView","loadBitmapFromView")
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.layout(0, 0, v.layoutParams.width, v.layoutParams.height)
            v.draw(c)
            return b
        }

        fun saveImage(bitmap: Bitmap) {
            Log.e("saveImage","saveImage")
            val root = Environment.getExternalStorageDirectory().toString()
            val myDir = File(root + "/LibroGiuseppe")
            myDir.mkdirs()
            val generator = Random()
            var n = 10000
            n = generator.nextInt(n)
            val fname = "Image-$n.jpg"
            val file = File(myDir, fname)
            //  Log.i(TAG, "" + file);
            if (file.exists())
                file.delete()
            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
