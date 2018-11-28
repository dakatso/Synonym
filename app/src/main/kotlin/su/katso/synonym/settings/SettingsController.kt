package su.katso.synonym.settings

import android.os.Bundle
import android.view.View
import su.katso.synonym.R
import su.katso.synonym.common.arch.BaseController
import su.katso.synonym.common.arch.PresentationModel

class SettingsController(args: Bundle = Bundle.EMPTY) : BaseController(args), SettingsViewController {
    override val content: Int = R.layout.settings_controller
    override val presentationModel = SettingsPresentationModel()
        .also { it.bindToLifecycle(this) }

    override fun View.initView() {
    }

    override fun render(viewState: SettingsViewState) {
    }

    override fun react(command: PresentationModel.Command) {
    }
}



