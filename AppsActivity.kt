package com.mishaal.camrylauncher

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class AppsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.insetsController?.hide(WindowInsets.Type.systemBars())

        val pm = packageManager
        val apps: List<ResolveInfo> = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
        ).filter { it.activityInfo.packageName != packageName }
            .sortedBy { it.loadLabel(pm).toString() }

        val grid = GridView(this).apply {
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            numColumns = 5
            verticalSpacing = 16
            horizontalSpacing = 16
            setPadding(32, 32, 32, 32)
            setBackgroundResource(R.drawable.bg_main)
            adapter = object : BaseAdapter() {
                override fun getCount() = apps.size
                override fun getItem(p: Int) = apps[p]
                override fun getItemId(p: Int) = p.toLong()
                override fun getView(p: Int, v: View?, parent: ViewGroup): View {
                    val ri = apps[p]
                    return LinearLayout(this@AppsActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        setPadding(8, 24, 8, 24)
                        setBackgroundResource(R.drawable.tile_bg)
                        addView(ImageView(context).apply {
                            setImageDrawable(ri.loadIcon(pm))
                            layoutParams = LinearLayout.LayoutParams(96, 96)
                        })
                        addView(TextView(context).apply {
                            text = ri.loadLabel(pm)
                            setTextColor(Color.WHITE)
                            textSize = 18f
                            gravity = Gravity.CENTER
                            maxLines = 1
                        })
                        setOnClickListener {
                            pm.getLaunchIntentForPackage(ri.activityInfo.packageName)
                                ?.let { startActivity(it) }
                        }
                    }
                }
            }
        }
        setContentView(grid)
    }
}
