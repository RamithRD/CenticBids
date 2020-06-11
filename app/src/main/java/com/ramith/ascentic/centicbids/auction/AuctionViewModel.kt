package com.ramith.ascentic.centicbids.auction

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ramith.ascentic.centicbids.model.AuctionItem

class AuctionViewModel() : ViewModel() {

    var auctionRepository = AuctionRepository()

    var auctionItemsListMutableLiveData : LiveData<List<AuctionItem>>? = null

    var auctionBidUpdateMutableLiveData : LiveData<Boolean>? = null


    fun getAllAuctionItems(){

        auctionItemsListMutableLiveData = auctionRepository.getAllAuctionItems()

    }

    fun updateBidForItem(auctionItem : AuctionItem){

        auctionBidUpdateMutableLiveData = auctionRepository.updateBidForItem(auctionItem)

    }

}