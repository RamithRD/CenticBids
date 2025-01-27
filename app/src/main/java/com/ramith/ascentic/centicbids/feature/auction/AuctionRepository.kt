package com.ramith.ascentic.centicbids.feature.auction

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ramith.ascentic.centicbids.model.AuctionItem
import com.ramith.ascentic.centicbids.model.CenticBidsUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class AuctionRepository {

    private val firestoreDbRef = FirebaseFirestore.getInstance()
    private val auctionsCollectionRef = firestoreDbRef.collection("auction_items")

    //retrieves all the action auctions
    fun getAllAuctionItems() : MutableLiveData<List<AuctionItem>> {

        val auctionItemsList : MutableList<AuctionItem> = mutableListOf()

        val auctionItemsListMutableLiveData = MutableLiveData<List<AuctionItem>>()
        auctionsCollectionRef.addSnapshotListener {querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                //handle error
                return@addSnapshotListener
            }

            //no error if reached this line
            querySnapshot?.let { querySnapshot ->

                auctionItemsList.clear()

                for(document in querySnapshot){
                    val auctionItem = document.toObject(AuctionItem::class.java)
                    Log.d("AUC_DATA", document.getData().toString())
                    auctionItemsList += auctionItem
                }

                auctionItemsListMutableLiveData.postValue(auctionItemsList)

            }
        }

        return  auctionItemsListMutableLiveData

    }

    //place a bid on an item, updates the auction_item_id field in firestore with the latest bid amount
    //the cloud function will be listening to this update trigger and send notifications to all the users who have been outbid
    fun updateBidForItem(auctionItem : AuctionItem) : MutableLiveData<Boolean> {

        var updatedSuccessfully : Boolean = false

        val auctionBidUpdateMutableLiveData = MutableLiveData<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {

            val updateBidQuery = auctionsCollectionRef
                .whereEqualTo("auction_item_id", auctionItem.auction_item_id )
                .get()
                .await()

            if(updateBidQuery.documents.isNotEmpty()) {

                for(document in updateBidQuery){

                    try {

                        auctionsCollectionRef.document(document.id).set(
                            auctionItem,
                            SetOptions.merge()
                        ).await()

                        updatedSuccessfully = true

                        auctionBidUpdateMutableLiveData.postValue(updatedSuccessfully)



                    }catch (e: Exception){

                        updatedSuccessfully = false
                        auctionBidUpdateMutableLiveData.postValue(updatedSuccessfully)

                    }

                }

            }

        }

        return auctionBidUpdateMutableLiveData

    }


}