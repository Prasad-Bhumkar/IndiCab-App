 package com.example.indicab.adapters
 
 import android.view.LayoutInflater
 import android.view.ViewGroup
 import androidx.recyclerview.widget.DiffUtil
 import androidx.recyclerview.widget.ListAdapter
 import androidx.recyclerview.widget.RecyclerView
 import com.bumptech.glide.Glide
 import com.example.indicab.databinding.ItemCarTypeBinding
 import com.example.indicab.models.CarType
 
 class CarTypesAdapter(
     private val onItemClick: (CarType) -> Unit
 ) : ListAdapter<CarType, CarTypesAdapter.CarTypeViewHolder>(CarTypeDiffCallback()) {
 
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarTypeViewHolder {
         val binding = ItemCarTypeBinding.inflate(
             LayoutInflater.from(parent.context),
             parent,
             false
         )
         return CarTypeViewHolder(binding)
     }
 
     override fun onBindViewHolder(holder: CarTypeViewHolder, position: Int) {
         holder.bind(getItem(position))
     }
 
     inner class CarTypeViewHolder(
         private val binding: ItemCarTypeBinding
     ) : RecyclerView.ViewHolder(binding.root) {
 
         init {
             binding.root.setOnClickListener {
                 val position = adapterPosition
                 if (position != RecyclerView.NO_POSITION) {
                     onItemClick(getItem(position))
                 }
             }
         }
 
         fun bind(carType: CarType) {
             binding.apply {
                 carTypeName.text = carType.name
                 carTypeDescription.text = carType.description
                 carTypeBasePrice.text = "₹${carType.basePrice}"
                 carTypeCapacity.text = "${carType.capacity} seats"
 
                 Glide.with(carTypeImage)
                     .load(carType.imageUrl)
                     .centerCrop()
                     .into(carTypeImage)
             }
         }
     }
 
     private class CarTypeDiffCallback : DiffUtil.ItemCallback<CarType>() {
         override fun areItemsTheSame(oldItem: CarType, newItem: CarType): Boolean {
             return oldItem.id == newItem.id
         }
 
         override fun areContentsTheSame(oldItem: CarType, newItem: CarType): Boolean {
             return oldItem == newItem
         }
     }
 } 