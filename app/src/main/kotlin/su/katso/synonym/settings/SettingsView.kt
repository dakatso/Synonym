package su.katso.synonym.settings

import android.os.Bundle
import android.view.View
import su.katso.synonym.R
import su.katso.synonym.common.arch.BaseView
import su.katso.synonym.common.arch.Command

class SettingsView(args: Bundle = Bundle.EMPTY) : BaseView(args), SettingsContract.View {
    override val content: Int = R.layout.settings_view
    override val controller = SettingsController(this)

    override fun View.initView() {
    }

    override fun render(model: SettingsModel) {
    }

    override fun react(command: Command) {
    }
}



