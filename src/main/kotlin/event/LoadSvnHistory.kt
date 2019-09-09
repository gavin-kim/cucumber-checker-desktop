package event

import model.cucumber.Build
import tornadofx.EventBus
import tornadofx.FXEvent

class LoadSvnHistory(val build: Build) : FXEvent(EventBus.RunOn.ApplicationThread)