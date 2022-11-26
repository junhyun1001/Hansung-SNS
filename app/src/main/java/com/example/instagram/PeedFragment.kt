package com.example.instagram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instagram.databinding.FragmentPeedBinding
import com.example.instagram.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PeedFragment : BaseFragment<FragmentPeedBinding>(R.layout.fragment_peed) {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var user: FirebaseUser? = null

    override fun initStartView() {
        super.initStartView()
        (activity as MainActivity).showNav()

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        user = FirebaseAuth.getInstance().currentUser

        // 리사이클러 뷰를 사용하려면 두 가지가 필수적으로 필요하다. Adpater와 LayoutManager이다.
        binding.detailviewfragmentRecyclerview.adapter = PeedAdapter()
        binding.detailviewfragmentRecyclerview.layoutManager =
            LinearLayoutManager(context) // 스크롤을 위아래로 할지, 좌우로 할지를 결정하는 것
    }

    inner class PeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var firestore: FirebaseFirestore? = null
        var auth: FirebaseAuth? = null
        var uid: String? = null
        var user: FirebaseUser? = null
        var currentUserUid: String? = null

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore = FirebaseFirestore.getInstance()
            uid = FirebaseAuth.getInstance().currentUser?.uid
            user = FirebaseAuth.getInstance().currentUser
            currentUserUid = auth?.currentUser?.uid
            auth = FirebaseAuth.getInstance()


            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


            setFragmentResult(
                "destinationUid",
                bundleOf("uidList" to contentUidList[position])
            )
            setFragmentResult("userId", bundleOf("DTOs" to contentDTOs[position].uid))

            val viewHolder = (holder as CustomViewHolder).itemView

            // Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val url = task.result["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop())
                            .into(viewHolder.findViewById(R.id.detailviewitem_profile_image))
                    }
                }

            // 해당 게시물을 올린 유저 프로필로 이동
            viewHolder.findViewById<ImageView>(R.id.detailviewitem_profile_image)
                .setOnClickListener {
                    val clickUser = contentDTOs[position].uid
                    // 해당 게시물을 올린 유저가 자기 계정일 때
                    if (uid != null && clickUser == uid) {
                        setFragmentResult("userId", bundleOf("DTOs" to contentDTOs[position].uid))
                        findNavController().navigate(R.id.action_peedFragment_to_myProfileFragment)
                    } else {
                        setFragmentResult("userId", bundleOf("DTOs" to contentDTOs[position].uid))
                        findNavController().navigate(R.id.action_peedFragment_to_yourProfileFragment)
                    }
                }

            // 유저 아이디
            viewHolder.findViewById<TextView>(R.id.detailviewitem_profile_textview).text =
                contentDTOs[position].userId

            // 가운데 이미지
            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .into(viewHolder.findViewById(R.id.detailviewitem_imageview_content))

            // 설명 텍스트
            viewHolder.findViewById<TextView>(R.id.detailviewitem_explain_textview).text =
                contentDTOs[position].explain
            // 좋아요 이벤트
            viewHolder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
                .setOnClickListener { favoriteEvent(position) }

            //좋아요 버튼 설정
            if (contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                viewHolder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
                    .setImageResource(R.drawable.ic_favorite)

            } else {

                viewHolder.findViewById<ImageView>(R.id.detailviewitem_favorite_imageview)
                    .setImageResource(R.drawable.ic_favorite_border)
            }
            //좋아요 카운터 설정
            viewHolder.findViewById<TextView>(R.id.detailviewitem_favoritecounter_textview).text =
                "좋아요 ${contentDTOs[position].favoriteCount}개"

            // 댓글 눌렀을 때
            viewHolder.findViewById<ImageView>(R.id.detailviewitem_comment_imageview)
                .setOnClickListener {
                    setFragmentResult(
                        "destinationUid",
                        bundleOf("uidList" to contentUidList[position])
                    )
                    setFragmentResult("userId", bundleOf("DTOs" to contentDTOs[position].uid))
                    findNavController().navigate(R.id.action_peedFragment_to_commentFragment)
                }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        //좋아요 이벤트 기능
        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->

                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                // 좋아요 버튼 눌려있을 때
                if (contentDTO!!.favorites.containsKey(uid)) {
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)

                    // 좋아요 버튼 안눌려있을 때
                } else {
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid] = true
//                favoriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }
        }


    }

    override fun initDataBinding() {
        super.initDataBinding()
    }

    override fun initAfterBinding() {
        super.initAfterBinding()
    }
}