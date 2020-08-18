package com.androidshowtime.snapshatclone

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androidshowtime.snapshatclone.databinding.FragmentImagePickerBinding
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_image_picker.*
import timber.log.Timber
import java.util.*


class ImagePickerFragment : Fragment() {
    private lateinit var storage: FirebaseStorage
    private lateinit var imageName: String
    private lateinit var imageURL: String
    private lateinit var snapMsg: String
    private lateinit var localUrl: Uri


    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {

        it.let {

            localUrl = it
            imageView.setImageURI(it)


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding = FragmentImagePickerBinding.inflate(inflater)


        //The first step in accessing your storage bucket is to create an instance of FirebaseStorage:
        storage = FirebaseStorage.getInstance()


        //creating cloud reference

        val reference = storage.reference

        binding.chooseImage.setOnClickListener {
            imageName = UUID.randomUUID().toString()
            pickImage.launch("image/*")

        }



        binding.nextButton.setOnClickListener {


            snapMsg = editTextSnapchatMsg.text.toString()




            uploadFile(localUrl)

        }

        return binding.root
    }


    private fun uploadFile(filePath: Uri) {

        //getting the storageRef
        val storageRef = storage.reference

        //defining child of storageReference - universally unique identifier (UUID)
        val childRef = storageRef.child("images/$imageName}")

        //putFile() returns an UploadTask Object which monitors the status of the transfer
        childRef.putFile(filePath)

            //add success and failure listeners to the uploadTask Object
            .addOnSuccessListener { task ->
                Toast.makeText(activity, "Upload Successful", Toast.LENGTH_SHORT)
                    .show()
                val downloadUrl = childRef.downloadUrl


                Timber.i("myUrl is $downloadUrl")

                val result = task.metadata?.reference?.downloadUrl

                result?.let { t ->
                    t.addOnSuccessListener {

                        val downLoadLink = it.toString()
                        imageURL = downLoadLink
                        Timber.i("my URL 3 is $downLoadLink")

                        findNavController().navigate(
                            ImagePickerFragmentDirections.actionImagePickerFragmentToContactsFragment(
                                snapMsg,
                                imageName,
                                imageURL
                            )
                        )
                    }

                }


            }
            .addOnFailureListener {

                Toast.makeText(activity, "Upload Failed", Toast.LENGTH_SHORT)
                    .show()
            }.continueWith {
                if (!it.isSuccessful) {
                    it.exception?.let {
                        throw it
                    }
                }
                val downloadUrl = childRef.downloadUrl
                Timber.i("myUrl 2 is $downloadUrl")
            }


    }


}