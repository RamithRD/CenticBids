package com.ramith.ascentic.centicbids.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.CachePolicy
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.model.AuctionItem
import kotlinx.android.synthetic.main.list_auction_item.view.*
/**
 * RecyclerView Adapter and Viewholder class implementation for the active auctions list
 */
class AuctionItemsAdapter(var auctionItemsList : List<AuctionItem>, var onAuctionItemClickListener : OnAuctionItemClickListener) : RecyclerView.Adapter<AuctionItemsAdapter.AuctionItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_auction_item, parent, false)
        return AuctionItemsViewHolder(view, onAuctionItemClickListener)
    }

    override fun getItemCount(): Int {
        return auctionItemsList.size
    }

    //setting
    override fun onBindViewHolder(holder: AuctionItemsViewHolder, position: Int) {

        val currentAuctionItem = auctionItemsList[position]

        holder.itemView.auctionItemListNameTxt.text = currentAuctionItem.auction_title
        holder.itemView.auctionItemListDescTxt.text = currentAuctionItem.auction_desc

        holder.itemView.auctionItemListTimeTxt.text = "Remaining Time : ${currentAuctionItem.auction_remaining_time}"
        holder.itemView.auctionItemListBasePriceTxt.text = "Base Price : Rs.${currentAuctionItem.base_price}"
        holder.itemView.auctionItemListHighBidTxt.text = "Highest Bid : Rs.${currentAuctionItem.latest_bid}"

        holder.itemView.auctionItemListImg.load(currentAuctionItem?.item_img_url){
            memoryCachePolicy(CachePolicy.ENABLED)
        }


    }

    //viewholder impl for recycler view adapter
    class AuctionItemsViewHolder(itemView : View, var onAuctionItemClickListener : OnAuctionItemClickListener) : RecyclerView.ViewHolder(itemView) , View.OnClickListener {

        override fun onClick(p0: View?) {
            onAuctionItemClickListener.onAuctionItemClicked(adapterPosition)
        }

        init {
            itemView.auctionItemMoreDetailsBtn.setOnClickListener(this)
        }


    }


    //onclick item callback
    interface OnAuctionItemClickListener{

        fun onAuctionItemClicked(position : Int)

    }

}