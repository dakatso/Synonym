package su.katso.synonym.settings

import su.katso.synonym.common.arch.BaseController

class SettingsController : BaseController<SettingsView, SettingsModel>(SettingsModel()) {
    override fun onBind(view: SettingsView) {
    }
}