package su.katso.synonym.common.arch

interface Command
class ToastCommand(val text: String) : Command
class HideKeyboardCommand : Command
