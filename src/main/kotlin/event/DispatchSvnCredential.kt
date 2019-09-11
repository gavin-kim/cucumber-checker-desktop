package event

import tornadofx.EventBus
import tornadofx.FXEvent

class DispatchSvnCredential(val username: String, val password: String) : FXEvent(EventBus.RunOn.ApplicationThread)