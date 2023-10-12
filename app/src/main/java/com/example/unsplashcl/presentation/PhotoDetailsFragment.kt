package com.example.unsplashcl.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.FragmentPhotoDetailsBinding
import com.example.unsplashcl.entity.PhotoWithDetails
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class PhotoDetailsFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!
    private val pattern = "https://api.unsplash.com/photos".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        setBtnPanel()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val state = viewModel.state
        state.isOnLineCheck()

        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)



        viewLifecycleOwner.lifecycleScope.launch {
            var image: String
            var icon: String
            var likeHeart: Int

            viewModel.photoByIdFlow.collect { photoItem ->
                if (photoItem == null) return@collect
                binding.swipeRefresh.setOnRefreshListener {
                    state.isOnLineCheck()
                    viewModel.loadPhotoById(photoItem.id)
                }
                if (state.onLine) {
                    image = photoItem.urls.full
                    icon = photoItem.user.profileImage.large

                    likeHeart = when (photoItem.likedByUser) {
                        true -> R.drawable.heart_full
                        false -> R.drawable.heart_empty
                    }
                    binding.likesNumber.text = photoItem.likes.toString()
                    binding.userName.text = photoItem.user.name

                    Glide.with(this@PhotoDetailsFragment)
                        .load(icon)
                        .circleCrop()
                        .into(binding.userIcon)

                    Glide.with(this@PhotoDetailsFragment)
                        .load(image)
                        .centerCrop()
                        .into(binding.photo)

                    Glide.with(this@PhotoDetailsFragment)
                        .load(likeHeart)
                        .into(binding.heart)

                    if (photoItem.exif?.model != null) {
                        binding.exif.text =
                            getString(R.string.make) + photoItem.exif?.make + "\n" +
                                    getString(R.string.model) + photoItem.exif?.model + "\n" +
                                    getString(R.string.exposure) + photoItem.exif?.exposureTime + "\n" +
                                    getString(R.string.aperture) + photoItem.exif?.aperture + "\n" +
                                    getString(R.string.focal_length) + photoItem.exif?.focalLength + "\n" +
                                    getString(R.string.iso) + photoItem.exif?.iso
                    }

                    if (photoItem.location?.country != null) {
                        binding.location.text =
                            getString(R.string.location) + photoItem.location?.country +
                                    photoItem.location?.city
                        binding.location.setOnClickListener {
                            val geoUriString =
                                "geo:${photoItem.location?.position?.latitude},${photoItem.location?.position?.longitude}?z=14"
                            val geoUri: Uri = Uri.parse(geoUriString)
                            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                            startActivity(mapIntent)
                        }
                    }

                    binding.tags.text = photoItem.tags.joinToString(separator = ",") { it.title }
                    binding.generateLinkBtn.setOnClickListener {
                        binding.link.setText(pattern.toString() + "/" + photoItem.id)
                    }
                    binding.link.addTextChangedListener {
                        val link = it.toString().substringBeforeLast("/")
                        binding.openLinkBtn.isEnabled = pattern.matches(link)
                    }
                    binding.openLinkBtn.setOnClickListener {
                        val id = binding.link.text.toString().substringAfterLast("/")
                        viewModel.loadPhotoById(id)
                    }
                    binding.downloaded.text =
                        getString(R.string.downloads) + photoItem.downloads

                    binding.likeLayout.setOnClickListener {
                        onItemLike(photoItem)
                    }

                    binding.downloadBtn.setOnClickListener {
                        state.isOnLineCheck()
                        if (state.onLine) {
                            var uri: String?
                            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                uri = viewModel.saveImage(photoItem)
                                val snackbar = Snackbar.make(
                                    binding.layout,
                                    getString(R.string.image_saved) + uri,
                                    Snackbar.LENGTH_SHORT
                                )
                                if (uri != null) {
                                    snackbar.setAction(getString(R.string.open)) {
                                        onSnackbarClick(
                                            uri!!
                                        )
                                    }
                                    viewModel.trackPhotoDownloadUseCase.execute(photoItem.id, photoItem.links.downloadLocation)
                                    snackbar.show()
                                }
                            }
                        } else {
                            viewModel.photosToLoad.add(photoItem)
                            Snackbar.make(
                                binding.layout,
                                getString(R.string.download_later),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onItemLike(item: PhotoWithDetails) {
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

    private fun onSnackbarClick(uri: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.collectionsBtn.isEnabled = true
        mainActivity.get()!!.binding.photosBtn.isEnabled = true
        mainActivity.get()!!.binding.userBtn.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
