package model.property

import javafx.beans.property.SimpleBooleanProperty

class ToggleProperty: SimpleBooleanProperty() {
    fun toggle() {
        value = !value
    }
}