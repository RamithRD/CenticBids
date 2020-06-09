package com.ramith.ascentic.centicbids.auction

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.ramith.ascentic.centicbids.model.AuctionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class AuctionRepository {

    private val firestoreDbRef = FirebaseFirestore.getInstance()
    private val auctionsCollectionRef = firestoreDbRef.collection("auction_items")

    fun getAllAuctionItems() : MutableLiveData<List<AuctionItem>> {

        val auctionItemsList : MutableList<AuctionItem> = mutableListOf()

        val auctionItemsListMutableLiveData = MutableLiveData<List<AuctionItem>>()
        auctionsCollectionRef.addSnapshotListener {querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                //handle error
                return@addSnapshotListener
            }

            //no error if reached this line
            querySnapshot?.let {

                auctionItemsList.clear()

                for(document in it){
                    val auctionItem = document.toObject(AuctionItem::class.java)
                    auctionItemsList += auctionItem
                }

                auctionItemsListMutableLiveData.postValue(auctionItemsList)

            }
        }

        return  auctionItemsListMutableLiveData

    }


}