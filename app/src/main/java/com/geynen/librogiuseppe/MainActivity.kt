package com.geynen.librogiuseppe

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.geynen.librogiuseppe.barcode.BarcodeCaptureActivity
import android.view.Gravity
import android.view.View
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip



class MainActivity : AppCompatActivity() {

    private lateinit var mResultTextView: TextView
    private lateinit var mp:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mResultTextView = findViewById(R.id.result_textview)

        findViewById<Button>(R.id.scan_barcode_button).setOnClickListener {
            val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
        }

        /* Reproducir audios - Inicio */
        mp = MediaPlayer.create(this, R.raw.ambiente)
        /* Reproducir audios - Fin */

        //Para desarrollo
        //val intent = Intent(this, AnimacionActivity::class.java)
        //startActivity(intent)

        var btnScan: View = findViewById(R.id.scan_barcode_button)

        SimpleTooltip.Builder(this)
                .anchorView(btnScan)
                .text("Selecciona un código del libro")
                .gravity(Gravity.TOP)
                .animated(true)
                .transparentOverlay(false)
                .build()
                .show()

    }

    override fun onResume() {
        super.onResume()

        /* Reproducir audios - Inicio */
        mp.start()
        /* Reproducir audios - Fin */
    }

    override fun onPause() {
        super.onPause()

        /* Reproducir audios - Inicio */
        if (mp.isPlaying ()) {
            mp.stop()
            mp.seekTo (0)
        }
        /* Reproducir audios - Fin */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>(BarcodeCaptureActivity.BarcodeObject)
                    val p = barcode.cornerPoints
                    mResultTextView.text = barcode.displayValue

                    Log.e(LOG_TAG, barcode.displayValue)
                    if(barcode.displayValue.equals("Cuento5Catrina")) {
                        val intent = Intent(this, AnimacionActivity::class.java)
                        // To pass any data to next activity
                        intent.putExtra("barcode", barcode.displayValue)
                        // start your next activity
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,resources.getText(R.string.barcode_invalido), Toast.LENGTH_SHORT).show()
                    }
                } else
                    mResultTextView.setText(R.string.no_barcode_captured)
            } else
                Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                        CommonStatusCodes.getStatusCodeString(resultCode)))
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private val BARCODE_READER_REQUEST_CODE = 1
    }
}
