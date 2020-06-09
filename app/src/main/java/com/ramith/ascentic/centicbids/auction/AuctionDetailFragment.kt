package com.ramith.ascentic.centicbids.auction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.ramith.ascentic.centicbids.R

/**
 * A simple [Fragment] subclass.
 */
class AuctionDetailFragment : Fragment(R.layout.fragment_auction_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : AuctionDetailFragmentArgs = AuctionDetailFragmentArgs.fromBundle(requireArguments())
        val auctionItem = args.auctionItem

        Log.d("SAFE_ARGS", auctionItem!!.auction_title)




    }

}
