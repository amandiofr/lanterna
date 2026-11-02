package fr.amandio.lanterna

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/** Launcher entry point: switches the torch on or off, then disappears without showing anything. */
class ToggleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, TorchService::class.java)
        if (TorchService.isOn) {
            stopService(intent)
        } else {
            startForegroundService(intent)
        }
        finish()
    }
}
