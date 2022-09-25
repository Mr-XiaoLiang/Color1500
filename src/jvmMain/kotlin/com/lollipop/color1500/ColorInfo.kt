package com.lollipop.color1500

import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

data class ColorInfo(
    val page: Int,
    val index: Int,
    val code: String,
    val name: String,
    val codeOriginal: String,
    val group: String,
    val red: Int,
    val green: Int,
    val blue: Int
) {

    val color: Int by lazy {
        val a = 0xFF.shl(24)
        val r = red.and(0xFF).shl(16)
        val g = green.and(0xFF).shl(8)
        val b = blue.and(0xFF)
        a.or(r).or(g).or(b)
    }

    val colorValue: String by lazy {
        Integer.toHexString(color.and(0xFFFFFF))
    }

    val hsv: FloatArray by lazy {
        ColorUtils.rgbToHSV(red, green, blue)
    }

}

object ColorLoader {

    val colorList = ArrayList<ColorInfo>()

    val pageMenu = listOf(
        "白色系" to 1..15,
        "红色系" to 16..33,
        "橙色系" to 34..50,
        "黄色系" to 51..67,
        "绿色系" to 68..86,
        "蓝色系-深" to 87..101,
        "蓝色系-浅" to 102..108,
        "紫色系" to 109..122,
        "中性色-偏红" to 123..134,
        "中性色-偏黄" to 135..150,
        "中性色-偏绿" to 151..164,
        "中性色-偏蓝" to 165..178,
        "深色系" to 179..188,
    )

    var maxSpanCount = 20
        private set

    // { page, { row, col } }
    val pages = ArrayList<ColorPage>()

    class ColorPage(
        val pageName: String,
        val pageNumber: Int,
        val list: ArrayList<ColorColumn>,
    )

    class ColorColumn(
        val columnId: Int,
        val list: ArrayList<ColorInfo>,
    )

    fun load() {
        if (pages.isNotEmpty()) {
            return
        }
        val resourcePath = "src.txt"
        val thisClass = this::class.java
        val input = thisClass.classLoader.getResourceAsStream(resourcePath)
            ?: thisClass.getResourceAsStream(resourcePath) ?: return
        val output = ByteArrayOutputStream()
        val outBuffer = BufferedOutputStream(output)
        val buffer = ByteArray(4 * 1024)
        do {
            val read = input.read(buffer)
            if (read < 0) {
                break
            }
            outBuffer.write(buffer, 0, read)
        } while (true)
        outBuffer.flush()
        val string = output.toString(StandardCharsets.UTF_8)
        input.close()
        output.close()
        val lines = string.split(";")
        for (line in lines) {
            val col = line.split(",")
            if (col.size < 9) {
                continue
            }
            colorList.add(
                ColorInfo(
                    page = col[0].intValue(),
                    index = col[1].intValue(),
                    code = col[2],
                    name = col[3],
                    codeOriginal = col[4],
                    group = col[5],
                    red = col[6].intValue(),
                    green = col[7].intValue(),
                    blue = col[8].intValue()
                )
            )
        }

        val tempPageMap = HashMap<String, HashMap<Int, ArrayList<ColorInfo>>>()

        for (info in colorList) {
            val menu = pageMenu.find { it.second.contains(info.page) } ?: continue
            val page = tempPageMap[menu.first] ?: HashMap()
            val col = page[info.page] ?: ArrayList()
            col.add(info)
            page[info.page] = col
            tempPageMap[menu.first] = page
        }
        tempPageMap.forEach { page ->
            val columnList = ArrayList<ColorColumn>()
            var pageNumber = Int.MAX_VALUE
            page.value.forEach { column ->
                val rowList = ArrayList<ColorInfo>()
                rowList.addAll(column.value)
                rowList.sortBy { it.index }
                columnList.add(ColorColumn(column.key, rowList))
                if (pageNumber > column.key) {
                    pageNumber = column.key
                }
            }
            columnList.sortBy { it.columnId }
            pages.add(ColorPage(page.key, pageNumber, columnList))
        }
        pages.sortBy { it.pageNumber }

        maxSpanCount = 0
        pages.forEach { page ->
            val size = page.list.size
            if (maxSpanCount < size) {
                maxSpanCount = size
            }
        }
    }

    private fun String.intValue(): Int {
        try {
            if (this.isEmpty()) {
                return 0
            }
            return this.toInt()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return 0
    }


}

object ColorUtils {
    fun rgbToHSV(red: Int, green: Int, blue: Int): FloatArray {
        val r = red / 255.0
        val g = green / 255.0
        val b = blue / 255.0

        // h, s, v = hue, saturation, value
        val cmax = Math.max(r, Math.max(g, b)) // maximum of r, g, b
        val cmin = Math.min(r, Math.min(g, b)) // minimum of r, g, b
        val diff = cmax - cmin // diff of cmax and cmin.
        var h = -1.0
        var s = -1.0

        // if cmax and cmax are equal then h = 0
        when (cmax) {
            cmin -> h = 0.0
            r -> h = (60 * ((g - b) / diff) + 360) % 360
            g -> h = (60 * ((b - r) / diff) + 120) % 360
            b -> h = (60 * ((r - g) / diff) + 240) % 360
        }

        // if cmax equal zero
        s = if (cmax == 0.0) {
            0.0
        } else {
            diff / cmax * 100
        }
        // compute v
        val v = cmax * 100
        return floatArrayOf(h.toFloat(), s.toFloat(), v.toFloat())
    }
}