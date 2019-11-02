package com.example.firebasechat_typek

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


object FireHelper {

    private var mAuth: FirebaseAuth? = null
    private var mUser: FirebaseUser? = null

    fun AuthInit(): FirebaseAuth {

        mAuth = FirebaseAuth.getInstance()

        return mAuth!!

    }

    fun getCurrentUser(): FirebaseUser {
        mUser = FirebaseAuth.getInstance().currentUser
        return mUser!!
    }

}