package com.example.instagram

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.databinding.FragmentYourProfileBinding
import com.example.instagram.model.ContentDTO
import com.example.instargram.navigation.model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class YourProfileFragment :
    BaseFragment<FragmentYourProfileBinding>(R.layout.fragment_your_profile) {
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    var uid: String? = null
    var currentUserUid: String? = null

    override fun initStartView() {
        super.initStartView()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserUid = auth?.currentUser?.uid


        var flag = true
        binding.accountBtnFollow.setOnClickListener {
            if (flag) {
                binding.accountBtnFollow.text = "Unfollow"
//                myRequestFollow()
                flag = false
            } else if (!flag) {
                binding.accountBtnFollow.text = "Follow"
//                yourRequestFollow()
                flag = true
            }
        }

        binding.accountRecyclerview.adapter = UserFragmentRecyclerViewAdapter()
        binding.accountRecyclerview.layoutManager = GridLayoutManager(context, 3)
    }

    // 내가 팔로우를 걸었을 때
    fun myRequestFollow() {
        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }
            // Unstar the post and remove self from stars
            if (followDTO.followings.containsKey(uid)) {

                followDTO.followerCount = followDTO.followerCount - 1
                followDTO.followings.remove(uid)
            } else {

                followDTO.followerCount = followDTO.followerCount + 1
                followDTO.followings[uid!!] = true
//                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }
    }

    // 상대방이 나를 팔로우 했을 때
    fun yourRequestFollow() {
        var tsDocFollower = firestore!!.collection("users").document(uid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followerCount = 1
                followDTO.followers[currentUserUid!!] = true


                transaction.set(tsDocFollower, followDTO)
                return@runTransaction
            }

            if (followDTO.followers.containsKey(currentUserUid!!)) {


                followDTO.followerCount = followDTO.followerCount - 1
                followDTO.followers.remove(currentUserUid!!)
            } else {

                followDTO.followerCount = followDTO.followerCount + 1
                followDTO.followers[currentUserUid!!] = true

            }// Star the post and add self to stars

            transaction.set(tsDocFollower, followDTO)
            return@runTransaction
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)

        }

        inner class CustomViewHolder(var imageview: ImageView) :
            RecyclerView.ViewHolder(imageview)


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview


            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .centerCrop()//이거 고쳐야함!
//              .apply(new RequestOptions().centerCrop()) 원래 코드
                .into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }


    override fun initDataBinding() {
        super.initDataBinding()
    }

    override fun initAfterBinding() {
        super.initAfterBinding()
    }

}