package com.example.instagram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth


class SigninFragment : BaseFragment<FragmentSigninBinding>(R.layout.fragment_signin) {
    var auth: FirebaseAuth? = null
    override fun initStartView() {
        super.initStartView()

        // 이것도 나중에 지울것
        auth = FirebaseAuth.getInstance()

    }

    // 이거 나중에 지워야댐
    private fun signIn() {
        auth?.signInWithEmailAndPassword("test@test.com", "123123")?.addOnCompleteListener{}
    }

    override fun initDataBinding() {
        super.initDataBinding()

        // 여기다가 binding
        binding.signinButton.setOnClickListener {
            signIn()
            println("이거 맞냐?: " + auth?.currentUser)
            val mainAct = activity as MainActivity
            navController.navigate(R.id.action_signinFragment_to_peedFragment) // 프래그먼트 바꾸는 방법
            mainAct.showBottomNav()
        }
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }


}
