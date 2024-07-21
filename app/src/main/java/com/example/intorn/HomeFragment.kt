package com.example.intorn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.intorn.databinding.FragmentHomeBinding
import com.example.intorn.databinding.ReceiptAlertDialogBinding
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleDataArrayList: ArrayList<HomeModel>
    private lateinit var articleDataArrayListTemp: ArrayList<HomeModel>
    private lateinit var articleSellList: MutableList<SellingProcessModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var billRecyclerView: RecyclerView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var billAlertAdapter: BillAlertAdapter
    private lateinit var sellingProcessAdapter: SellingProcessAdapter
    private var totalText = ""
    private var selectedPaymentOption = "Bar" // Varsayılan olarak "Bar" seçeneği

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.products)
        billRecyclerView = view.findViewById(R.id.recyclerView)
        articleDataArrayListTemp = ArrayList()
        articleSellList = ArrayList()
        articleDataArrayList = databaseHelper.getProducts() as ArrayList<HomeModel>
        setupNumberClickListeners()
        loadProductsData()
        loadSellingList()
        productOnClick()


        binding.textviewDelete.setOnClickListener {
            if (totalText.isNotEmpty()) {
                totalText = totalText.substring(0, totalText.length - 1)
                binding.textViewTotal.text = totalText
            }
        }

        binding.textviewPay.setOnClickListener {
            // Varsayılan olarak seçili seçeneği göster
            handlePaymentOption(selectedPaymentOption)
        }

        binding.textviewPay.setOnLongClickListener {
            showPaymentOptions()
            true // LongClickListener'ı tüketmek için true döndürüyoruz
        }
    }

    private fun loadSellingList() {
        val newList = articleSellList.map { SellingProcessDtoModel(it.name, it.price, it.quantity) }
        sellingProcessAdapter = SellingProcessAdapter(newList)
        billRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sellingProcessAdapter
        }
    }

    private fun productOnClick() {
        homeAdapter.setOnClickListener(object : HomeAdapter.OnClickListener {
            override fun onClick(position: Int, model: HomeModel) {
                val productId = databaseHelper.getProductId(model.name).toString()
                val stock = databaseHelper.getProductStock(productId)
                val homeModel = articleDataArrayList[position]
                Log.v("asdfasdf", position.toString())
                if (stock > 0 && homeModel.stock > 0) {
                    /*items.add(SellingProcessModel(model.name, model.price.toDouble(), model.stock))
                    sellingProcessAdapter.notifyDataSetChanged()*
                    if (databaseHelper.readSellingProcess(productId)) {
                        databaseHelper.updateSellingProcess(productId, model.price)
                    } else {
                        databaseHelper.insertSellingProcess(
                            model.name,
                            model.price,
                            model.stock.toString(),
                            productId
                        )
                    }
                     */
                    val article = articleSellList.find { it.name == homeModel.name }
                    if(article != null){
                        article.quantity += 1
                    }
                    else{
                        articleSellList.add(SellingProcessModel(homeModel.name, homeModel.price.toDouble(), 1, productId.toInt()))
                    }
                    homeModel.stock -= 1
                    articleDataArrayList[position] = homeModel
                    databaseHelper.updateProductStock(productId, stock)
                    homeAdapter.notifyDataSetChanged()
                    sellingProcessAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(activity, "Not enough stock", Toast.LENGTH_SHORT).show()
                }

                loadSellingList()

            }
        })

    }

    private fun setupNumberClickListeners() {
        // Burada tıklanabilir textview'leri tanımlayın ve onClickListener'larını ayarlayın
        val numberTextViews = arrayOf(
            binding.textview0, binding.textview1, binding.textview2, binding.textview3,
            binding.textview4, binding.textview5, binding.textview6, binding.textview7,
            binding.textview8, binding.textview9, binding.textviewC, binding.textviewComma
        )

        for (textView in numberTextViews) {
            textView.setOnClickListener {
                val number = textView.text.toString()
                if (textView.id == R.id.textviewC) {
                    totalText = ""
                } else {
                    totalText += number
                }
                binding.textViewTotal.text = totalText
            }
        }
    }

    private fun loadProductsData() {
        //val newList = articleDataArrayList.sortedWith(compareBy { it.name })
        //val newArrayList = java.util.ArrayList<HomeModel>()
        //newArrayList.addAll(newList)
        homeAdapter = HomeAdapter(articleDataArrayList)
        recyclerView.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 4, LinearLayoutManager.VERTICAL, false)
            adapter = homeAdapter
        }
        binding.layoutSearchview.setOnClickListener {
            binding.searchArticle.onActionViewExpanded()
        }
        binding.searchArticle.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    articleDataArrayListTemp.clear()
                    val searchText = p0!!.lowercase(Locale.getDefault())
                    if (searchText.isNotEmpty()) {
                        articleDataArrayList.forEach {
                            if (it.name.lowercase(Locale.getDefault()).contains(searchText)) {
                                articleDataArrayListTemp.add(it)
                            }
                        }
                        homeAdapter.updateItems(articleDataArrayListTemp)
                    } else {
                        articleDataArrayListTemp.clear()
                        articleDataArrayListTemp.addAll(articleDataArrayList)
                        homeAdapter.updateItems(articleDataArrayList)
                    }
                    return false
                }
            })
    }

    private fun showPaymentOptions() {
        val options = arrayOf("Bar", "Card", "Other")
        var selectedOption = ""

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Payment Option")
            .setSingleChoiceItems(
                options,
                options.indexOf(selectedPaymentOption)
            ) { _, which ->
                selectedOption = options[which]
            }
            .setPositiveButton("Select") { _, _ ->
                if (selectedOption.isNotEmpty()) {
                    selectedPaymentOption = selectedOption
                    handlePaymentOption(selectedOption)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handlePaymentOption(option: String) {
        // Seçilen ödeme seçeneğini işleyin (örneğin, bir şey yapabilirsiniz)

        when (option) {
            "Bar" -> {
                showAlertDialog()

            }

            "Card" -> {
                // 'Card' seçeneğine tıklanınca yapılacak işlemler
                // Örneğin, "Card" seçeneğine tıklandığında yapılacak işlemler buraya yazılabilir.

            }

            "Other" -> {
                // 'Other' seçeneğine tıklanınca yapılacak işlemler
                // Örneğin, "Other" seçeneğine tıklandığında yapılacak işlemler buraya yazılabilir.
            }
        }

        // Seçilen ödeme seçeneğini görsel olarak da güncelleyebilirsiniz
        binding.textViewTotal.text = "Selected Payment: $option"
    }

    private fun showAlertDialog() {
        updateOrInsertSellingProcesses()
        val customLayout =
            layoutInflater.inflate(R.layout.receipt_alert_dialog, binding.root, false)
        val alertBinding = ReceiptAlertDialogBinding.bind(customLayout)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(alertBinding.root)
        val billRecyclerView : RecyclerView = customLayout.findViewById(R.id.billAlert)
        val newList = articleSellList.map { BillAlertModel(it.name, it.price*it.quantity) }
        billAlertAdapter = BillAlertAdapter(newList)
        billRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = billAlertAdapter
        }
        builder.show()
    }


    private fun updateOrInsertSellingProcesses() {
        for(article in articleSellList){
            if (databaseHelper.readSellingProcess(article.productId.toString())) {
                databaseHelper.updateSellingProcess(article.productId.toString(),article.price.toString())
            }
            else {
                databaseHelper.insertSellingProcess(article.price.toString(),article.productId.toString(), article.quantity.toString())
            }
        }
    }

}


