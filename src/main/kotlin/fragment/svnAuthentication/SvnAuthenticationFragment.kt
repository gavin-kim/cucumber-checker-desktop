package fragment.svnAuthentication

import javafx.geometry.Orientation
import tornadofx.Fragment
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.passwordfield
import tornadofx.required
import tornadofx.textfield
import tornadofx.vbox

class SvnAuthenticationFragment : Fragment("SVN Authentication") {

    private val controller: SvnAuthenticationController by inject()

    override val root = vbox(2) {
        form {
            fieldset(labelPosition = Orientation.VERTICAL) {
                field("Username") {
                    textfield(controller.usernameProperty) {
                        required()
                    }
                }

                field("Password") {
                    passwordfield(controller.passwordProperty) {
                        required()
                    }
                }
            }
        }

        button("Submit") {
            onAction = controller.onClickSubmitButton(this@SvnAuthenticationFragment)
        }
    }
}