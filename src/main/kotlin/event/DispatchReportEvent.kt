package event

import model.CucumberReport
import tornadofx.EventBus
import tornadofx.FXEvent

data class DispatchReportEvent(val report: CucumberReport) : FXEvent(EventBus.RunOn.ApplicationThread)