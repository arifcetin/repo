package com.example.intorn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class StaffFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)

        // RecyclerView için örnek veri oluştur
        val items = listOf(
            ItemModel(R.drawable.ic_staffs_icon, "Staff"),
            ItemModel(R.drawable.ic_staff_roles_icon, "Staff Roles")
        )

        // LayoutManager ve Adapter'i RecyclerView'e bağla
        itemAdapter = ItemAdapter(items)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = itemAdapter
        }
        itemAdapter.setOnClickListener(object : ItemAdapter.OnClickListener{
            override fun onClick(position: Int, model: ItemModel) {
                if(model.text=="Staff"){
                    moveToNewActivity()
                }
            }

        })
        return view
    }
    private fun moveToNewActivity() {
        val i = Intent(activity, StaffActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
    }

}