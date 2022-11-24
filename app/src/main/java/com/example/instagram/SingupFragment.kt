package com.example.instagram

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.instagram.databinding.FragmentSingupBinding
import com.google.firebase.auth.FirebaseAuth

class SingupFragment : BaseFragment<FragmentSingupBinding>(R.layout.fragment_singup) {
//    private val mainAct = activity as MainActivity

    var auth: FirebaseAuth? = null
    override fun initStartView() {
        super.initStartView()
        auth = FirebaseAuth.getInstance()
    }

    override fun initDataBinding() {
        super.initDataBinding()
        // 여기다가 binding
        binding.signupBtn.setOnClickListener {
            createAccount()
        }

    }

    private fun createAccount() {

        if (binding.emailText.text.toString() == "" && binding.passwordText.text.toString() == "") {
            Toast.makeText(context, "아이디랑 비밀번호를 입력해 주세요!", Toast.LENGTH_LONG).show()
        } else {

            auth?.createUserWithEmailAndPassword(
                binding.emailText.text.toString(),
                binding.passwordText.text.toString()
            )
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "계정이 생성 되었어요!", Toast.LENGTH_LONG).show()
                        navController.navigate(R.id.action_siginupFragment_to_signinFragment)
                    } else
                        Toast.makeText(context, task.exception?.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }
}