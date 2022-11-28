package com.example.instagram

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instagram.databinding.FragmentSearchBinding
import com.example.instagram.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var currentUserUid: String? = null
    var uid: String? = null
    var destinationUid: String? = null


    var followings: ArrayList<String> = arrayListOf()

    var imagesSnapshot: ListenerRegistration? = null

    override fun initStartView() {
        super.initStartView()
        currentUserUid = auth?.currentUser?.uid
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).showNav()

        getFollowing()


    }


    override fun initDataBinding() {
        super.initDataBinding()

        // 내가 팔로잉 하고 있는 유저 검색 가능
//        var followers: MutableMap<String, Boolean> = HashMap() // 중복 팔로우 방지
//        var users = arrayOf("가","나","다","라","마","바","사")
        var adapter =
            context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_dropdown_item_1line,
                    followings
                )
            }
//        binding.autoCompleteTextview.setAdapter(adapter)
//        binding.autoCompleteTextview.setOnItemClickListener { adapterView, view, position, rowId ->
//            println(
//                "position: $position, rowId: $rowId, string: ${
//                    adapterView.getItemAtPosition(
//                        position
//                    )
//                }"
//            )
//        }


        binding.gridfragmentRecyclerview.adapter = GridFragmentRecyclerViewAdapter()
        binding.gridfragmentRecyclerview.layoutManager = GridLayoutManager(context, 3)


    }

    fun getFollowing() {
//        firestore?.collection("users")?.document(currentUserUid!!)
//            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
//                if (documentSnapshot == null) {
//                    return@addSnapshotListener
//                }
//                println("******************안에 진입")
//                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
//                println("$$$$$$$$$$$$$$$$$$$$$$$ followings${followDTO?.followings}")
//                if (followDTO?.followings != null) {
//                    println("!!!!!!!!!!!!!!!!!!!!!!!getFollowing")
//                    followDTO.followings.forEach { key ->
//                        println("###################  key: $key")
//                    }
//                }
//            }
        setFragmentResultListener("destinationUid") { _, bundle ->
            destinationUid = bundle.getString("DTOsUid")
            uid = destinationUid
            val data = firestore?.collection("users")?.document("test@test.com")
            data?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot != null) {
                    val x = documentSnapshot.data.toString()
//                    binding.textView.text = x
                }
            }
        }

    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }

    inner class GridFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO>
        var uid: String? = null

        // 현재 접속된 계정의 uid와 이미지 좋아요의 계정중에서 같은것만 보여주기
        init {
            uid = FirebaseAuth.getInstance().currentUser?.uid

//            contentDTOs = ArrayList()
//            var x =
//                firestore?.collection("images")?.document(uid.toString())?.collection("favorites")
////            println(x)
//                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    contentDTOs.clear()
//
//                    for(snapshot in querySnapshot!!.documents) {
//                        followings.add(snapshot.toString())
//                        var item = snapshot.toObject(ContentDTO::class.java)
//                        contentDTOs.add(item!!)
//                    }
//                    notifyDataSetChanged()
//                }

            contentDTOs = ArrayList()
            imagesSnapshot = FirebaseFirestore
                .getInstance().collection("images").orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
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

            var imageView = (holder as CustomViewHolder).imageView

//            println("#######################################${uid.toString()}") // ydj0siWGuVcoM7TCkiovDXds5go2

            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)

            // 이미지 클릭하면 해당 유저의 프로필이 보임
            imageView.setOnClickListener {
                val clickUser = contentDTOs[position].uid
                // 해당 게시물을 올린 유저가 자기 계정일 때
                if (uid != null && clickUser == uid) {
                    findNavController().navigate(R.id.action_searchFragment_to_myProfileFragment)
                } else {
                    setFragmentResult(
                        "destinationUid",
                        bundleOf("DTOsUid" to contentDTOs[position].uid)
                    )
                    setFragmentResult(
                        "userId",
                        bundleOf("DTOsUserId" to contentDTOs[position].userId)
                    )
                    findNavController().navigate(R.id.action_searchFragment_to_yourProfileFragment)
                }


            }


        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}