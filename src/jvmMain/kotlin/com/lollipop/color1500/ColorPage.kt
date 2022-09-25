package com.lollipop.color1500

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
@Preview
fun ColorPage() {
    val maxSpanCount = ColorLoader.maxSpanCount
    val colWeight = 1F / maxSpanCount

    var searchInput by remember { mutableStateOf("") }
    var searchValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8F).align(Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier.align(Alignment.CenterVertically).fillMaxWidth()
            ) {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(10.dp),
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),//自定义回车为搜索操作
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchValue = searchInput
                        }
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color(0xFF262626),
                        fontSize = 14.sp,
                    )
                )
                if (searchInput.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center).padding(10.dp),
                        text = "输入色号或名称",
                        color = Color(0xFF262626),
                        fontSize = 16.sp,
                    )
                }
                Image(
                    Icons.Default.Search,
                    "search",
                    modifier = Modifier.align(Alignment.CenterEnd).clickable {
                        searchValue = searchInput
                    }
                )
                Divider(
                    modifier = Modifier.align(Alignment.BottomEnd).fillMaxWidth().height(1.dp),
                    color = Color(0xFF333333),
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(0.97F)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState())
            ) {

                ColorLoader.pages.forEach { page ->
                    Column {
                        Text(
                            text = page.pageName,
                            fontSize = 18.sp,
                        )
                        Row(
                            Modifier.fillMaxWidth()
                        ) {
                            val rows = page.list
                            for (index in 0 until maxSpanCount) {
                                val row = if (rows.size > index) {
                                    rows[index].list
                                } else {
                                    null
                                }
                                Column(
                                    modifier = Modifier.weight(colWeight)
                                ) {
                                    row?.forEach { info ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth()
                                                .aspectRatio(1.63f)
                                        ) {
                                            if (contains(searchValue, info.colorValue, info.name, info.code)) {
                                                colorCard(info)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun BoxScope.colorCard(info: ColorInfo) {

    val textColor = if (info.red > 180 || info.green > 180 || info.blue > 180) {
        Color.Black
    } else {
        Color.White
    }

    Box(
        modifier = Modifier.background(Color(info.color))
            .fillMaxHeight(0.9F)
            .fillMaxWidth(0.9F)
            .align(Alignment.Center)
            .clickable {
                save(info.colorValue)
            }
    ) {}

    Box(
        modifier = Modifier.fillMaxHeight(0.8F)
            .fillMaxWidth(0.8F)
            .align(Alignment.Center)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = "${info.page}",
            color = textColor,
            fontSize = 8.sp,
            maxLines = 1
        )
        Text(
            modifier = Modifier.align(Alignment.TopEnd),
            text = "${info.index}",
            color = textColor,
            fontSize = 8.sp,
            maxLines = 1
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomStart)
                .clickable {
                    save("${info.code} ${info.name}")
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = "${info.code}",
                color = textColor,
                fontSize = 8.sp,
                maxLines = 1
            )
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "${info.name}",
                color = textColor,
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}

private fun contains(keyword: String, vararg targets: String): Boolean {
    for (target in targets) {
        if (target.contains(keyword, true)) {
            return true
        }
    }
    return false
}

private fun save(value: String) {
    // 获取系统剪贴板
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    // 封装文本内容
    val trans = StringSelection(value);
    // 把文本内容设置到系统剪贴板
    clipboard.setContents(trans, null);
}
