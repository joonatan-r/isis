package org.ro.core.aggregator

import org.ro.core.Utils
import org.ro.core.event.LogEntry
import org.ro.core.event.RoXmlHttpRequest
import org.ro.to.Action
import org.ro.to.Link
import org.ro.to.Method
import org.ro.ui.Point
import org.ro.ui.kv.ActionPrompt

class ActionDispatcher(private val at: Point = Point(100,100)) : BaseAggregator() {

    override fun update(logEntry: LogEntry) {
        val action = logEntry.getTransferObject() as Action
        action.links.forEach {
            // it.rel should be neither: (self | up | describedBy )
            if (it.isInvokeAction()) {
                when (it.method) {
                    Method.GET.name -> processGet(action, it)
                    Method.POST.name -> processPost(action, it)
                    Method.PUT.name -> processPut(it)
                }
            }
        }
    }

    /**
     *  link.rel should neither be: (self | up | describedBy )
     */
    private fun Link.isInvokeAction(): Boolean {
        return rel.contains("invoke") && rel.contains("action")
    }

    private fun processGet(action: Action, link: Link) {
        if (link.hasArguments()) {
            ActionPrompt(action).open(at)
        } else {
            invoke(link)
        }
    }

    private fun processPost(action: Action, link: Link) {
        console.log("[ActionDispatcher.processPost] link.hasArguments: ${link.hasArguments()}")
        console.log(link)
        val title = Utils.deCamel(action.id)
        if (link.hasArguments()) {
            ActionPrompt(action).open(at)
        } else {
            RoXmlHttpRequest().invoke(link, ObjectAggregator(title))
        }
    }

    private fun processPut(link: Link) {
        invoke(link)
    }

}
