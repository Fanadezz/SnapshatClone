package com.androidshowtime.snapshatclone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidshowtime.snapshatclone.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.*
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoginBinding.inflate(inflater)
        (activity as AppCompatActivity).supportActionBar?.title = "Login"

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.button.setOnClickListener { loginTheUser() }

        return binding.root
    }


    private fun loginTheUser() {
        val email = userNameEdt.text.toString()
        val password = passwordEdt.text.toString()
        Timber.i("$password and $email")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) {

            if (it.isSuccessful) {
                navigateToHomeScreen()
                Timber.i("loginInWithEmail: success")
                Toast.makeText(activity, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                createTheUser(email, password)

            }
        }

    }

    private fun createTheUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    navigateToHomeScreen()
//create a reference pointing to users
                    val reference = db.reference.child("users")

                    //get firebase user from the task result
                    val user = task.result?.user

                    //null-check on the user before gettin user's UID
                    user?.let {


                        reference.child(it.uid).child("email").setValue(email)

                    }

                    Toast.makeText(activity, "SignUp Successful", Toast.LENGTH_SHORT).show()


                } else {

                    Toast.makeText(activity, "Authentication Failed, Try Again", Toast.LENGTH_SHORT)
                        .show()
                }

            }
    }


    override fun onStart() {
        super.onStart()
        //check if the user is signed in

        if (auth.currentUser != null) {
            navigateToHomeScreen()
        }

    }


    private fun navigateToHomeScreen() {
        //navigate to home

        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }


}