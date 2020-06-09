package com.ramith.ascentic.centicbids.auction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import coil.api.load
import coil.request.CachePolicy
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.model.AuctionItem
import kotlinx.android.synthetic.main.fragment_auction_detail.*
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class AuctionDetailFragment : Fragment(R.layout.fragment_auction_detail) {

    private var newBidValue : Int = 0

    var auctionItem : AuctionItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : AuctionDetailFragmentArgs = AuctionDetailFragmentArgs.fromBundle(requireArguments())
        auctionItem = args.auctionItem

        Log.d("SAFE_ARGS", auctionItem!!.bidding_history.toString())

        initUi()

    }

    private fun initUi(){

        newBidValue = auctionItem?.latest_bid!!.toInt()
        newBidValueTxt.text = newBidValue.toString()

        decrementBidValueBtn.isEnabled = false

        auctionItemDetailTitleTxt.text = auctionItem?.auction_title
        auctionItemDetailDescTxt.text = auctionItem?.auction_desc

        auctionItemDetailsImg.load(auctionItem?.item_img_url){
            memoryCachePolicy(CachePolicy.ENABLED)
        }

        decrementBidValueBtn.setOnClickListener {

            if(newBidValue <= auctionItem?.latest_bid!!.toInt()){
                decrementBidValueBtn.isEnabled = false
            } else {
                newBidValue -= 100
                newBidValueTxt.text = newBidValue.toString()
            }

        }

        incrementBidValueBtn.setOnClickListener {

            decrementBidValueBtn.isEnabled = true
            newBidValue += 100
            newBidValueTxt.text = newBidValue.toString()

        }

    }

}
