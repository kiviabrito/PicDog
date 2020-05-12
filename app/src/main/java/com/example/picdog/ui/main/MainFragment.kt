package com.example.picdog.ui.main

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
import kotlinx.android.synthetic.main.fragment_main.view.*


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
  private val adapter: DogPictureAdapter by lazy { DogPictureAdapter(ArrayList()) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProvider(this).get(MainViewModel::class.java).apply {
      setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val root = inflater.inflate(R.layout.fragment_main, container, false)
    root.main_progress_bar.visibility = View.VISIBLE
    setupRecyclerView(root)
    observers(root)
    return root
  }

  private fun setupRecyclerView(root: View) {
    val recyclerView: RecyclerView = root.findViewById(R.id.dog_list)
    val layoutManager = GridLayoutManager(requireContext(), 2)
    recyclerView.layoutManager = layoutManager
    adapter.dogPictureView = this
    recyclerView.adapter = adapter
  }

  private fun observers(root: View) {
    viewModel.feed.observe(viewLifecycleOwner, Observer { list ->
      adapter.setItemsAdapter(list)
      root.main_progress_bar.visibility = View.GONE
    })

    viewModel.error.observe(viewLifecycleOwner, Observer { error ->
      Toast.makeText(requireContext(), getString(R.string.error_message,error), Toast.LENGTH_LONG).show()
      root.main_progress_bar.visibility = View.GONE
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

}