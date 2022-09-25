// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lollipop.color1500.ColorLoader
import com.lollipop.color1500.ColorPage

@Composable
@Preview
fun App() {
    MaterialTheme {
        ColorPage()
    }
}

fun main() = application {
    ColorLoader.load()
    Window(
        onCloseRequest = ::exitApplication,
        title = "都芳1500电子色卡"
    ) {
        App()
    }
}
