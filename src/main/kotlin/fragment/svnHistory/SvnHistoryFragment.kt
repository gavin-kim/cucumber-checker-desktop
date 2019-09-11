package fragment.svnHistory

import model.cucumber.Build
import model.svn.SvnLog
import tornadofx.Fragment
import tornadofx.SmartResize
import tornadofx.bindSelected
import tornadofx.fitToParentHeight
import tornadofx.fitToParentSize
import tornadofx.label
import tornadofx.multiSelect
import tornadofx.readonlyColumn
import tornadofx.tableview
import tornadofx.textfield
import tornadofx.vbox

class SvnHistoryFragment : Fragment("SVN History") {

    val controller: SvnHistoryController by inject()

    val build: Build by param()

    override val root = vbox(2) {
        label("Build Revision")

        textfield(controller.buildRevisionProperty) {
            isEditable = false
        }

        tableview(controller.svnLogListProperty) {
            fitToParentHeight()
            multiSelect(false)
            bindSelected(property = controller.selectedSvnLogProperty)

            readonlyColumn("Revision", SvnLog::revision)
            readonlyColumn("Author", SvnLog::author)
            readonlyColumn("Date", SvnLog::date)
            readonlyColumn("Message", SvnLog::message)

            columnResizePolicy = SmartResize.POLICY
        }

        fitToParentSize()
    }

    override fun onBeforeShow() {
        controller.buildProperty.set(build)
    }
}