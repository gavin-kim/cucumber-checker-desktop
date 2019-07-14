package event

import tornadofx.EventBus
import tornadofx.FXEvent

class ShowReportOverlay : FXEvent(EventBus.RunOn.ApplicationThread)

