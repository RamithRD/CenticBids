package com.ramith.ascentic.centicbids

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 */
class AuctionsListFragment : Fragment(R.layout.fragment_auctions_list) , FirebaseAuth.AuthStateListener {

    private val firebaseAuth = FirebaseAuth.getInstance()

    var userMenuItem : Menu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAuthStateChanged(firebaseAuth : FirebaseAuth) {

        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //hide ui components like logout, email address etc
            Toast.makeText(activity, "LOGGED OUT!", Toast.LENGTH_LONG).show()
//            userMenuItem.findItem(R.id.action_user_profile).setVisible(false);
//            userMenuItem.findItem(R.id.action_user_profile).setEnabled(false);

            userMenuItem?.findItem(R.id.action_auth_logout)?.setVisible(false);
            userMenuItem?.findItem(R.id.action_auth_logout)?.setEnabled(false);
        } else {
            //add logout button
            Toast.makeText(activity, "LOGGED In!", Toast.LENGTH_LONG).show()
//            userMenuItem.findItem(R.id.action_user_profile).setVisible(true);
//            userMenuItem.findItem(R.id.action_user_profile).setEnabled(true);

            userMenuItem?.findItem(R.id.action_auth_logout)?.setVisible(true);
            userMenuItem?.findItem(R.id.action_auth_logout)?.setEnabled(true);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.logout_menu, menu)
        userMenuItem = menu
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_auth_logout -> {
            singOutFirebase()
            //hide ui
            true
        }

        R.id.action_user_profile -> {
            val firebaseUser = firebaseAuth.currentUser
            if(firebaseUser == null){
                findNavController().navigate(R.id.action_auctionsListFragment_to_loginFragment)
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

}
