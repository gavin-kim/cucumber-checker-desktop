package component.buildSearch

import tornadofx.Stylesheet
import tornadofx.cssclass

class BuildSearchStyle: Stylesheet() {

    companion object {

        val buildListView by cssclass()
    }

    init {
        buildListView {
            cell {
                and(".broken") {

                }

                and(".stable") {

                }

                and(".unstable") {

                }

                and("aborted") {

                }
            }
        }
    }
}