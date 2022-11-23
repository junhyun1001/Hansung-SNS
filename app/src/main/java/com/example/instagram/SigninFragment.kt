package com.example.instagram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagram.databinding.FragmentSigninBinding


class SigninFragment : BaseFragment<FragmentSigninBinding>(R.layout.fragment_signin) {

    override fun initStartView() {
        super.initStartView()

    }

    override fun initDataBinding() {
        super.initDataBinding()

        // 여기다가 binding
        binding.signinButton.setOnClickListener {
            val mainAct = activity as MainActivity
            navController.navigate(R.id.action_signinFragment_to_peedFragment) // 프래그먼트 바꾸는 방법
            mainAct.showBottomNav()
        }
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }


}
