package com.letmeeat.project

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class CustomScannerActivity : Activity(), DecoratedBarcodeView.TorchListener {

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null
    private var backPressCloseHandler: BackPressCloseHandler? = null
    private var setting_btn: ImageButton? = null
    private var switchFlashlightButton: ImageButton? = null
    private var switchFlashlightButtonCheck: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        switchFlashlightButtonCheck = true

        backPressCloseHandler = BackPressCloseHandler(this)

        setting_btn = findViewById(R.id.setting_btn) as ImageButton
        switchFlashlightButton = findViewById(R.id.switch_flashlight) as ImageButton

        if (!hasFlash()) {
            switchFlashlightButton!!.visibility = View.GONE
        }

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
        barcodeScannerView!!.setTorchListener(this)
        capture = CaptureManager(this, barcodeScannerView!!)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()
        val i = intent;

    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        backPressCloseHandler!!.onBackPressed()
    }

    fun switchFlashlight(view: View) {
        if (switchFlashlightButtonCheck!!) {
            barcodeScannerView!!.setTorchOn()
        } else {
            barcodeScannerView!!.setTorchOff()
        }
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onTorchOn() {
        switchFlashlightButton!!.setImageResource(R.drawable.ic_flash_on_white_36dp)
        switchFlashlightButtonCheck = false
    }

    override fun onTorchOff() {
        switchFlashlightButton!!.setImageResource(R.drawable.ic_flash_off_white_36dp)
        switchFlashlightButtonCheck = true
    }
}