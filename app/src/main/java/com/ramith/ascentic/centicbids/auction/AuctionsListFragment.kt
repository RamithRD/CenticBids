package com.ramith.ascentic.centicbids.auction

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ramith.ascentic.centicbids.R
import com.ramith.ascentic.centicbids.adapters.AuctionItemsAdapter
import com.ramith.ascentic.centicbids.model.AuctionItem
import kotlinx.android.synthetic.main.fragment_auctions_list.*
import org.jetbrains.anko.alert


/**
 * A simple [Fragment] subclass.
 */
class AuctionsListFragment : Fragment(R.layout.fragment_auctions_list) , FirebaseAuth.AuthStateListener, AuctionItemsAdapter.OnAuctionItemClickListener {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser : FirebaseUser? = firebaseAuth.currentUser

    lateinit var auctionViewModel : AuctionViewModel

    var userMenuItem : Menu? = null

    var auctionItemsList :  List<AuctionItem>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        auctionViewModel = ViewModelProvider(this).get(AuctionViewModel::class.java)

        auctionViewModel.getAllAuctionItems()
        auctionViewModel.auctionItemsListMutableLiveData!!.observe(viewLifecycleOwner, Observer { auctionsList ->

            auctionItemsList = auctionsList
            Log.d("AUCTIONS_LIST", auctionsList.size.toString())

            val auctionsAdapter = AuctionItemsAdapter(auctionsList, this)
            auctionsListRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            auctionsListRecycler.adapter = auctionsAdapter

        })


    }

    override fun onAuthStateChanged(firebaseAuth : FirebaseAuth) {

        firebaseUser = firebaseAuth.currentUser

        if(firebaseUser == null){
            userMenuItem?.findItem(R.id.action_auth_logout)?.setVisible(false);
            userMenuItem?.findItem(R.id.action_auth_logout)?.setEnabled(false);
        } else {
            userMenuItem?.findItem(R.id.action_auth_logout)?.setVisible(true);
            userMenuItem?.findItem(R.id.action_auth_logout)?.setEnabled(true);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.logout_menu, menu)
        userMenuItem = menu

        val item = menu.findItem(R.id.action_auth_logout)

        //show hide logout action depending on user auth status
        if(firebaseUser == null){
            item?.setVisible(false);
            item.setEnabled(false);
        } else {
            item?.setVisible(true);
            item?.setEnabled(true);
        }

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_auth_logout -> {
            singOutFirebase()
            //hide ui
            true
        }

        R.id.action_user_profile -> {
            if(firebaseUser == null){
                showGotoLoginDialog()
            } else {
                showUserInfoDialog()
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun singOutFirebase() {
        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }

    private fun showUserInfoDialog() {

        activity?.alert("Logged in as ${firebaseUser!!.email}", "You can now bid on Auctions!") {
            positiveButton("Ok") {dialog ->
                dialog.dismiss()
            }
        }?.show()

    }

    private fun showGotoLoginDialog() {

        activity?.alert("You can bid on Auctions after you've signed in :)", "Hi! Please Sign In") {
            positiveButton("SIGN IN") {
                findNavController().navigate(R.id.action_auctionsListFragment_to_loginFragment)
            }
        }?.show()

    }

    override fun onAuctionItemClicked(position: Int) {

        Log.d("AUCTIONS_LIST", "position $position clicked")

       val action : AuctionsListFragmentDirections.ActionAuctionsListFragmentToAuctionDetailFragment  = AuctionsListFragmentDirections.actionAuctionsListFragmentToAuctionDetailFragment(
           auctionItemsList?.get(position)
       );

       findNavController().navigate(action)


    }

}
