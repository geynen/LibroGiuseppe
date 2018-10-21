package com.geynen.librogiuseppe

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AnimacionActivity : AppCompatActivity() {

    @JvmField @BindView(R.id.main_container)
    var mainContainer: ConstraintLayout? = null
    @JvmField @BindView(R.id.imgv_bg)
    var imgvBg: ImageView? = null
    @JvmField @BindView(R.id.imgv_photo)
    var imgvPhoto: SimpleDraweeView? = null
    @JvmField @BindView(R.id.imgv_calavera)
    var imgvCalavera: SimpleDraweeView? = null
    @JvmField @BindView(R.id.fab_home)
    var fabHome: FloatingActionButton? = null
    @JvmField @BindView(R.id.fab_share)
    var fabSharePhoto: FloatingActionButton? = null
    @JvmField @BindView(R.id.fab_info)
    var fabInfo: FloatingActionButton? = null
    @JvmField @BindView(R.id.imgv_lineas)
    var imgvLineas: SimpleDraweeView? = null
    @JvmField @BindView(R.id.imgv_nube_01)
    var imgvNube01: ImageView? = null
    @JvmField @BindView(R.id.imgv_nube_02)
    var imgvNube02: ImageView? = null
    @JvmField @BindView(R.id.imgv_nube_03)
    var imgvNube03: ImageView? = null

    @JvmField @BindView(R.id.btnFoco1)
    var btnFoco01: Button? = null
    @JvmField @BindView(R.id.btnFoco2)
    var btnFoco02: Button? = null

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""
    private var mCaptureScreenPath: String = ""
    private var iliminado = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animacion)
        ButterKnife.bind(this)

        /* Animar Gif - Inicio */
        var uri = Uri.parse("res:///" + R.mipmap.lineas_carretera)
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imgvLineas?.controller)
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build()
        imgvLineas?.controller = controller
        /* Animar Gif - Fin */

        imgvPhoto?.setOnClickListener {
            validatePermissions()
        }

        var btnScan: View = findViewById(R.id.imgv_photo)

        SimpleTooltip.Builder(this)
                .anchorView(btnScan)
                .text("¿Cómo te verías al lado de la Catrina?")
                .gravity(Gravity.TOP)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show()

        fabHome?.setOnClickListener {
            finish()
        }
        fabSharePhoto?.setOnClickListener {
            validatePermissionsShare()
        }

        fabInfo?.setOnClickListener {

            val intent = Intent(this, PaginaDetalleActivity::class.java)
            startActivity(intent)
        }

        btnFoco01?.setOnClickListener {
            iluminar()
        }

        btnFoco02?.setOnClickListener {
            iluminar()
        }

        /* Mover Calavera - Inicio */
        // Create shake effect from xml resource
        val shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_calavera)
        // View element to be shaken
        // Perform animation
        imgvCalavera?.startAnimation(shake)
        /* Mover Calavera - Fin */

        /* Mover Ambulancia - Inicio */
        val shake_bg = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.up_down)
        imgvBg?.startAnimation(shake_bg)

        shake_bg.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                // not implemented
            }

            override fun onAnimationRepeat(p0: Animation?) {
                // not implemented
            }

            override fun onAnimationEnd(p0: Animation?) {
                /* Mover Ambulancia - Inicio */
                val shake_bg2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_ambulancia)
                imgvBg?.startAnimation(shake_bg2)
                /* Mover Ambulancia - Fin */
            }
        })
        /* Mover Ambulancia - Fin */

        /* Mover Nube 01 - Inicio */
        val shake_nube_01 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_nube2)
        imgvNube01?.startAnimation(shake_nube_01)
        /* Mover Nube 01 - Fin */

        /* Mover Nube 02 - Inicio */
        val shake_nube_02 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_nube)
        imgvNube02?.startAnimation(shake_nube_02)
        /* Mover Nube 02 - Fin */

        /* Mover Nube 03 - Inicio */
        val shake_nube_03 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_nube)
        imgvNube03?.startAnimation(shake_nube_03)
        /* Mover Nube 03 - Fin */
    }

    override fun onResume() {
        super.onResume()

        /* Reproducir audios - Inicio */
        val mp = MediaPlayer.create(this, R.raw.fear)
        mp.start()
        /* Reproducir audios - Fin */
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

    private fun shareCapturedPhoto() {
        Log.e("mCaptureScreenPath",mCaptureScreenPath)

        //Share Intent
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, Uri.parse(mCaptureScreenPath))
            putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.send_to_subject));
            putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.send_to_body));
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))
    }

    fun loadBitmapFromView(v: View, width: Int, height: Int): Bitmap {
        Log.e("loadBitmapFromView","loadBitmapFromView")
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        v.layout(0, 0, v.width, v.height)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCaptureScreenPath = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file).toString();
        }else{
            mCaptureScreenPath = Uri.fromFile(file).toString();
        }

        shareCapturedPhoto()

    }

    private fun compartir(){
        fabSharePhoto?.isEnabled = false
        fabHome?.visibility = View.INVISIBLE
        fabSharePhoto?.visibility = View.INVISIBLE
        fabInfo?.visibility = View.INVISIBLE
        //Toast.makeText(this,resources.getText(R.string.send_to_toast),Toast.LENGTH_SHORT).show()
        val bitmap = loadBitmapFromView(findViewById(R.id.main_container), 350, 450)
        fabHome?.visibility = View.VISIBLE
        fabSharePhoto?.visibility = View.VISIBLE
        fabInfo?.visibility = View.VISIBLE
        saveImage(bitmap)
        fabSharePhoto?.isEnabled = true
    }

    private fun validatePermissionsShare() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report?.areAllPermissionsGranted()==true){
                            compartir()
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

    private fun iluminar(){
        if(iliminado){
            imgvBg?.setImageDrawable(ContextCompat.getDrawable(
                    applicationContext,
                    R.mipmap.ambulancia
            ))

            imgvCalavera?.setActualImageResource(R.mipmap.cabeza_catrina)

            if(mCurrentPhotoPath.equals("")) {
                imgvPhoto?.setActualImageResource(R.mipmap.cabeza_copiloto)
            }
        }else{
            imgvBg?.setImageDrawable(ContextCompat.getDrawable(
                    applicationContext,
                    R.mipmap.ambulancia_clara
            ))

            imgvCalavera?.setActualImageResource(R.mipmap.cabeza_catrina_clara)
            imgvCalavera?.scaleType = ImageView.ScaleType.FIT_XY

            if(mCurrentPhotoPath.equals("")) {
                imgvPhoto?.setActualImageResource(R.mipmap.cabeza_copiloto_clara)
            }
        }
        iliminado = !iliminado
    }
}
