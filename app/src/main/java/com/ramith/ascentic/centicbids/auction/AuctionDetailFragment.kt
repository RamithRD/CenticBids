package com.ramith.ascentic.centicbids.auction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import coil.request.CachePolicy
import com.google.firebase.auth.FirebaseAuth
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.model.AuctionItem
import kotlinx.android.synthetic.main.fragment_auction_detail.*
import org.jetbrains.anko.indeterminateProgressDialog
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class AuctionDetailFragment : Fragment(R.layout.fragment_auction_detail) {

    private var newBidValue : Int = 0

    var auctionItem : AuctionItem? = null

    lateinit var auctionViewModel : AuctionViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args : AuctionDetailFragmentArgs = AuctionDetailFragmentArgs.fromBundle(requireArguments())
        auctionItem = args.auctionItem

        auctionViewModel = ViewModelProvider(this).get(AuctionViewModel::class.java)

        Log.d("SAFE_ARGS", auctionItem!!.bidding_history.toString())
        Log.d("AUCTION", "Bidding history " +auctionItem!!.bidding_history.toString())

        initUI()
        setupBiddingMechanism()

    }

    private fun setupBiddingMechanism(){

        newBidValue = getCurrentBidValue().toInt()
        newBidValueTxt.text = newBidValue.toString()

        decrementBidValueBtn.isEnabled = false

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

        placeBidBtn.setOnClickListener {

            if(auctionItem!!.latest_bid_uid == FirebaseAuth.getInstance().currentUser?.uid){
                Toast.makeText(requireContext(), "You're the current highest bidder!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auctionItem!!.latest_bid = newBidValue.toFloat()
            auctionItem!!.latest_bid_uid = FirebaseAuth.getInstance().currentUser!!.uid

            auctionItem!!.bidding_history = auctionItem!!.bidding_history?.plus(
                mapOf(
                    Pair("user_email", FirebaseAuth.getInstance().currentUser!!.email.toString()),
                    Pair("user_id", FirebaseAuth.getInstance().currentUser!!.uid),
                    Pair("timestamp", System.currentTimeMillis().toString())
                )
            )

            val progressDialog = activity?.indeterminateProgressDialog(message = "Placing new bid...", title = "CenticBids")
            auctionViewModel.updateBidForItem(auctionItem!!)
            auctionViewModel.auctionBidUpdateMutableLiveData!!.observe(viewLifecycleOwner, Observer { isUpdated ->

                if(isUpdated){
                    Toast.makeText(requireContext(), "Bid successfully placed, you're now the highest bidder!", Toast.LENGTH_SHORT).show()
                    initUI()
                } else {
                    Toast.makeText(requireContext(), "An Error Occured!", Toast.LENGTH_SHORT).show()
                }

                progressDialog?.dismiss()

            })


        }

    }

    private fun initUI(){

        auctionItemDetailTitleTxt.text = auctionItem?.auction_title
        auctionItemDetailDescTxt.text = auctionItem?.auction_desc
        currentHighBidTxt.text = auctionItem?.latest_bid.toString()
        itemBasePriceTxt.text = auctionItem?.base_price.toString()

        if(auctionItem!!.latest_bid_uid == FirebaseAuth.getInstance().currentUser?.uid){
            currentStatusDescTxt.text = "You're currently the highest bidder! Yay!"
        } else {
            currentStatusDescTxt.text = "Place a bid to make this item yours!"
        }

        auctionItemDetailsImg.load(auctionItem?.item_img_url){
            memoryCachePolicy(CachePolicy.ENABLED)
        }

    }

    //Checks if this auction item is new, if it's new the bid value starts from the base price
    private fun getCurrentBidValue() : Float {

        if(auctionItem!!.latest_bid < auctionItem!!.base_price){
            return auctionItem!!.base_price
        }

        return auctionItem!!.latest_bid

    }

}
