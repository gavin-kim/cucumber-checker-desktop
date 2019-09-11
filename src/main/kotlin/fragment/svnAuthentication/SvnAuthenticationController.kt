package fragment.svnAuthentication

import event.DispatchSvnCredential
import javafx.event.ActionEvent
import javafx.event.EventHandler
import tornadofx.Controller
import tornadofx.Fragment
import tornadofx.getProperty
import tornadofx.property

class SvnAuthenticationController : Controller() {

    private var username: String by property()
    val usernameProperty = getProperty(SvnAuthenticationController::username)

    private var password: String by property()
    val passwordProperty = getProperty(SvnAuthenticationController::password)

    fun onClickSubmitButton(fragment: Fragment) = EventHandler<ActionEvent> {
        fire(DispatchSvnCredential(username, password))
        fragment.close()
    }

    fun onClickCancelButton() = EventHandler<ActionEvent> {

    }
}