package org.ro.core.aggregator

import org.ro.core.event.LogEntry
import org.ro.core.model.DisplayObject
import org.ro.layout.Layout
import org.ro.to.HttpError
import org.ro.to.Property
import org.ro.to.ResultObject
import org.ro.to.TObject
import org.ro.ui.ErrorAlert
import org.ro.ui.kv.UiManager

class ObjectAggregator(val actionTitle: String) : BaseAggregator() {

    init {
        dsp = DisplayObject(actionTitle)
    }

    override fun update(logEntry: LogEntry, subType: String) {

        when (val obj = logEntry.getTransferObject()) {
            is TObject -> handleObject(obj)
            is ResultObject -> handleResultObject(obj)
            is Property -> handleProperty(obj)
            is Layout -> handleLayout(obj)
            is HttpError -> ErrorAlert(logEntry).open()
            else -> log(logEntry)
        }

        if (dsp.canBeDisplayed()) {
            UiManager.openObjectView(this)
        }
    }

    fun handleObject(obj: TObject) {
        dsp.addData(obj)
        obj.getLayoutLink()?.invokeWith(this)
    }

    fun handleResultObject(obj: ResultObject) {
        console.log("[OA.handleResultObject]")
        console.log(obj)
       // dsp.addData(obj)
    }

    override fun getObject(): TObject? {
        return dsp.getObject()
    }

    private fun handleProperty(property: Property) {
        console.log("[ObjectAggregator.handleProperty] yet to be implemented")
        console.log(property)
    }

    private fun handleLayout(layout: Layout) {
        (dsp as DisplayObject).addLayout(layout)
        //FIXME do analogous to DisplayList
 /*       layout.propertyDescriptionList.forEach {
            it.links.forEach { l ->
                //TODO correct link?
                l.invokeWith(this)
            }
        } */
    }

    override fun reset(): ObjectAggregator {
        dsp.isRendered = false
        return this
    }

}
