package com.ramith.ascentic.centicbids.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AuctionItem  (

    var auction_item_id : String = "",
    var auction_title : String = "",
    var auction_desc : String = "",
    var item_img_url : String = "",
    var base_price : Float = 0.0F,
    var latest_bid : Float = 0.0F,
    var latest_bid_uid : String = "",
    var bidding_history: List<Map<String, String>>? = null

) : Parcelable