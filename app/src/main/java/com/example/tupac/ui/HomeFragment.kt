package com.example.tupac.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tupac.MainActivity
import com.example.tupac.R
import com.example.tupac.adapter.PostAdapter
import com.example.tupac.adapter.cardClick
import com.example.tupac.databinding.FragmentHomeBinding
import com.example.tupac.model.Post
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), cardClick {
    private lateinit var binding: FragmentHomeBinding
    private val args by navArgs<HomeFragmentArgs>()
    private val args1 by navArgs<PostFragmentArgs>()
    private val mAdapter by lazy { PostAdapter(this@HomeFragment) }
    private val mDatabaseReference: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val mPost: ArrayList<Post> by lazy { ArrayList() }
    lateinit var postFab:FloatingActionButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val  root = binding.root
        binding.ivProfilePic.load(args.currentUser?.imageURL)

        setupRecyclerView()
        getPosts()

        binding.postRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.postFab.hide()
                } else {
                    binding.postFab.show()
                }
            }
        })

        binding.ivSetting.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToUserDetailFragment(args.currentUser)
            findNavController().navigate(action)
        }
        postFab = root.findViewById(R.id.postFab)

        postFab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPostFragment(args1.currentUserInfo)
           findNavController().navigate(action)
        }

        return root
    }

    private fun getPosts() {
        mDatabaseReference.child("posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mPost.clear()
                    for (snap in snapshot.children) {
                        val listPost = snap.getValue(Post::class.java)
                        mPost.add(listPost!!)
                    }
                    mPost.reverse()
                    mAdapter.setData(mPost)
                    binding.postRecyclerview.hideShimmer()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun setupRecyclerView() {
        val alphaAdapter = AlphaInAnimationAdapter(mAdapter)
        binding.postRecyclerview.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
            setDuration(1000)
            setHasStableIds(false)
            setFirstOnly(false)
            setInterpolator(OvershootInterpolator(.100f))
        }
        binding.postRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun detailView(currentPost: Post) {
        val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment2(
            currentPost,
            args.currentUser
        )
        findNavController().navigate(action)
    }

    override fun deleteClick(currentPost: Post) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this Post")
            .setPositiveButton("Yes") { _, _ ->
                deleteDataFromDb(currentPost)
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }

    override fun editClick(currentPost: Post) {
        val action =
            HomeFragmentDirections.actionHomeFragmentToEditFragment(currentPost, args.currentUser)
        findNavController().navigate(action)
    }

    private fun deleteDataFromDb(currentPost: Post) {
        val ref = mDatabaseReference.child("posts").child(currentPost.postTime.toString())
        ref.removeValue()
    }
}

private fun ExtendedFloatingActionButton.expand() {
    TODO("Not yet implemented")
}

private fun MaterialButton.collapse() {
    TODO("Not yet implemented")
}


