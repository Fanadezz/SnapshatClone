package com.androidshowtime.snapshatclone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androidshowtime.snapshatclone.databinding.FragmentContactsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class ContactsFragment : Fragment() {

    private lateinit var db: FirebaseDatabase
    private lateinit var emailList: MutableList<String>
    private lateinit var keysList: MutableList<String>


    private val args: ContactsFragmentArgs by navArgs()

    lateinit var listView: ListView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseDatabase.getInstance()

        val binding = FragmentContactsBinding.inflate(inflater)
        listView = binding.contactsListView
        emailList = mutableListOf()
        keysList = mutableListOf()

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, emailList)

        listView.adapter = adapter

        //obtaining reference to database
        val reference = db.reference.child("users")

        //put a listener to the reference's child
        reference.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val email = snapshot.child("email").value.toString()
                emailList.add(email)
                keysList.add(snapshot.key!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

                val removedAddress = snapshot.child("email").value.toString()
                emailList.remove(removedAddress)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}


        })
        createSnap(listView)

        return binding.root
    }


    fun createSnap(listView: ListView) {

        val from = FirebaseAuth.getInstance().currentUser?.email
        val imageName = args.imageName
        val imageURL = args.imageUrl
        val message = args.snapChatTextMsg

        val snapMap = mapOf(
            "from" to from,
            "imageName" to imageName,
            "imageURL" to imageURL,
            "message" to message
        )

        listView.setOnItemClickListener { adapterView, view, i, l ->

            //push() creates random child with a random name
            db.reference.child("users").child(keysList[i]).child("snaps").push().setValue(snapMap)
            findNavController().navigate(ContactsFragmentDirections.actionContactsFragmentToHomeFragment())
        }

    }


}