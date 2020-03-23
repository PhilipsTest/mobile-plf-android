package com.philips.cdp.di.mec.screens.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.philips.cdp.di.ecs.model.asset.Asset
import com.philips.cdp.di.mec.databinding.MecImagePagerItemBinding

class ImageAdapter(val assets: List<Asset>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return assets.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        var binding = MecImagePagerItemBinding.inflate(inflater)
        val asset = assets.get(position)

        binding.asset = asset
        binding.executePendingBindings()
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return assets.get(position % assets.size).asset
    }

}