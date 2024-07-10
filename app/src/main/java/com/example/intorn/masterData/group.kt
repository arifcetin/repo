package com.example.intorn.masterData

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intorn.DatabaseHelper
import com.example.intorn.HomeFragment
import com.example.intorn.MainActivity
import com.example.intorn.R
import com.example.intorn.databinding.FragmentGroupBinding
import com.example.intorn.databinding.FragmentStaffBinding
import com.example.intorn.staff.UsersAdapter
import com.google.android.material.navigation.NavigationView


class group : Fragment() {


    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var group_adapter: Group_adapter
    private lateinit var binding: FragmentGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentGroupBinding.inflate(inflater, container, false)
        initClickListener()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper= DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.group_recyclerview)


        val items = databaseHelper.getGroupsName()
        group_adapter = Group_adapter(items)
        recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2,
                LinearLayoutManager.VERTICAL, false)
            adapter = group_adapter
        }


    }
    private fun initClickListener(){
        binding.addGroupLinearLayout.setOnClickListener {
            MainActivity.navController.navigate(R.id.addGroup)
        }
    }

}