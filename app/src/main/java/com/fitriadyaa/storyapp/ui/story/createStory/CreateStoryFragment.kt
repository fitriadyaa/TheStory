package com.fitriadyaa.storyapp.ui.story.createStory

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.fitriadyaa.storyapp.databinding.FragmentCreateStoryBinding
import com.fitriadyaa.storyapp.utils.ViewModelFactory
import java.io.File
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fitriadyaa.storyapp.data.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import android.database.Cursor
import android.graphics.Matrix
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.fitriadyaa.storyapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class CreateStoryFragment : Fragment() {
    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding!!

    private val createStoryViewModel: CreateStoryViewModel by viewModels {
        ViewModelFactory(requireActivity())
    }

    private lateinit var outputDirectory: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun dispatchTakePictureIntent() {
        val photoFile = createImageFile()
        photoFile?.let { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.file-provider",
                file
            )
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            launcherIntentCamera.launch(takePictureIntent)
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmm-ss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            outputDirectory = this
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = BitmapFactory.decodeFile(outputDirectory.absolutePath)
            binding.ivStory.setImageBitmap(imageBitmap)
            getFile = outputDirectory
        } else {
            Toast.makeText(requireContext(), "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        outputDirectory = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmm-ss", Locale.getDefault()).format(Date())}_",
            ".jpg",
            requireContext().cacheDir
        )

        binding.btnCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnImage.setOnClickListener {
            startGallery()
        }

        binding.btnUpload.setOnClickListener {
            uploadImage()
        }

        @Suppress("DEPRECATION") val fileUri = arguments?.get("selected_image") as? Uri
        if (fileUri != null) {
            val isBackCamera = arguments?.getBoolean("isBackCamera") ?: false
            val result = rotateBitmap(
                BitmapFactory.decodeFile(fileUri.path),
                isBackCamera
            )
            binding.ivStory.setImageBitmap(result)
            getFile = fileUri.toFile()
        }
    }

    private var getFile: File? = null
    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.ivStory.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        getFile?.let { file ->
            showLoading(true)
            val reducedFile = reduceFileImage(file)
            val descriptionText = binding.edtDesc.text.toString()
            if (descriptionText.isNotEmpty()) {
                val description = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    reducedFile.name,
                    requestImageFile
                )

                createStoryViewModel.postStory(imageMultipart, description).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Success -> {
                            showLoading(false)
                            Toast.makeText(context, result.data.message, Toast.LENGTH_LONG).show()
                            findNavController().navigate(CreateStoryFragmentDirections.actionCreateStoryFragmentToListStoryFragment())
                        }
                        is Result.Loading -> showLoading(true)
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(context, result.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } else {
                Toast.makeText(requireContext(), getString(R.string.warning_text), Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        } ?: run {
            Toast.makeText(requireContext(), getString(R.string.warning_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressIndicator.isVisible = state
        binding.ivStory.isEnabled = !state
        binding.btnCamera.isEnabled = !state
        binding.btnUpload.isEnabled = !state
        binding.btnImage.isEnabled = !state
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(if (isBackCamera) 0F else 180F)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun reduceFileImage(file: File): File {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)

        val reqWidth = 720
        val reqHeight = 1280

        var inSampleSize = 1
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            val heightRatio = (options.outHeight.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (options.outWidth.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }

        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = inSampleSize

        val reducedBitmap = BitmapFactory.decodeFile(file.absolutePath, bitmapOptions)

        val reducedFile = File.createTempFile(
            "reducedImage_", ".jpg",
            requireContext().cacheDir
        )
        val fileOutputStream = FileOutputStream(reducedFile)
        reducedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
        fileOutputStream.close()

        return reducedFile
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        var cursor: Cursor? = null
        return try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.let {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                val fileName = it.getString(nameIndex)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, fileName)
                val outputStream = FileOutputStream(file)
                if (inputStream != null) {
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                }
                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            cursor?.close()
        }
    }
}
