package event

import model.Report
import tornadofx.EventBus
import tornadofx.FXEvent

class ReportLoaded(val report: Report) : FXEvent(EventBus.RunOn.ApplicationThread)