package com.example.unsplashcl.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.FragmentUserBinding
import com.example.unsplashcl.entity.Photo
import com.example.unsplashcl.entity.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class UserFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private var user: User? = null

    private val photosPagedAdapter = PhotosPagedAdapter({ Photo -> onItemClick(Photo) },
        { _, _ ->  })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        setBtnPanel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val state = viewModel.state
        state.isOnLineCheck()
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.exit_question))
                setPositiveButton(getString(R.string.ok)
                ) { _, _ ->
                    viewModel.removeToken()
                    viewModel.clearDb()
                    findNavController().navigate(R.id.action_global_welcomeFragment)
                }
                setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            }
            builder.create()
        }

        binding.recyclerView.adapter = photosPagedAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            user = viewModel.getMyInfo()
            binding.name.text = user?.name
            binding.location.text = user?.location.toString()
            Glide.with(this@UserFragment)
                .load(user?.profileImage?.large)
                .circleCrop()
                .into(binding.icon)

            viewModel.loadPhotosLikedByUser(photosPagedAdapter, user?.name.toString())
        }

        binding.location.setOnClickListener {
            val geoUriString =
                "geo:0,0?q=${binding.location.text}&z=14"
            val geoUri: Uri = Uri.parse(geoUriString)
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
            startActivity(mapIntent)
        }

        binding.clearBtn.setOnClickListener {
            viewModel.clearDb()
        }

        binding.logoutBtn.setOnClickListener {
            alertDialog?.show()
        }
    }

    private fun onItemClick(item: Photo) {
        if (!viewModel.state.onLine) return
        viewModel.loadPhotoById(item.id)
        findNavController().navigate(R.id.action_userFragment_to_photoDetailsFragment)
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.collectionsBtn.isEnabled = true
        mainActivity.get()!!.binding.photosBtn.isEnabled = true
        mainActivity.get()!!.binding.userBtn.isEnabled = false
    }
}

