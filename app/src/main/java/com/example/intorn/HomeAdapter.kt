package com.example.intorn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.intorn.staff.UsersAdapter.UserViewHolder

class HomeAdapter(private val productItems: List<HomeModel>):
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products_recyclerview, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeAdapter.HomeViewHolder, position: Int) {
        val item = productItems[position]
        holder.bind(productItems[position])
    }

    override fun getItemCount(): Int {
        return productItems.size
    }

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.productsName)
        private val stock: TextView = itemView.findViewById(R.id.productsStock)
        private val  price: TextView = itemView.findViewById(R.id.productsPrice)
        fun bind(item: HomeModel){
            name.text = item.name
            stock.text = item.stock
            price.text = item.price
        }

    }
}
