package event

import tornadofx.EventBus
import tornadofx.FXEvent

class ReportDisplayed : FXEvent(EventBus.RunOn.ApplicationThread)