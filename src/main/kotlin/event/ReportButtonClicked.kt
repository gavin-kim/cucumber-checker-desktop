package event

import tornadofx.EventBus
import tornadofx.FXEvent

class ReportButtonClicked: FXEvent(EventBus.RunOn.ApplicationThread)

