package com.ramith.ascentic.centicbids.auction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ramith.ascentic.centicbids.model.AuctionItem
import com.ramith.ascentic.centicbids.model.CenticBidsUser

class AuctionViewModel(application: Application?) : AndroidViewModel(application!!)  {

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