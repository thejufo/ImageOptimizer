package com.taaxocm.io

import javafx.scene.control.Alert

inline fun alert(type: Alert.AlertType = Alert.AlertType.NONE, block: Alert.() -> Unit) =
        Alert(type).apply(block)

data class Book(
        var ISDN: String,
        var name: String,
        var pages: Int,
        var weight: Double,
        var year: Int,
        var author: String
)
