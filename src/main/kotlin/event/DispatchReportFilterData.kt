package event

import component.reportFilter.ReportFilterData
import tornadofx.EventBus
import tornadofx.FXEvent

class DispatchReportFilterData(val reportFilterData: ReportFilterData) : FXEvent(EventBus.RunOn.ApplicationThread)