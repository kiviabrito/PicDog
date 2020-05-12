package com.example.picdog.ui.main.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picdog.R
import com.example.picdog.ui.main.MainStateEvent
import com.example.picdog.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(), DogPictureView {

  companion object {
    private const val ARG_SECTION_NUMBER = "section_number"

    @JvmStatic
    fun newInstance(sectionNumber: Int): MainFragment {
      return MainFragment().apply {
        arguments = Bundle().apply {
          putInt(ARG_SECTION_NUMBER, sectionNumber)
        }
      }
    }
  }

  private lateinit var viewModel: MainViewModel
  private val adapter: DogPictureAdapter by lazy {
    DogPictureAdapter(listOf())
  }
  private var expandedImageDialog: Dialog? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_main, container, false)
    setupView(root)
    return root
  }

  private fun setupView(root: View) {
    viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    setupRecyclerView(root)
    subscribeObservers()
    viewModel.setStateEvent(
      MainStateEvent.GetFeedEvent(
        arguments?.getInt(
          ARG_SECTION_NUMBER
        ) ?: 1
      )
    )
  }

  private fun setupRecyclerView(root: View) {
    val recyclerView: RecyclerView = root.findViewById(R.id.dog_list)
    val layoutManager = GridLayoutManager(requireContext(), 2)
    recyclerView.layoutManager = layoutManager
    adapter.dogPictureView = this
    recyclerView.adapter = adapter
  }

  private fun subscribeObservers() {
    viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
      // Handle Message
      dataState.message?.let { event ->
        event.getContentIfNotHandled()?.let { message ->
          Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
      }
      // Handle loading
      showProgressBar(dataState.loading)
      // Handle Data<T>
      dataState.data?.let { event ->
        event.getContentIfNotHandled()?.let { mainViewState ->
          println("DEBUG: DataState: $mainViewState")
          mainViewState.feed?.let { list ->
            viewModel.setFeedData(list)
          }

          mainViewState.pictureUrl?.let { picture ->
            viewModel.setPicture(picture)
          }
        }
      }
    })

    viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
      viewState.feed?.let { list ->
        println("DEBUG: Setting pictures to RecyclerView: $list")
        adapter.setItemsAdapter(list)
      }

      viewState.pictureUrl?.let {picture ->
        if (picture != "") {
          showExpandedImage(picture)
        }
      }

    })
  }

  override fun openDogPicture(picture: String) {
    viewModel.setStateEvent(MainStateEvent.TappedImage(picture))
  }

  private fun showExpandedImage(picture: String) {
    val expandedLayout = layoutInflater.inflate(R.layout.dialog_expanded_picture, null)
    val expandedImage = expandedLayout.findViewById<ImageView>(R.id.expanded_image)
    Glide.with(requireContext())
      .load(picture)
      .into(expandedImage)
    expandedImageDialog = Dialog(requireContext()).apply {
      this.requestWindowFeature(Window.FEATURE_NO_TITLE)
      this.setCanceledOnTouchOutside(true)
      this.setContentView(expandedLayout)
      this.show()
      this.setOnCancelListener {
        viewModel.setStateEvent(MainStateEvent.TappedImage(""))
      }
    }
  }

  private fun showProgressBar(isVisible: Boolean) {
    if (isVisible) {
      main_progress_bar.visibility = View.VISIBLE
    } else {
      main_progress_bar.visibility = View.INVISIBLE
    }
  }

  override fun onPause() {
    if (expandedImageDialog != null && expandedImageDialog!!.isShowing) {
      expandedImageDialog!!.dismiss()
      expandedImageDialog = null
    }
    super.onPause()
  }

}