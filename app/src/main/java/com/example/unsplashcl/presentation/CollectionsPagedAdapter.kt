package com.example.unsplashcl.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unsplashcl.databinding.CollectionItemBinding
import com.example.unsplashcl.entity.PhotosCollection
import javax.inject.Inject

class CollectionsPagedAdapter @Inject constructor(
    private val onClick: (PhotosCollection) -> Unit,

    ): PagingDataAdapter<PhotosCollection, CollectionViewHolder>(CollectionDifutilCallback()) {

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collectionItem = getItem(position)
        var image: String
        var icon: String
        var totalPhotos: Int
        if (collectionItem != null) {
            with(holder.binding) {
                image = collectionItem.coverPhoto.urls.full
                icon = collectionItem.user.profileImage.large
                totalPhotos = collectionItem.totalPhotos

                Glide.with(collectionItemLayout)
                    .load(icon)
                    .circleCrop()
                    .into(userIcon)

                Glide.with(collectionItemLayout)
                    .load(image)
                    .centerCrop()
                    .into(photo)

                photosNumber.text = totalPhotos.toString()
                userName.text = collectionItem.user.name
                title.text = collectionItem.title
            }
        }
        holder.binding.collectionItemLayout.setOnClickListener {
            collectionItem?.let {
                onClick(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding = CollectionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CollectionViewHolder(binding)
    }
}

class CollectionDifutilCallback : DiffUtil.ItemCallback<PhotosCollection>() {
    override fun areItemsTheSame(oldItem: PhotosCollection, newItem: PhotosCollection): Boolean {
       return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: PhotosCollection, newItem: PhotosCollection): Boolean {
        return oldItem == newItem
    }
}

class CollectionViewHolder @Inject constructor
    (val binding: CollectionItemBinding) :
    RecyclerView.ViewHolder(binding.root)