package com.example.fakevirus

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var simulationFinished = false

    private val fakeCommands = listOf(
        "root@android:/ # su",
        "root@android:/ # pm uninstall --user 0 com.android.systemui",
        "    [OK] package com.android.systemui removed",
        "root@android:/ # rm -rf /data/data/com.android.*",
        "    [OK] deleted 8473 files",
        "root@android:/ # dd if=/dev/zero of=/dev/block/bootdevice/by-name/boot",
        "    1048576 bytes transferred",
        "root@android:/ # settings put global airplane_mode_on 1",
        "    [OK] radio disabled",
        "root@android:/ # am start -a android.intent.action.MASTER_CLEAR",
        "    [WARN] factory reset initiated...",
        "root@android:/ # iptables -F; iptables -X",
        "    [OK] firewall purged",
        "root@android:/ # echo 1 > /proc/sys/kernel/panic",
        "    [OK] kernel panic triggered",
        "root@android:/ # mv /system/bin/app_process /system/bin/app_process.bak",
        "    [OK] runtime replaced",
        "root@android:/ # for f in /sdcard/*; do shred -n 3 -z \"${'$'}f\"; done",
        "    [OK] sdcard wiped",
        "root@android:/ # setprop ro.secure 0",
        "    [OK] security disabled",
        "root@android:/ # stop; start",
        "    [INFO] zygote restarting...",
        "",
        "[!] CRITICAL: All protections bypassed",
        "[!] Uploading credentials to 192.168.666.666...",
        "[!] Device brick scheduled in T-5 seconds...",
        "",
        "    ...шучу. Это была имитация. Всё в порядке :)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashText = TextView(this).apply {
            text = "ахахаххахах это был вирус"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            gravity = Gravity.CENTER
        }

        val splashLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(splashText, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ))
        }

        setContentView(splashLayout)

        handler.postDelayed({
            showConsole()
        }, 2000)
    }

    private fun showConsole() {
        val consoleText = TextView(this).apply {
            setTextColor(Color.GREEN)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = android.graphics.Typeface.MONOSPACE
            setPadding(32)
        }

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(consoleText, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }

        setContentView(scrollView)

        val fullLog = SpannableStringBuilder()
        var lineIndex = 0

        fun typeNextLine() {
            if (lineIndex >= fakeCommands.size) {
                simulationFinished = true
                return
            }

            val line = fakeCommands[lineIndex]
            lineIndex++

            val start = fullLog.length
            fullLog.append(line).append("\n")

            val color = when {
                line.contains("[!]") -> Color.RED
                line.contains("[WARN]") -> Color.YELLOW
                line.contains("[OK]") || line.contains("[INFO]") -> Color.GREEN
                line.startsWith("root@android") -> Color.CYAN
                else -> Color.WHITE
            }
            fullLog.setSpan(
                ForegroundColorSpan(color),
                start, fullLog.length - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            consoleText.text = fullLog

            scrollView.post {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }

            val delay = if (line.isEmpty()) 200L else Random.nextLong(150, 600)
            handler.postDelayed(::typeNextLine, delay)
        }

        typeNextLine()
    }

    override fun onBackPressed() {
        if (simulationFinished) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
