package com.example.picdog.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picdog.R

interface DogPictureView {
  fun openDogPicture(picture: String)
}

class DogPictureAdapter(private var items: List<String>) :
  RecyclerView.Adapter<DogPictureAdapter.DogListViewHolder>() {

  var dogPictureView: DogPictureView? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dog_list, parent, false)
    return DogListViewHolder(view)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun setItemsAdapter(newList: List<String>) {
    val oldList = items
    val diffCallback = PokemonDiffCallback(oldList, newList)
    val diffResult = DiffUtil.calculateDiff(diffCallback)
    items = newList
    diffResult.dispatchUpdatesTo(this)
  }

  override fun onBindViewHolder(holder: DogListViewHolder, position: Int) {
    val item = items[position]
    holder.bindItems(item)

    holder.itemView.setOnClickListener {
      dogPictureView?.openDogPicture(item)
    }
  }

  class DogListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindItems(item: String) {

      val imageView: ImageView = itemView.findViewById(R.id.dog_picture) as ImageView

      Glide.with(itemView.context)
        .load(item)
        .centerCrop()
        .into(imageView)
    }

  }

  class PokemonDiffCallback(
    private val oldList: List<String>,
    private val newList: List<String>
  ) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
      return oldList.size
    }

    override fun getNewListSize(): Int {
      return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
      return oldList[oldItemPosition].equals(newList[newItemPosition])
    }

  }

}

