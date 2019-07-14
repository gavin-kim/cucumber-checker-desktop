package event

import model.Report
import tornadofx.EventBus
import tornadofx.FXEvent

class DisplayReport(val report: Report) : FXEvent(EventBus.RunOn.ApplicationThread)