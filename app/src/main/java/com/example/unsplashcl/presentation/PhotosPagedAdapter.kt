package com.example.unsplashcl.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplashcl.R
import com.example.unsplashcl.databinding.PhotoItemBinding
import com.example.unsplashcl.entity.Photo
import javax.inject.Inject

class PhotosPagedAdapter @Inject constructor(
    private val onClick: (Photo) -> Unit,
    private val onLike: (Photo, PhotoItemBinding) -> Unit
) : PagingDataAdapter<Photo, PhotoViewHolder>(DifutiilCallback()) {

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        var likeHeart: Int
        var image: String
        var icon: String
        var likesNumber: Int

        if (photoItem != null) {
            with(holder.binding) {

                image = photoItem.urls.full
                icon = photoItem.user.profileImage.large
                likesNumber = photoItem.likes
                likeHeart = when (photoItem.likedByUser) {
                    true -> R.drawable.heart_full
                    false -> R.drawable.heart_empty
                }
                this.likesNumber.text = likesNumber.toString()
                userName.text = photoItem.user.name

                Glide.with(photoItemLayout)
                    .load(icon)
                    .circleCrop()
                    .into(userIcon)

                Glide.with(photoItemLayout)
                    .load(image)
                    .centerCrop()
                    .into(photo)

                Glide.with(photoItemLayout)
                    .load(likeHeart)
                    .into(heart)
            }
        }
        holder.binding.photoItemLayout.setOnClickListener {
            photoItem?.let {
                onClick(it)
            }
        }
        holder.binding.likeLayout.setOnClickListener {
            photoItem?.let {
                onLike(it, holder.binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }
}

class DifutiilCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
        oldItem == newItem
}

class PhotoViewHolder @Inject constructor
    (val binding: PhotoItemBinding) :
    RecyclerView.ViewHolder(binding.root)
