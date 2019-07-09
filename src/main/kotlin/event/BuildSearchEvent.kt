package event

import model.Report
import tornadofx.EventBus
import tornadofx.FXEvent

sealed class BuildSearchEvent {
    class ReportLoaded(val report: Report) : FXEvent(EventBus.RunOn.ApplicationThread)
    class ReportButtonClick: FXEvent(EventBus.RunOn.ApplicationThread)
}
