package view.buildSearchView

import tornadofx.CssRule
import tornadofx.Stylesheet
import tornadofx.cssclass

class BuildSearhStyle: Stylesheet() {

    companion object {

        val buildListView by cssclass()
    }

    init {
        buildListView {
            cell {
                and(CssRule)
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