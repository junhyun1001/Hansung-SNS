package com.example.instagram

import android.widget.Toast
import com.example.instagram.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth

class SigninFragment : BaseFragment<FragmentSigninBinding>(R.layout.fragment_signin) {

    var auth: FirebaseAuth? = null

    override fun initStartView() {
        super.initStartView()
        auth = FirebaseAuth.getInstance()

    }

    override fun initDataBinding() {
        super.initDataBinding()

        // 여기다가 binding
        binding.signinButton.setOnClickListener {
            // 유저 정보가 있으면 로그인 후 peed fragment로 변경
            if (binding.textEmail.text.toString() == "" && binding.passwdEditText.text.toString() == "") {
                Toast.makeText(context, "아이디 또는 비밀번호를 입력해 주세요!", Toast.LENGTH_LONG).show()
            } else {
                signIn()
            }
        }

        binding.signupTextView.setOnClickListener {
            navController.navigate(R.id.action_signinFragment_to_siginupFragment) // 프래그먼트 바꾸는 방법
        }

    }



    private fun signIn() {
        auth?.signInWithEmailAndPassword(
            binding.textEmail.text.toString(),
            binding.passwdEditText.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(R.id.action_signinFragment_to_peedFragment)
                } else {
                    Toast.makeText(context, "회원 정보가 맞지 않아요!", Toast.LENGTH_LONG).show()
                }
            }
        // [END sign_in_with_email]
    }


    override fun initAfterBinding() {
        super.initAfterBinding()

    }


}
