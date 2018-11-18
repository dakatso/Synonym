package su.katso.synonym.auth

import su.katso.synonym.common.arch.PresentationModel.ViewState

data class AuthViewState(
    var isLoading: Boolean = false,
    var isAddressError: Boolean = false,
    var isAccountsError: Boolean = false,
    var isPasswordError: Boolean = false
) : ViewState