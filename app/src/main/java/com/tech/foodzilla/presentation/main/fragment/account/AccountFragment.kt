package com.tech.foodzilla.presentation.main.fragment.account

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tech.foodzilla.R
import com.tech.foodzilla.databinding.FragmentAccountBinding
import com.tech.foodzilla.model.UserInfoModel
import com.tech.foodzilla.presentation.authentication.UserInfoViewModel
import com.tech.foodzilla.util.DISPLAY_DIALOG
import com.tech.foodzilla.util.LOADING_ANNOTATION
import com.tech.foodzilla.util.Resource
import com.tech.foodzilla.util.extention.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private lateinit var binding: FragmentAccountBinding

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named(LOADING_ANNOTATION)
    lateinit var loadingDialog: Dialog

    @Inject
    @Named(DISPLAY_DIALOG)
    lateinit var displayAlert: AlertDialog

    private var userInfoModel: UserInfoModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {
        userInfoViewModel.userInformationLiveData.observe(viewLifecycleOwner) { userInfo ->
            when (userInfo) {
                is Resource.Success -> {
                    userInfoModel = userInfo.data
                    binding.userInfo = userInfoModel
                    loadingDialog.hide()
                }

                is Resource.Error -> {
                    loadingDialog.hide()
                    showToast(userInfo.msg!!)
                }

                is Resource.Loading -> loadingDialog.show()
            }
        }

        userInfoViewModel.userLocationLiveData.observe(viewLifecycleOwner) { newLocation ->
            if (newLocation != null) {
                userInfoViewModel.changUserLocation(newLocation)
                showAlertDialog(getString(R.string.myLocation), getString(R.string.locationChanged))
            }

        }
    }

    private fun showAlertDialog(title: String, message: String) {
        displayAlert.apply {
            setTitle(title)
            setMessage(message)
            show()
        }
    }

    fun openOrdersFragment() {
        val action = AccountFragmentDirections.actionAccountFragmentToAllOrdersFragment()
        findNavController().navigate(action)
    }

    fun openAccountSettings() {
        val action = AccountFragmentDirections.actionAccountFragmentToCreateUserInfoFragment(userInfoModel)
        findNavController().navigate(action)
    }

    fun openDeliveryAddressFragment() {
        val action = AccountFragmentDirections.actionAccountFragmentToLocateUserLocationFragment()
        findNavController().navigate(action)
    }

    fun openHelpFragment() {
        // TODO: Add help information.
    }

    fun openAboutFragment() {
        // TODO: Add about app information.
    }

    fun logout() {
        firebaseAuth.signOut()
        navigateToAuthFragment()
        showToast(getString(R.string.logOutMessage))
    }

    private fun navigateToAuthFragment() {
        val action = AccountFragmentDirections.actionAccountFragmentToAuthenticationFragment()
        findNavController().navigate(action)
    }
}