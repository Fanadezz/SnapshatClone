package com.androidshowtime.snapshatclone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androidshowtime.snapshatclone.databinding.FragmentSnapshatBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber

class SnapshatFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private val args: SnapshatFragmentArgs by navArgs()
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        storage = FirebaseStorage.getInstance()
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val binding = FragmentSnapshatBinding.inflate(inflater)
        textView = binding.snapTextView
        imageView = binding.snapImageView


        val imageName = args.imageName
        val imageURL = args.imageURL
        val key = args.snapKey
        val msg = args.snapMsg
        Timber.i("the image is called $imageName while the key is $key and message is $msg  \n URL is: $imageURL")


        textView.text = msg

        Glide.with(imageView.context).load(imageURL).into(imageView)

        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requireActivity().onBackPressedDispatcher.addCallback(this) {


            val currentUser = auth.currentUser!!

            db.reference.child("users").child(currentUser.uid).child("snaps").child(args.snapKey)
                .removeValue()
            Toast.makeText(activity, "Snapchat Deleted", Toast.LENGTH_SHORT).show()

            storage.reference.child("images").child(args.imageName).delete()

            findNavController().navigate(SnapshatFragmentDirections.actionSnapshatFragmentToHomeFragment())
        }
    }

}