package com.ramith.ascentic.centicbids.auction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ramith.ascentic.centicbids.model.AuctionItem

class AuctionViewModel(application: Application?) : AndroidViewModel(application!!)  {

    var auctionRepository = AuctionRepository()

    var auctionItemsListMutableLiveData : LiveData<List<AuctionItem>>? = null


    fun getAllAuctionItems(){

        auctionItemsListMutableLiveData = auctionRepository.getAllAuctionItems()

    }

}