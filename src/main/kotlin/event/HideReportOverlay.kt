package event

import tornadofx.EventBus
import tornadofx.FXEvent

class HideReportOverlay : FXEvent(EventBus.RunOn.ApplicationThread)