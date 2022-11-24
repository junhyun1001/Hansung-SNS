package com.example.instagram

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import com.example.instargram.navigation.model.ContentDTO
import com.example.instagram.databinding.FragmentAlarmBinding
import com.example.instagram.databinding.FragmentMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagram.model.ContentDTO

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(R.layout.fragment_my_profile) {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUID: String? = null
    override fun initStartView() {
        super.initStartView()
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUID = auth?.currentUser?.uid

        binding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        binding.accountRecyclerview.layoutManager = LinearLayoutManager(context)
//        binding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
//        binding.accountRecyclerview.layoutManager = GridLayoutManager(context, 3)

        binding.accountSignout.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_signinFragment)
            auth?.signOut()
        }
        getProfileImage()

    }

    override fun initDataBinding() {
        super.initDataBinding()

        // 여기다가 binding
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }

    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(currentUserUID!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null)
                    return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot?.data!!["image"]
                    Glide.with(requireActivity())
                        .load(url)
                        .circleCrop()
                        .into(binding.accountIvProfile)

                }
            }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestore ->

                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    binding.accountTvPostCount.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview


            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .centerCrop()//이거 고쳐야함!
//              .apply(new RequestOptions().centerCrop()) 원래 코드
                .into(imageview); }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
}