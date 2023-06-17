package com.mawumbo.mystoryapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mawumbo.mystoryapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("email_request") { _, bundle ->
            val email = bundle.getString("email")
            binding.edtEmail.setText(email)
        }

        setFragmentResultListener("password_request") { _, bundle ->
            val password = bundle.getString("password")
            binding.edtPassword.setText(password)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.edtPassword.isError.observe(viewLifecycleOwner) {
            binding.btnLogin.isEnabled = it
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        val uiState = viewModel.uiState
        lifecycleScope.launch {
            uiState.collect {
                if (it.navigateToHome == true) {
                    val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    findNavController().navigate(action)
                    viewModel.navigatedToHome()
                }
                it.error?.let { error ->
                    Snackbar.make(
                        requireView(),
                        error,
                        Snackbar.LENGTH_SHORT
                    ).addCallback(
                        object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                viewModel.errorShown()
                            }
                        }
                    ).show()
                }

            }
        }

    }

}