package control

import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.util.Callback
import tornadofx.addColumnInternal
import tornadofx.observable
import kotlin.reflect.KProperty1


@Suppress("UNCHECKED_CAST")
inline fun <reified S, T> TableView<S>.nestedColumn(
    title: String,
    prop: KProperty1<S, T>,
    noinline op: TableView<S>.(TableColumn<S, T>) -> Unit = {}
): TableColumn<S, T> {
    val column = TableColumn<S, T>(title)
    column.cellValueFactory = Callback { observable(it.value, prop) }
    addColumnInternal(column)

    val previousColumnTarget = properties["tornadofx.columnTarget"] as? ObservableList<TableColumn<S, T>>
    properties["tornadofx.columnTarget"] = column.columns
    op(this, column)
    properties["tornadofx.columnTarget"] = previousColumnTarget
    return column
}