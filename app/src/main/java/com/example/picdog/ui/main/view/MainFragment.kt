package com.example.picdog.ui.main.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picdog.R
import com.example.picdog.ui.main.MainStateEvent
import com.example.picdog.ui.main.MainViewModel
import com.example.picdog.utility.DataStateListener


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
  lateinit var dataStateHandler: DataStateListener
  private val adapter: DogPictureAdapter by lazy {
    DogPictureAdapter(
      listOf()
    )
  }

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

  private fun subscribeObservers(){
    viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

      // Handle Loading and Message
      dataStateHandler.onDataStateChange(dataState)

      // handle Data<T>
      dataState.data?.let{ event ->
        event.getContentIfNotHandled()?.let{ mainViewState ->

          println("DEBUG: DataState: $mainViewState")

          mainViewState.feed?.let{ list ->
            // set BlogPosts data
            viewModel.setFeedData(list)
          }

        }
      }
    })

    viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState ->
      viewState.feed?.let {list ->
        // set BlogPosts to RecyclerView
        println("DEBUG: Setting blog posts to RecyclerView: ${list}")
        adapter.setItemsAdapter(list)
      }

    })
  }

  override fun openDogPicture(picture: String) {
    val expandedLayout = layoutInflater.inflate(R.layout.dialog_expanded_picture, null)
    val expandedImage = expandedLayout.findViewById<ImageView>(R.id.expanded_image)

    Glide.with(requireContext())
      .load(picture)
      .into(expandedImage)

    val builder = Dialog(requireContext())
    builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
    builder.setContentView(expandedLayout)
    builder.show()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try{
      dataStateHandler = context as DataStateListener
    }catch(e: ClassCastException){
      println("$context must implement DataStateListener")
    }

  }

}