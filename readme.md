# CenticBids Mobile App

## App Features :

- Users can view all ongoing auctions (both authenticated and unauthenticated are able to)
- An auction item consists of the following attributes:
   1. Title
   2. Description
   3. Base price
   4. Lastest bid
   5. Auction item image
   6. Remianing time
   7. Bid now button    
- An authenticated user can place a bid on an item in the item details page
- Users who have already bid on a specific item will receive a notification (FCM push messsage) when they have been outbid
  
## App Architecture :

The application architecture follows the MVVM pattern (using ViewModels and LiveData) together with the repository pattern.

Since the app is using Firebase as the backend, the repository doesn't need to implement any caching mechanisms or communiate with a remote data source since Firebase SDK provides everything to effectively cache and manipulate data.

### Architecture diagram :

![alt text](doc_assets/centic_bid_app_arch.png "CenticBids App architecture")

### Top-level Navigation Graph :

The application consists of 4 fragments :

1. AuctionsList fragment (start)
2. AuctionDetail fragment
3. Login fragment
4. Register fragment

![alt text](doc_assets/navigation_graph.png "CenticBids App architecture")

## Firestore Schema :

