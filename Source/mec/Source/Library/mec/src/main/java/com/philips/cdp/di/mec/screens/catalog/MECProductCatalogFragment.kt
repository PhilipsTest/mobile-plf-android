package com.philips.cdp.di.mec.screens.catalog


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
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
import android.widget.CompoundButton
import com.philips.cdp.di.ecs.model.products.ECSProducts

import com.philips.cdp.di.mec.activity.MecError
import com.philips.cdp.di.mec.databinding.MecCatalogFragmentBinding
import com.philips.cdp.di.mec.screens.InAppBaseFragment


/**
 * A simple [Fragment] subclass.
 */
class MECProductCatalogFragment : InAppBaseFragment(),Observer<MutableList<ECSProducts>> {

    var totalPages:Int = 0
    var currentPage:Int = 0
    var pageSize:Int = 8

    override fun onChanged(ecsProductsList:MutableList<ECSProducts>?) {

      System.out.println("Size of products"+ (ecsProductsList?.size ?: 0))

        totalPages = ecsProductsList?.get(0)?.pagination?.totalPages ?: 0

        if (ecsProductsList != null) {
            for (ecsProducts in ecsProductsList){


                for(ecsProduct in ecsProducts.products){

                    mecProductList.add(MECProduct(ecsProduct.code, ecsProduct.summary.price.formattedDisplayPrice, ecsProduct.summary.imageURL, ecsProduct.summary.productTitle))

                }
            }
        }

        adapter.notifyDataSetChanged()

    }

    private lateinit var adapter: MECProductCatalogBaseAbstractAdapter
    val TAG = MECProductCatalogFragment::class.java.name

    lateinit var ecsProductViewModel :EcsProductViewModel


    lateinit var mecProductList : MutableList<MECProduct>

    private lateinit var binding: MecCatalogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = MecCatalogFragmentBinding.inflate(inflater, container, false)

        binding.fragment = this

        ecsProductViewModel = ViewModelProviders.of(this).get(EcsProductViewModel::class.java)

        ecsProductViewModel.ecsProductsList.observe(this, this);

        ecsProductViewModel.mecError.observe(this, object :Observer<MecError>{

            override fun onChanged(mecError: MecError?) {
                System.out.println("Error while  fetching")

            }
        })

        ecsProductViewModel.init(currentPage,pageSize);

        mecProductList = mutableListOf<MECProduct>()

        binding.mecSearchBox.searchTextView.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

        })


        binding.productCatalogRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(currentPage<totalPages) {
                    currentPage++
                    ecsProductViewModel.init(currentPage, pageSize)
                }
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
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

    fun onLayoutChanged(compoundButton: CompoundButton,isChecked: Boolean) {

        if(isChecked){
            adapter = MECProductCatalogGridAdapter(mecProductList)
            binding.productCatalogRecyclerView.layoutManager = GridLayoutManager(activity, 2)

        }else{
            adapter = MECProductCatalogListAdapter(mecProductList)
            binding.productCatalogRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL ,false)
        }
        binding.productCatalogRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}
