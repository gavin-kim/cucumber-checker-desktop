package component.buildSearch

import tornadofx.Stylesheet
import tornadofx.cssclass

class BuildSearhStyle: Stylesheet() {

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