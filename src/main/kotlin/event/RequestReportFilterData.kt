package event

import tornadofx.EventBus
import tornadofx.FXEvent

class RequestReportFilterData : FXEvent(EventBus.RunOn.ApplicationThread)