package com.example.instagram

import android.content.Intent
import android.graphics.PorterDuff
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.instagram.databinding.FragmentMyProfileBinding
import com.example.instagram.model.ContentDTO
import com.example.instagram.model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(R.layout.fragment_my_profile) {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var imagesSnapshot: ListenerRegistration? = null
    var userID: String? = null
    var currentUserUid: String? = null


    var destinationUid: String? = null

    var followListenerRegistration: ListenerRegistration? = null
    var followingListenerRegistration: ListenerRegistration? = null

    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun initStartView() {
        super.initStartView()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth?.currentUser?.email
        currentUserUid = auth?.currentUser?.uid

        getProfileImage()

    }

    override fun initDataBinding() {
        super.initDataBinding()

        binding.accountSignout.setOnClickListener {
            (activity as MainActivity).hideNav()
            navController.navigate(R.id.action_myProfileFragment_to_signinFragment)
            auth?.signOut()
        }

//        getProfileImage()

        getFollowerAndFollowing()

        binding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        binding.accountRecyclerview.layoutManager = GridLayoutManager(context, 3)
//        binding.userName.text = userID
    }

    fun getFollowerAndFollowing() {
        firestore?.collection("users")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if (followDTO?.followingCount != null) {
                    binding.accountTvFollowingCount.text = followDTO.followingCount.toString()
                }
                if (followDTO?.followerCount != null) {
                    binding.accountTvFollowCount.text = followDTO.followerCount.toString()
                }
            }
    }


    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    var url = documentSnapshot.data!!["image"]
                    Glide.with(requireActivity())
                        .load(url)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.accountIvProfile)
                }
            }
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = ArrayList()

        init {
            imagesSnapshot = FirebaseFirestore
                .getInstance().collection("images").whereEqualTo("uid", currentUserUid)
                ?.addSnapshotListener { querySnapshot, firebaseFiresrore ->

                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
                    binding.accountTvPostCount.text = contentDTOs.size.toString()
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var imageView = (holder as CustomViewHolder).imageview

//            binding.accountIvProfile.setOnClickListener {
//                var photoPickerIntent = Intent(Intent.ACTION_PICK)
//                photoPickerIntent.type = "image/*"
//                activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
//            }

            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)

            binding.accountIvProfile.setOnClickListener {

                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)

//                val fragment = UserFragment()
//                val bundle = Bundle()
//
//                bundle.putString("destinationUid", contentDTOs[position].uid)
//                bundle.putString("userId", contentDTOs[position].userId)
//
//                fragment.arguments = bundle
//                activity!!.supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_content, fragment)
//                    .commit()
            }


        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview)
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }
}