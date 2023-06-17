package com.mawumbo.mystoryapp.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.mawumbo.mystoryapp.R
import com.mawumbo.mystoryapp.databinding.FragmentCameraBinding
import com.mawumbo.mystoryapp.utils.createFile
import com.mawumbo.mystoryapp.utils.rotateFile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture = ImageCapture.Builder().build()
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openCamera()
        binding.captureImage.setOnClickListener { capturePhoto() }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            openCamera()
        }
    }

    private fun openCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    activity,
                    "There is problem with the camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture

        val photoFile = activity?.let { createFile(it.application) }

        val outputOptions = photoFile?.let { ImageCapture.OutputFileOptions.Builder(it).build() }
        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Toast.makeText(
                            activity,
                            "Failed to take photo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        rotateFile(
                            photoFile,
                            isBackCamera = (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        )
                        requireActivity().runOnUiThread {
                            setFragmentResult(
                                "requestImage",
                                bundleOf("responseImage" to photoFile.absolutePath)
                            )
                            findNavController().navigateUp()
                        }
                    }
                }
            )
        }
    }

}