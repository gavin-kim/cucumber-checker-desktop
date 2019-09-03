package component.statusBar

import tornadofx.View
import tornadofx.box
import tornadofx.c

import tornadofx.fitToParentWidth
import tornadofx.hbox
import tornadofx.px
import tornadofx.style
import tornadofx.text
import tornadofx.usePrefHeight


class StatusBarView : View("Status View") {

    private val controller: StatusBarController by inject()

    override val root = hbox {
        prefHeight = 24.0
        usePrefHeight = true

        fitToParentWidth()

        text("test")

        style {
            borderColor += box(c("#C8C8C8"))
            backgroundColor += c("#E7E7E7")
            fontSize = 14.px
        }
    }
}