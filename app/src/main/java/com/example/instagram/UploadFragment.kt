package com.example.instagram

//import com.example.hanstargram.navigation.model.ContentDTO
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentTransaction
import com.example.instagram.databinding.FragmentUploadBinding
import com.example.instagram.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*

class UploadFragment : BaseFragment<FragmentUploadBinding>(R.layout.fragment_upload) {
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun initStartView() {
        super.initStartView()

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"



        binding.addphotoBtnUpload.setOnClickListener {
            // 업로드 사진없으면 null 처리 해야됨
            contentUpload()
            //사진 업로드 후 재시도 할 시 프래그먼트가 피드로 완전히 바뀌어 있음
        }
        binding.addphotoImage.setOnClickListener {

            launcher.launch(photoPickerIntent)
        }


    }


    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //This is path to the selected image
                photoUri = result.data?.data
                binding.addphotoImage.setImageURI(photoUri)

            } else {
                // Exit the addPhotoActivity if you leave the album without selecting it
                activity?.finish()
            }
        }

    override fun initDataBinding() {
        super.initDataBinding()

        // 여기다가 binding
    }

    override fun initAfterBinding() {
        super.initAfterBinding()

    }

    fun contentUpload() {
        //make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        // 이미지 이름을 현재시간으로 정해줘서 중복 방지

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //이미지 업로드 (프로미스 방식)
        storageRef?.putFile(photoUri!!)?.continueWithTask() { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            contentDTO.imageUrl = uri.toString()

            contentDTO.uid = auth?.currentUser?.uid

            contentDTO.userId = auth?.currentUser?.email

            contentDTO.explain = binding!!.addphotoEditExplain.text.toString()

            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            activity?.setResult(Activity.RESULT_OK)
//            activity?.finish()
            navController.navigate(R.id.action_uploadFragment_self)


        }
    }

}