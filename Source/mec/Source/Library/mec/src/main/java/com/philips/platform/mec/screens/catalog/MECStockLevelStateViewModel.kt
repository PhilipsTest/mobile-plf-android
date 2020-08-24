package com.philips.platform.mec.screens.catalog

import androidx.annotation.IdRes
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.philips.platform.ecs.microService.model.filter.ECSStockLevel
import com.philips.platform.mec.R


class MECStockLevelStateViewModel    : ViewModel() {
    val checkBox1: ObservableBoolean? = ObservableBoolean()
    val checkBox2: ObservableBoolean? = ObservableBoolean()
    val checkBox3: ObservableBoolean? = ObservableBoolean()
    val validated = MutableLiveData<ArrayList<ECSStockLevel>>()

    var mStockList: ArrayList<ECSStockLevel> = ArrayList()
//    var checkBox1 = ObservableBoolean()
//    var checkBox2 = ObservableBoolean()
//    var checkBox3 = ObservableBoolean()


    val isCheckedCheckBox1 = MutableLiveData<Boolean>(false)
    val isCheckedCheckBox2 = MutableLiveData<Boolean>(false)
    val isCheckedCheckBox3 = MutableLiveData<Boolean>(false)


    private val checkBoxInStock = MutableLiveData<ECSStockLevel>(ECSStockLevel.InStock)
    private val checkBoxLowStock = MutableLiveData<ECSStockLevel>(ECSStockLevel.LowStock)
    private val checkBoxOutOfStock = MutableLiveData<ECSStockLevel>(ECSStockLevel.OutOfStock)

    fun onCheckedChanged(@IdRes resId: Int) {
        checkBoxInStock.value = ECSStockLevel.InStock
        checkBoxLowStock.value = ECSStockLevel.LowStock
        checkBoxOutOfStock.value = ECSStockLevel.LowStock

        when (resId) {
            R.id.mec_filter_checkbox1 -> isCheckedCheckBox1.value = isCheckedCheckBox1.value?.not()
            R.id.mec_filter_checkbox2 -> isCheckedCheckBox2.value = isCheckedCheckBox2.value?.not()
            R.id.mec_filter_checkbox3 -> isCheckedCheckBox3.value = isCheckedCheckBox3.value?.not()
        }
//        checkBox1 = isCheckedCheckBox1.value as ObservableBoolean
//        checkBox2 = isCheckedCheckBox2.value as ObservableBoolean
//        checkBox3 = isCheckedCheckBox3.value as ObservableBoolean

        if (resId == R.id.mec_filter_checkbox1)
            if (isCheckedCheckBox1.value!!)
                mStockList.add(ECSStockLevel.InStock)
            else
                mStockList.remove(ECSStockLevel.InStock)
        else if (resId == R.id.mec_filter_checkbox2)
            if (isCheckedCheckBox2.value!!)
                mStockList.add(ECSStockLevel.LowStock)
            else
                mStockList.remove(ECSStockLevel.LowStock)
        else if (resId == R.id.mec_filter_checkbox3)
            if (isCheckedCheckBox3.value!!)
                mStockList.add(ECSStockLevel.OutOfStock)
            else
                mStockList.remove(ECSStockLevel.OutOfStock)


        checkBox1?.set(isCheckedCheckBox1.value!!)
        checkBox2?.set(isCheckedCheckBox2.value!!)
        checkBox3?.set(isCheckedCheckBox3.value!!)

        checkBox1?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable?, i: Int) {
                if (checkBox1.get()) {
                    checkBox1.set(mStockList.contains(ECSStockLevel.InStock))

                }
            }
        })
        checkBox2?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable?, i: Int) {
                if (checkBox2.get()) {
                    checkBox2.set(mStockList.contains(ECSStockLevel.LowStock))
                }
            }
        })
        checkBox3?.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable?, i: Int) {
                if (checkBox3.get()) {
                    checkBox3.set(mStockList.contains(ECSStockLevel.OutOfStock))
                }
            }
        })
        checkBox1?.set(mStockList.contains(ECSStockLevel.InStock))
        checkBox2?.set(mStockList.contains(ECSStockLevel.LowStock))
        checkBox3?.set(mStockList.contains(ECSStockLevel.OutOfStock))


    }

    fun onClickValidateButton() {
//        if (!isValid()) {
//            checkBox1Color.value = ECSStockLevel.InStock
//            checkBox2Color.value = ECSStockLevel.OutOfStock
//            return
//        }
        validated.value = mStockList
    }


    private fun isValid() = isCheckedCheckBox1.value == true && isCheckedCheckBox2.value == true
}