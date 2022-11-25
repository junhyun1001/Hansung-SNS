package com.example.instagram

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.instagram.databinding.FragmentCommentBinding
import com.example.instagram.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class CommentFragment : BaseFragment<FragmentCommentBinding>(R.layout.fragment_comment) {
    var contentUid: String? = null
    var destinationUid: String? = null
    var commentSnapshot: ListenerRegistration? = null

    override fun initStartView() {
        super.initStartView()
    }

    override fun initDataBinding() {
        super.initDataBinding()

        binding.commentBtnSend.setOnClickListener {
            val comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser!!.email
            comment.comment = binding.commentEditMessage.text.toString()
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            comment.timestamp = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection("images")
                .document(destinationUid!!)
                .collection("comments")
                .document()
                .set(comment)

            binding.commentEditMessage.text = null
        }

        binding.commentRecyclerview.adapter = CommentRecyclerViewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(context)

    }

    inner class CommentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val comments: ArrayList<ContentDTO.Comment> = ArrayList()

        init {
            setFragmentResultListener("userId") { _, bundle ->
                contentUid = bundle.getString("DTOs")

                setFragmentResultListener("destinationUid") { _, bundle ->
                    destinationUid = bundle.getString("uidList")


                    commentSnapshot = FirebaseFirestore
                        .getInstance()
                        .collection("images")
                        .document(destinationUid!!)
                        .collection("comments").orderBy("timestamp", Query.Direction.ASCENDING)
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            comments.clear()
                            if (querySnapshot == null) return@addSnapshotListener
                            for (snapshot in querySnapshot.documents) {
                                comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                            }
                            notifyDataSetChanged()
                        }
                }
            }

        }

        private inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var view = holder.itemView

            // Profile Image
            FirebaseFirestore.getInstance()
                .collection("profileImages")
                .document(comments[position].uid!!)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot?.data != null) {

                        val url = documentSnapshot?.data!!["image"]
                        Glide.with(holder.itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop())
                            .into(view.findViewById(R.id.commentviewitem_imageview_profile))
                    }
                }

            view.findViewById<TextView>(R.id.commentviewitem_textview_profile).text =
                comments[position].userId
            view.findViewById<TextView>(R.id.commentviewitem_textview_comment).text =
                comments[position].comment
        }

        override fun getItemCount(): Int {

            return comments.size
        }

    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }
}