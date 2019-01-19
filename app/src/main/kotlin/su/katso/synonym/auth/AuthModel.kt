package su.katso.synonym.auth

import su.katso.synonym.common.arch.MvcModel

data class AuthModel(
    var isLoading: Boolean = false,
    var isAddressError: Boolean = false,
    var addressText: String = "",
    var isAccountError: Boolean = false,
    var accountText: String = "",
    var isPasswordError: Boolean = false,
    var passwordText: String = ""
) : MvcModel {
    fun isInputValid() = addressText.isNotEmpty()
            && accountText.isNotEmpty()
            && passwordText.isNotEmpty()

    override fun toString(): String {
        return """
            |
            |AuthViewState{
            |   isLoading     $isLoading
            |   addressText   $addressText
            |   accountText   $accountText
            |   passwordText  $passwordText
            |}
        """.trimMargin()
    }
}