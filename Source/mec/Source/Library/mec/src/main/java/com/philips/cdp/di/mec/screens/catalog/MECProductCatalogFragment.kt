package com.philips.cdp.di.mec.screens.catalog


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.philips.cdp.di.ecs.model.products.ECSProducts

import com.philips.cdp.di.mec.activity.MecError
import com.philips.cdp.di.mec.databinding.MecCatalogFragmentBinding

import android.support.v7.widget.DefaultItemAnimator
import android.util.Log
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.screens.MecBaseFragment
import kotlinx.android.synthetic.main.mec_main_activity.*


/**
 * A simple [Fragment] subclass.
 */
open class MECProductCatalogFragment : MecBaseFragment(),Pagination,Observer<MutableList<ECSProducts>> {
    override fun isPaginationSupported(): Boolean {
        return true
    }

    private lateinit var mecCatalogUIModel: MECCatalogUIModel
    val TAG = MECProductCatalogFragment::class.java.name

    var totalPages: Int = 0
    var currentPage: Int = 0
    var pageSize: Int = 8

    override fun onChanged(ecsProductsList: MutableList<ECSProducts>?) {
        hideProgressBar()

        totalPages = ecsProductsList?.get(0)?.pagination?.totalPages ?: 0

       currentPage = ecsProductsList?.get(0)?.pagination?.currentPage ?: 0

        currentPage++


        if (ecsProductsList != null) {
            for (ecsProducts in ecsProductsList) {

                for (ecsProduct in ecsProducts.products) {
                    mecProductList.add(MECProduct(ecsProduct.code, ecsProduct.summary.price.formattedDisplayPrice, ecsProduct.summary.imageURL, ecsProduct.summary.productTitle))
                }
            }
        }
        binding.fragment = this
        currentPage++

        mecCatalogUIModel.isEmptyView = mecProductList.isEmpty()
        binding.uiModel = mecCatalogUIModel
        adapter.notifyDataSetChanged()

    }

    private lateinit var adapter: MECProductCatalogBaseAbstractAdapter


    lateinit var ecsProductViewModel: EcsProductViewModel


    lateinit var mecProductList: MutableList<MECProduct>

    private lateinit var binding: MecCatalogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {



        binding = MecCatalogFragmentBinding.inflate(inflater, container, false)

        mecCatalogUIModel = MECCatalogUIModel()
        binding.uiModel = mecCatalogUIModel


        ecsProductViewModel = ViewModelProviders.of(this).get(EcsProductViewModel::class.java)

        ecsProductViewModel.ecsProductsList.observe(this, this);

        val bundle = arguments


        binding.mecGrid.setOnClickListener {
            binding.mecGrid.setBackgroundColor(Color.parseColor("#DCDCDC"))
            binding.mecList.setBackgroundColor(Color.parseColor("#ffffff"))
            adapter = MECProductCatalogGridAdapter(mecProductList)
            binding.productCatalogRecyclerView.layoutManager = GridLayoutManager(activity, 2)
            binding.productCatalogRecyclerView.adapter = adapter
            binding.productCatalogRecyclerView.setItemAnimator(DefaultItemAnimator())
            val Hdivider = DividerItemDecoration(binding.productCatalogRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL)
            val Vdivider = DividerItemDecoration(binding.productCatalogRecyclerView.getContext(), DividerItemDecoration.VERTICAL)
            binding.productCatalogRecyclerView.addItemDecoration(Hdivider)
            binding.productCatalogRecyclerView.addItemDecoration(Vdivider)
            adapter.notifyDataSetChanged()
        }

        binding.mecList.setOnClickListener {
            binding.mecList.setBackgroundColor(Color.parseColor("#DCDCDC"))
            binding.mecGrid.setBackgroundColor(Color.parseColor("#ffffff"))
            adapter = MECProductCatalogListAdapter(mecProductList)
            binding.productCatalogRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.productCatalogRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        ecsProductViewModel.mecError.observe(this, object : Observer<MecError> {

            override fun onChanged(mecError: MecError?) {
                binding.mecProductCatalogEmptyTextLabel.visibility = View.VISIBLE
                binding.productCatalogRecyclerView.visibility = View.GONE

            }
        })




        mecProductList = mutableListOf<MECProduct>()

        binding.mecSearchBox.setSearchBoxHint("Search")
        binding.mecSearchBox.setDecoySearchViewHint("Search")

        binding.mecSearchBox.searchTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

        })


        binding.productCatalogRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)


                if(shouldFetchNextPage())
                    executeRequest()

            }
        })

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        executeRequest()
    }
    override fun onResume() {
        super.onResume()
        setTitleAndBackButtonVisibility(R.string.mec_product_catalog, true)
    }

    override fun handleBackEvent(): Boolean {
        return super.handleBackEvent()
    }


    public fun createInstance(args: Bundle): MECProductCatalogFragment {
        val fragment = MECProductCatalogFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = MECProductCatalogListAdapter(mecProductList)

        binding.productCatalogRecyclerView.adapter = adapter

        binding.productCatalogRecyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

    }


    private fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

    open fun executeRequest(){
        createCustomProgressBar(container, MEDIUM)
        ecsProductViewModel.init(currentPage, pageSize)
    }


    fun shouldFetchNextPage(): Boolean{

        if(!isPaginationSupported()){
            return false
        }
        val lay = binding.productCatalogRecyclerView
                .layoutManager as LinearLayoutManager

        if (isScrollDown(lay)) {
            if (currentPage < totalPages) {
              return true
            }
        }

        return false;
    }

    open fun enableLoadMore() : Boolean{
        return false
    }
}


