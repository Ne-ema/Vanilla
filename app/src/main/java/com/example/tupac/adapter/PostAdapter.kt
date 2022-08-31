package com.example.tupac.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tupac.model.Post
import com.example.tupac.model.User
import com.example.tupac.ui.HomeFragment
import com.example.tupac.utils.PostsDiffUtil
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView


interface cardClick{
    fun detailView(currentPost : Post)
    fun deleteClick(currentPost: Post)
    fun editClick(currentPost: Post)
}

class PostAdapter(private val listener: HomeFragment) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var post = emptyList<Post>()
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePic: CircleImageView = itemView.findViewById(com.example.tupac.R.id.ivProfilePic)
        val username: TextView = itemView.findViewById(com.example.tupac.R.id.username)
        val minutesAgo: TextView = itemView.findViewById(com.example.tupac.R.id.minutesAgo)
        val profileTitle: TextView = itemView.findViewById(com.example.tupac.R.id.postTitle)
        val postDesc: TextView = itemView.findViewById(com.example.tupac.R.id.postDescription)
        val postCard : MaterialCardView = itemView.findViewById(com.example.tupac.R.id.postCard)
        val deleteBtn : ImageView = itemView.findViewById(com.example.tupac.R.id.deletePost)
        val editBtn : ImageView = itemView.findViewById(com.example.tupac.R.id.editPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(com.example.tupac.R.layout.post_row_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.apply {
            val currentPost = post[position]

            mDatabaseReference.child("users").child(currentPost.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(User::class.java)
                        username.text = currentUser?.username
                        profilePic.load(currentUser?.imageURL) {
                            error(com.example.tupac.R.drawable.ic_error_placeholder)
                        }
                        if (mAuth.currentUser?.uid == currentUser?.uid){
                            deleteBtn.visibility = View.VISIBLE
                            editBtn.visibility = View.VISIBLE
                        }else{
                            deleteBtn.visibility = View.INVISIBLE
                            editBtn.visibility = View.INVISIBLE
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            deleteBtn.setOnClickListener {
                listener.deleteClick(currentPost)
            }

            editBtn.setOnClickListener {
                listener.editClick(currentPost)
            }

            minutesAgo.text = DateUtils.getRelativeTimeSpanString(currentPost.postTime!!)
            profileTitle.text = currentPost.title
            postDesc.text = currentPost.content

            postCard.setOnClickListener {
                listener.detailView(currentPost)
            }



        }
    }

    override fun getItemCount(): Int = post.size

    fun setData(newData: ArrayList<Post>) {
        val postDiffUtil = PostsDiffUtil(post, newData)
        val diffUtilResult = DiffUtil.calculateDiff(postDiffUtil)
        post = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

}