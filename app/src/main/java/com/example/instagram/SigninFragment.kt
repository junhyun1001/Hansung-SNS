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
    }


    override fun initAfterBinding() {
        super.initAfterBinding()

    }

    override fun onDestroy() {
        super.onDestroy()

        val mainAct = activity as MainActivity
        mainAct.showBottomNav()
    }

}
