package com.example.fakevirus

import android.content.Intent
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
    private var isDestroyed = false

    private val consoleLines = mutableListOf<ConsoleLine>()

    sealed class ConsoleLine {
        data class Prompt(val text: String) : ConsoleLine()
        data class Progress(val text: String, val delayMs: Long) : ConsoleLine()
        data class Result(val text: String, val color: Int) : ConsoleLine()
        data class Empty(val delayMs: Long) : ConsoleLine()
    }

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

        generateSequence()

        handler.postDelayed({
            if (!isDestroyed) showConsole()
        }, 2000)
    }

    private fun generateSequence() {
        // 1. su
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # su"))
        consoleLines.add(ConsoleLine.Progress("checking device root status...", 400))
        consoleLines.add(ConsoleLine.Progress("exploiting CVE-2024-XXXX...", 600))
        consoleLines.add(ConsoleLine.Progress("elevating privileges...", 500))
        consoleLines.add(ConsoleLine.Result("[OK] root access granted", Color.GREEN))

        // 2. pm uninstall systemui
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # pm uninstall --user 0 com.android.systemui"))
        consoleLines.add(ConsoleLine.Progress("analyzing package com.android.systemui...", 300))
        consoleLines.add(ConsoleLine.Progress("removing APK from /system/priv-app/SystemUI/...", 500))
        consoleLines.add(ConsoleLine.Progress("cleaning dalvik-cache...", 400))
        consoleLines.add(ConsoleLine.Progress("removing data directory /data/data/com.android.systemui/...", 600))
        consoleLines.add(ConsoleLine.Result("[OK] package com.android.systemui removed", Color.GREEN))

        // 3. rm -rf
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # rm -rf /data/data/com.android.*"))
        consoleLines.add(ConsoleLine.Progress("scanning /data/data/...", 300))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.settings (2.4 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.systemui (5.1 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.launcher (3.8 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.phone (4.2 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.providers.media (1.9 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("...", 300))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.vending (12.7 MB)...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting com.android.chrome (89.3 MB)...", 400))
        consoleLines.add(ConsoleLine.Progress("removing shared preferences...", 300))
        consoleLines.add(ConsoleLine.Progress("wiping databases...", 400))
        consoleLines.add(ConsoleLine.Result("[OK] deleted 8473 files, freed 1.2 GB", Color.GREEN))

        // 4. dd
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # dd if=/dev/zero of=/dev/block/bootdevice/by-name/boot"))
        consoleLines.add(ConsoleLine.Progress("opening source /dev/zero...", 300))
        consoleLines.add(ConsoleLine.Progress("opening target /dev/block/bootdevice/by-name/boot...", 300))
        consoleLines.add(ConsoleLine.Progress("transferring 1048576 bytes...", 800))
        consoleLines.add(ConsoleLine.Progress("524288/1048576 bytes (50%)", 400))
        consoleLines.add(ConsoleLine.Progress("786432/1048576 bytes (75%)", 300))
        consoleLines.add(ConsoleLine.Progress("1048576/1048576 bytes (100%)", 300))
        consoleLines.add(ConsoleLine.Result("[OK] boot partition overwritten", Color.GREEN))

        // 5. airplane mode
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # settings put global airplane_mode_on 1"))
        consoleLines.add(ConsoleLine.Progress("accessing secure settings database...", 300))
        consoleLines.add(ConsoleLine.Progress("modifying global.airplane_mode_on...", 200))
        consoleLines.add(ConsoleLine.Progress("sending broadcast android.intent.action.AIRPLANE_MODE...", 400))
        consoleLines.add(ConsoleLine.Result("[OK] radio disabled, all connections terminated", Color.GREEN))

        // 6. factory reset
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # am start -a android.intent.action.MASTER_CLEAR"))
        consoleLines.add(ConsoleLine.Progress("constructing intent MASTER_CLEAR...", 300))
        consoleLines.add(ConsoleLine.Progress("sending to ActivityManager...", 400))
        consoleLines.add(ConsoleLine.Progress("verifying device owner privileges...", 500))
        consoleLines.add(ConsoleLine.Result("[WARN] factory reset initiated, confirmation bypassed", Color.YELLOW))

        // 7. iptables
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # iptables -F; iptables -X"))
        consoleLines.add(ConsoleLine.Progress("flushing INPUT chain...", 250))
        consoleLines.add(ConsoleLine.Progress("flushing OUTPUT chain...", 250))
        consoleLines.add(ConsoleLine.Progress("flushing FORWARD chain...", 250))
        consoleLines.add(ConsoleLine.Progress("deleting custom chains...", 300))
        consoleLines.add(ConsoleLine.Progress("removing nat table rules...", 300))
        consoleLines.add(ConsoleLine.Result("[OK] firewall purged, device is open", Color.GREEN))

        // 8. kernel panic
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # echo 1 > /proc/sys/kernel/panic"))
        consoleLines.add(ConsoleLine.Progress("accessing procfs /proc/sys/kernel/panic...", 300))
        consoleLines.add(ConsoleLine.Progress("writing panic trigger...", 200))
        consoleLines.add(ConsoleLine.Progress("kernel panic scheduled on next watchdog timeout...", 500))
        consoleLines.add(ConsoleLine.Result("[OK] kernel panic triggered", Color.GREEN))

        // 9. mv app_process
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # mv /system/bin/app_process /system/bin/app_process.bak"))
        consoleLines.add(ConsoleLine.Progress("checking /system/bin/app_process (2.1 MB)...", 300))
        consoleLines.add(ConsoleLine.Progress("remounting /system as rw...", 500))
        consoleLines.add(ConsoleLine.Progress("moving app_process -> app_process.bak...", 400))
        consoleLines.add(ConsoleLine.Progress("verifying checksum...", 300))
        consoleLines.add(ConsoleLine.Result("[OK] runtime replaced, system will not boot", Color.GREEN))

        // 10. shred sdcard
        consoleLines.add(ConsoleLine.Prompt("""root@android:/ # for f in /sdcard/*; do shred -n 3 -z "${'$'}f"; done"""))
        consoleLines.add(ConsoleLine.Progress("enumerating /sdcard/...", 300))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 1/3]...", 400))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 2/3]...", 400))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 3/3]...", 400))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 1/3]...", 350))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 2/3]...", 350))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 3/3]...", 350))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/Documents/passwords.txt (0.01 MB) [pass 1/3]...", 200))
        consoleLines.add(ConsoleLine.Progress("...", 300))
        consoleLines.add(ConsoleLine.Progress("shredding /sdcard/Movies/vacation.mp4 (156.7 MB) [pass 3/3]...", 800))
        consoleLines.add(ConsoleLine.Result("[OK] 47 files shredded, recovery impossible", Color.GREEN))

        // 11. setprop
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # setprop ro.secure 0"))
        consoleLines.add(ConsoleLine.Progress("accessing property service...", 300))
        consoleLines.add(ConsoleLine.Progress("modifying ro.secure = 0...", 200))
        consoleLines.add(ConsoleLine.Progress("disabling SELinux enforcing mode...", 400))
        consoleLines.add(ConsoleLine.Progress("setting ro.debuggable = 1...", 200))
        consoleLines.add(ConsoleLine.Result("[OK] security disabled, ADB unrestricted", Color.GREEN))

        // 12. stop; start
        consoleLines.add(ConsoleLine.Prompt("root@android:/ # stop; start"))
        consoleLines.add(ConsoleLine.Progress("sending stop to init daemon...", 400))
        consoleLines.add(ConsoleLine.Progress("terminating zygote (PID 1234)...", 500))
        consoleLines.add(ConsoleLine.Progress("killing system_server (PID 1567)...", 400))
        consoleLines.add(ConsoleLine.Progress("all user processes terminated", 300))
        consoleLines.add(ConsoleLine.Progress("restarting zygote64...", 600))
        consoleLines.add(ConsoleLine.Progress("preloading classes...", 800))
        consoleLines.add(ConsoleLine.Progress("starting system_server...", 700))
        consoleLines.add(ConsoleLine.Result("[INFO] zygote restarted, system compromised", Color.GREEN))

        // Финал
        consoleLines.add(ConsoleLine.Empty(400))
        consoleLines.add(ConsoleLine.Result("[!] CRITICAL: All protections bypassed", Color.RED))
        consoleLines.add(ConsoleLine.Result("[!] Uploading credentials to 192.168.666.666...", Color.RED))
        consoleLines.add(ConsoleLine.Progress("establishing connection...", 600))
        consoleLines.add(ConsoleLine.Progress("encrypting payload...", 500))
        consoleLines.add(ConsoleLine.Progress("uploading contacts.db (2347 entries)...", 700))
        consoleLines.add(ConsoleLine.Progress("uploading messages.db (12893 entries)...", 800))
        consoleLines.add(ConsoleLine.Result("[!] Upload complete. Device brick scheduled in T-5 seconds...", Color.RED))
        consoleLines.add(ConsoleLine.Empty(600))
        consoleLines.add(ConsoleLine.Result("    ...шучу. Это была имитация. Всё в порядке :)", Color.WHITE))
    }

    private fun showConsole() {
        val consoleText = TextView(this).apply {
            setTextColor(Color.GREEN)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = android.graphics.Typeface.MONOSPACE
            setPadding(24)
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
        var index = 0

        fun typeNext() {
            if (isDestroyed) return
            if (index >= consoleLines.size) {
                simulationFinished = true
                return
            }

            val line = consoleLines[index]
            index++

            when (line) {
                is ConsoleLine.Prompt -> {
                    val start = fullLog.length
                    fullLog.append(line.text).append("\n")
                    fullLog.setSpan(
                        ForegroundColorSpan(Color.CYAN),
                        start, fullLog.length - 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                is ConsoleLine.Progress -> {
                    val start = fullLog.length
                    fullLog.append(line.text).append("\n")
                    fullLog.setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        start, fullLog.length - 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    consoleText.text = fullLog
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    handler.postDelayed(::typeNext, line.delayMs)
                    return
                }
                is ConsoleLine.Result -> {
                    val start = fullLog.length
                    fullLog.append(line.text).append("\n")
                    fullLog.setSpan(
                        ForegroundColorSpan(line.color),
                        start, fullLog.length - 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                is ConsoleLine.Empty -> {
                    fullLog.append("\n")
                    consoleText.text = fullLog
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    handler.postDelayed(::typeNext, line.delayMs)
                    return
                }
            }

            consoleText.text = fullLog
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }

            val delay = Random.nextLong(80, 250)
            handler.postDelayed(::typeNext, delay)
        }

        typeNext()
    }

    override fun onBackPressed() {
        if (simulationFinished) {
            super.onBackPressed()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!simulationFinished) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!simulationFinished && !isDestroyed) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        isDestroyed = true
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
