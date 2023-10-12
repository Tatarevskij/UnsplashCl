package com.example.unsplashcl.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.FragmentPhotosBinding
import com.example.unsplashcl.databinding.PhotoItemBinding
import com.example.unsplashcl.entity.Photo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

@AndroidEntryPoint
class PhotosFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPhotosBinding? = null
    private val binding get() = _binding!!

    private val photosPagedAdapter = PhotosPagedAdapter({ Photo -> onItemClick(Photo) },
        { Photo, PhotoItemBinding -> onItemLike(Photo, PhotoItemBinding) })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosBinding.inflate(inflater, container, false)
        setBtnPanel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.state
        state.isOnLineCheck()

        binding.recyclerView.adapter = photosPagedAdapter
        viewModel.loadPhotos(photosPagedAdapter)
        viewModel.getToken()

        binding.searchBtn.setOnClickListener {
            if (!binding.input.isVisible) binding.input.isVisible = true
            else viewModel.loadPhotos(photosPagedAdapter, binding.input.text.toString())
        }

        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.swipeRefresh.setOnRefreshListener {
            state.isOnLineCheck()
            photosPagedAdapter.refresh()
        }
    }

    private fun onItemClick(item: Photo) {
        if (!viewModel.state.onLine) return
        viewModel.loadPhotoById(item.id)
        findNavController().navigate(R.id.action_photosFragment_to_photoDetailsFragment)
    }

    @SuppressLint("SetTextI18n")
    private fun onItemLike(item: Photo, binding: PhotoItemBinding) {
        val isLiked = item.likedByUser
        val likeHeart: Int

        if (isLiked) {
            viewModel.unlikePhoto(item.id)
            if (!viewModel.state.onLine) return
            likeHeart = R.drawable.heart_empty
            binding.likesNumber.text = (binding.likesNumber.text.toString().toInt() - 1).toString()
            item.likedByUser = false
        } else {
            viewModel.likePhoto(item.id)
            if (!viewModel.state.onLine) return
            likeHeart = R.drawable.heart_full
            binding.likesNumber.text = (binding.likesNumber.text.toString().toInt() + 1).toString()
            item.likedByUser = true
        }
        Glide.with(this)
            .load(likeHeart)
            .into(binding.heart)
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.collectionsBtn.isEnabled = true
        mainActivity.get()!!.binding.photosBtn.isEnabled = false
        mainActivity.get()!!.binding.userBtn.isEnabled = true
    }

}