package com.example.unsplashcl.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.unsplashcl.databinding.FragmentCollectionsBinding
import com.example.unsplashcl.entity.PhotosCollection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference

@AndroidEntryPoint
class CollectionsFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCollectionsBinding? = null
    private val binding get() = _binding!!
    private val collectionsPagedAdapter = CollectionsPagedAdapter { collection -> onItemClick(collection) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)
        setBtnPanel()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val state = viewModel.state
        binding.recyclerView.adapter = collectionsPagedAdapter
        viewModel.loadCollections(collectionsPagedAdapter)

        viewModel.isLoading.onEach {
            binding.swipeRefresh.isRefreshing = it
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.swipeRefresh.setOnRefreshListener {
            state.isOnLineCheck()
            collectionsPagedAdapter.refresh()
        }
    }

    private fun onItemClick(item: PhotosCollection) {
        viewModel.state.isOnLineCheck()
        if (!viewModel.state.onLine) return
        val id = item.id
        val action = CollectionsFragmentDirections.actionCollectionsFragmentToCollectionDetailsFragment(id)
        findNavController().navigate(action)
    }

    private fun setBtnPanel() {
        val mainActivity: WeakReference<MainActivity> =
            WeakReference(requireActivity() as MainActivity)
        mainActivity.get()!!.binding.buttonPanel.isVisible = true
        mainActivity.get()!!.binding.collectionsBtn.isEnabled = false
        mainActivity.get()!!.binding.photosBtn.isEnabled = true
        mainActivity.get()!!.binding.userBtn.isEnabled = true
    }
}