package su.katso.synonym.settings

import su.katso.synonym.common.arch.BaseController

class SettingsController(view: SettingsView) : BaseController<SettingsView, SettingsModel>(view, SettingsModel()) {
    override fun onBind(view: SettingsView) {
    }
}