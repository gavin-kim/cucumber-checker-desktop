package event

import model.Report
import tornadofx.EventBus
import tornadofx.FXEvent

data class DispatchReportEvent(val report: Report) : FXEvent(EventBus.RunOn.ApplicationThread)