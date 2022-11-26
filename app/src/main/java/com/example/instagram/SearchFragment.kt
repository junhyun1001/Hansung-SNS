package com.example.instagram

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
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
    var imagesSnapshot  : ListenerRegistration? = null
    override fun initStartView() {
        super.initStartView()

    }

    override fun initDataBinding() {
        super.initDataBinding()

        binding.gridfragmentRecyclerview.adapter = GridFragmentRecyclerViewAdapter()
        binding.gridfragmentRecyclerview.layoutManager = GridLayoutManager(context, 3)

    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }

    inner class GridFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO>
        var uid: String? = null

        init {
            uid = FirebaseAuth.getInstance().currentUser?.uid

            contentDTOs = ArrayList()
            imagesSnapshot = FirebaseFirestore
                .getInstance().collection("images").orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
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

            Glide.with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)

            // 이미지 클릭하면 해당 유저의 프로필이 보임
            imageView.setOnClickListener {
                println("유저 페이지 들어가기")

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

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}