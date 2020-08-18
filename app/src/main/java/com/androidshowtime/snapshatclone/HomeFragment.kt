package com.androidshowtime.snapshatclone

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidshowtime.snapshatclone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var auth: FirebaseAuth
    private lateinit var emailsList: MutableList<String>
    private lateinit var snapshotsList: MutableList<DataSnapshot>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        emailsList = mutableListOf()
        snapshotsList = mutableListOf()


        val binding = FragmentHomeBinding.inflate(inflater)
        auth = FirebaseAuth.getInstance()

        listView = binding.listview
        (activity as AppCompatActivity).supportActionBar?.title = " Home"
        // Inflate the layout for this fragment


        val adapter = ArrayAdapter<String>(
            requireActivity(),
            android.R.layout.simple_expandable_list_item_1,
            emailsList
        )
        listView.adapter = adapter
        setHasOptionsMenu(true)


        val currentUser = FirebaseAuth.getInstance().currentUser!!


        FirebaseDatabase.getInstance().reference.child("users").child(currentUser.uid)
            .child("snaps").addChildEventListener(object : ChildEventListener {


                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val email = snapshot.child("from").value as String
                    emailsList.add(email)
                    snapshotsList.add(snapshot)
                    adapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    emailsList.remove(snapshot.key)
                    snapshotsList.remove(snapshot)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}


            })


        onListItemClick(listView)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu, menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.new_snapshot -> {

                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToImagePickerFragment())
            }
            R.id.log_out -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
                auth.signOut()
            }

        }


        return super.onOptionsItemSelected(item)


    }


    private fun onListItemClick(listView: ListView) {


        listView.setOnItemClickListener { adapterView, view, i, l ->

            val snapshot = snapshotsList[i]

            val imageName = snapshot.child("imageName").value.toString()
            val imageURL = snapshot.child("imageURL").value.toString()
            val snapMsg = snapshot.child("message").value.toString()
            val snapKey = snapshot.key!!



            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSnapshatFragment(
                    imageName,
                    imageURL,
                    snapMsg,
                    snapKey
                )
            )

        }
    }


}