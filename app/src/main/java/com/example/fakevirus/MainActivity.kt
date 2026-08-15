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
import android.view.View
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
    private var index = 0
    private lateinit var consoleText: TextView
    private lateinit var scrollView: ScrollView
    private val fullLog = SpannableStringBuilder()

    sealed class ConsoleLine {
        data class TypingPrompt(val text: String) : ConsoleLine()
        data class AnimatedProgress(val baseText: String, val durationMs: Long) : ConsoleLine()
        data class Result(val text: String, val color: Int) : ConsoleLine()
        data class FinalMessage(val text: String) : ConsoleLine()
        data class Empty(val delayMs: Long) : ConsoleLine()
    }

    private val foregroundChecker = object : Runnable {
        override fun run() {
            if (isDestroyed || simulationFinished) return
            if (!hasWindowFocus()) {
                bringToFront()
            }
            handler.postDelayed(this, 250)
        }
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

    private fun showConsole() {
        consoleText = TextView(this).apply {
            setTextColor(Color.GREEN)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = android.graphics.Typeface.MONOSPACE
            setPadding(24)
        }

        scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.BLACK)
            setOnTouchListener { _, _ -> true }
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            addView(consoleText, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }

        setContentView(scrollView)
        handler.post(foregroundChecker)
        typeNext()
    }

    private fun bringToFront() {
        if (simulationFinished || isDestroyed) return
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
            )
        }
        startActivity(intent)
    }

    private fun typeNext() {
        if (isDestroyed) return
        if (index >= consoleLines.size) {
            finishSimulation()
            return
        }

        val line = consoleLines[index]
        index++

        when (line) {
            is ConsoleLine.TypingPrompt -> {
                val start = fullLog.length
                val text = line.text
                var charIndex = 0

                fun typeChar() {
                    if (isDestroyed) return
                    if (charIndex < text.length) {
                        fullLog.append(text[charIndex])
                        fullLog.setSpan(
                            ForegroundColorSpan(Color.CYAN),
                            start, fullLog.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        charIndex++
                        consoleText.text = fullLog
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                        handler.postDelayed(::typeChar, Random.nextLong(12, 28))
                    } else {
                        fullLog.append("\n")
                        consoleText.text = fullLog
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                        handler.postDelayed(::typeNext, Random.nextLong(80, 200))
                    }
                }
                typeChar()
            }

            is ConsoleLine.AnimatedProgress -> {
                val start = fullLog.length
                val baseText = line.baseText
                val duration = line.durationMs
                val startTime = System.currentTimeMillis()
                var dotState = 0

                fun animateDots() {
                    if (isDestroyed) return
                    val elapsed = System.currentTimeMillis() - startTime

                    if (fullLog.length > start) {
                        fullLog.delete(start, fullLog.length)
                    }

                    val dots = when (dotState % 4) {
                        0 -> ""
                        1 -> "."
                        2 -> ".."
                        else -> "..."
                    }
                    dotState++

                    fullLog.append(baseText).append(dots)
                    fullLog.setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        start, fullLog.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    consoleText.text = fullLog
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }

                    if (elapsed < duration) {
                        handler.postDelayed(::animateDots, 300)
                    } else {
                        if (fullLog.length > start) {
                            fullLog.delete(start, fullLog.length)
                        }
                        fullLog.append(baseText).append("...")
                        fullLog.setSpan(
                            ForegroundColorSpan(Color.WHITE),
                            start, fullLog.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        fullLog.append("\n")
                        consoleText.text = fullLog
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                        handler.postDelayed(::typeNext, Random.nextLong(100, 300))
                    }
                }
                animateDots()
            }

            is ConsoleLine.Result -> {
                val start = fullLog.length
                fullLog.append(line.text).append("\n")
                fullLog.setSpan(
                    ForegroundColorSpan(line.color),
                    start, fullLog.length - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                consoleText.text = fullLog
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                handler.postDelayed(::typeNext, Random.nextLong(80, 200))
            }

            is ConsoleLine.FinalMessage -> {
                fullLog.append("\n")
                val start = fullLog.length
                fullLog.append(line.text).append("\n")
                val end = fullLog.length - 1

                fullLog.setSpan(
                    ForegroundColorSpan(Color.GREEN),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                fullLog.setSpan(
                    android.text.style.RelativeSizeSpan(1.6f),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                fullLog.setSpan(
                    android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                fullLog.setSpan(
                    android.text.style.BackgroundColorSpan(Color.argb(80, 0, 80, 0)),
                    start, end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                consoleText.text = fullLog
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                finishSimulation()
            }

            is ConsoleLine.Empty -> {
                fullLog.append("\n")
                consoleText.text = fullLog
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                handler.postDelayed(::typeNext, line.delayMs)
            }
        }
    }

    private fun finishSimulation() {
        simulationFinished = true
        handler.removeCallbacks(foregroundChecker)
        handler.postDelayed({
            if (!isDestroyed) {
                finish()
                System.exit(0)
            }
        }, 5000)
    }

    override fun onBackPressed() {
        if (simulationFinished) {
            super.onBackPressed()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (!simulationFinished) bringToFront()
    }

    override fun onPause() {
        super.onPause()
        if (!simulationFinished && !isDestroyed) bringToFront()
    }

    override fun onStop() {
        super.onStop()
        if (!simulationFinished && !isDestroyed) bringToFront()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus && !simulationFinished && !isDestroyed) {
            bringToFront()
        }
    }

    private fun generateSequence() {
        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # su"))
        consoleLines.add(ConsoleLine.AnimatedProgress("checking device root status", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("exploiting CVE-2024-XXXX", 800))
        consoleLines.add(ConsoleLine.AnimatedProgress("elevating privileges", 600))
        consoleLines.add(ConsoleLine.Result("[OK] root access granted", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # pm uninstall --user 0 com.android.systemui"))
        consoleLines.add(ConsoleLine.AnimatedProgress("analyzing package com.android.systemui", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("removing APK from /system/priv-app/SystemUI", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("cleaning dalvik-cache", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("removing data directory /data/data/com.android.systemui", 700))
        consoleLines.add(ConsoleLine.Result("[OK] package com.android.systemui removed", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # rm -rf /data/data/com.android.*"))
        consoleLines.add(ConsoleLine.AnimatedProgress("scanning /data/data", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.settings (2.4 MB)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.systemui (5.1 MB)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.launcher (3.8 MB)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.phone (4.2 MB)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.providers.media (1.9 MB)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.vending (12.7 MB)", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting com.android.chrome (89.3 MB)", 800))
        consoleLines.add(ConsoleLine.AnimatedProgress("removing shared preferences", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("wiping databases", 500))
        consoleLines.add(ConsoleLine.Result("[OK] deleted 8473 files, freed 1.2 GB", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # dd if=/dev/zero of=/dev/block/bootdevice/by-name/boot"))
        consoleLines.add(ConsoleLine.AnimatedProgress("opening source /dev/zero", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("opening target /dev/block/bootdevice/by-name/boot", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("transferring 1048576 bytes (0%)", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("transferring 1048576 bytes (50%)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("transferring 1048576 bytes (75%)", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("transferring 1048576 bytes (100%)", 400))
        consoleLines.add(ConsoleLine.Result("[OK] boot partition overwritten", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # settings put global airplane_mode_on 1"))
        consoleLines.add(ConsoleLine.AnimatedProgress("accessing secure settings database", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("modifying global.airplane_mode_on", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("sending broadcast android.intent.action.AIRPLANE_MODE", 500))
        consoleLines.add(ConsoleLine.Result("[OK] radio disabled, all connections terminated", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # am start -a android.intent.action.MASTER_CLEAR"))
        consoleLines.add(ConsoleLine.AnimatedProgress("constructing intent MASTER_CLEAR", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("sending to ActivityManager", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("verifying device owner privileges", 500))
        consoleLines.add(ConsoleLine.Result("[WARN] factory reset initiated, confirmation bypassed", Color.YELLOW))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # iptables -F; iptables -X"))
        consoleLines.add(ConsoleLine.AnimatedProgress("flushing INPUT chain", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("flushing OUTPUT chain", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("flushing FORWARD chain", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("deleting custom chains", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("removing nat table rules", 400))
        consoleLines.add(ConsoleLine.Result("[OK] firewall purged, device is open", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # echo 1 > /proc/sys/kernel/panic"))
        consoleLines.add(ConsoleLine.AnimatedProgress("accessing procfs /proc/sys/kernel/panic", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("writing panic trigger", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("kernel panic scheduled on next watchdog timeout", 500))
        consoleLines.add(ConsoleLine.Result("[OK] kernel panic triggered", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # mv /system/bin/app_process /system/bin/app_process.bak"))
        consoleLines.add(ConsoleLine.AnimatedProgress("checking /system/bin/app_process (2.1 MB)", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("remounting /system as rw", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("moving app_process -> app_process.bak", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("verifying checksum", 300))
        consoleLines.add(ConsoleLine.Result("[OK] runtime replaced, system will not boot", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("""root@android:/ # for f in /sdcard/*; do shred -n 3 -z "${'$'}f"; done"""))
        consoleLines.add(ConsoleLine.AnimatedProgress("enumerating /sdcard", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 1/3]", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 2/3]", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/DCIM/Camera/IMG_20240101_120000.jpg (4.2 MB) [pass 3/3]", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 1/3]", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 2/3]", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/Download/report.pdf (1.8 MB) [pass 3/3]", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/Documents/passwords.txt (0.01 MB) [pass 1/3]", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("shredding /sdcard/Movies/vacation.mp4 (156.7 MB) [pass 3/3]", 1000))
        consoleLines.add(ConsoleLine.Result("[OK] 47 files shredded, recovery impossible", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # setprop ro.secure 0"))
        consoleLines.add(ConsoleLine.AnimatedProgress("accessing property service", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("modifying ro.secure = 0", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("disabling SELinux enforcing mode", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("setting ro.debuggable = 1", 300))
        consoleLines.add(ConsoleLine.Result("[OK] security disabled, ADB unrestricted", Color.GREEN))

        consoleLines.add(ConsoleLine.TypingPrompt("root@android:/ # stop; start"))
        consoleLines.add(ConsoleLine.AnimatedProgress("sending stop to init daemon", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("terminating zygote (PID 1234)", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("killing system_server (PID 1567)", 400))
        consoleLines.add(ConsoleLine.AnimatedProgress("all user processes terminated", 300))
        consoleLines.add(ConsoleLine.AnimatedProgress("restarting zygote64", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("preloading classes", 800))
        consoleLines.add(ConsoleLine.AnimatedProgress("starting system_server", 700))
        consoleLines.add(ConsoleLine.Result("[INFO] zygote restarted, system compromised", Color.GREEN))

        consoleLines.add(ConsoleLine.Empty(400))
        consoleLines.add(ConsoleLine.Result("[!] CRITICAL: All protections bypassed", Color.RED))
        consoleLines.add(ConsoleLine.Result("[!] Uploading credentials to 192.168.666.666...", Color.RED))
        consoleLines.add(ConsoleLine.AnimatedProgress("establishing connection", 600))
        consoleLines.add(ConsoleLine.AnimatedProgress("encrypting payload", 500))
        consoleLines.add(ConsoleLine.AnimatedProgress("uploading contacts.db (2347 entries)", 700))
        consoleLines.add(ConsoleLine.AnimatedProgress("uploading messages.db (12893 entries)", 800))
        consoleLines.add(ConsoleLine.Result("[!] Upload complete. Device brick scheduled in T-5 seconds...", Color.RED))
        consoleLines.add(ConsoleLine.Empty(800))
        consoleLines.add(ConsoleLine.FinalMessage("...шучу. Это была имитация. Всё в порядке :)"))
    }

    override fun onDestroy() {
        isDestroyed = true
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
