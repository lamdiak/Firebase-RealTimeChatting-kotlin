package com.example.firebasetypeab

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasechat_typek.CustomAdapter
import com.example.firebasechat_typek.FireHelper
import com.example.firebasechat_typek.FriendlyMessage
import com.google.firebase.database.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener{


    private var mAdapter: CustomAdapter? = null

    private var database: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    private var fromUseridentify: String? = null
    private var mFMessages: ArrayList<FriendlyMessage>? = null
    private var currentUser: String? = null

    private var mRecyclerView: RecyclerView? = null
    private var msgBtn: ImageButton? = null

    private var msgText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        msgBtn = findViewById(R.id.msgsend)
        msgBtn!!.setOnClickListener(this)

        msgText = findViewById(R.id.msgmessgaeedit)

        mRecyclerView = findViewById(R.id.recyclerview) as RecyclerView
        val mLinearLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.setLayoutManager(mLinearLayoutManager)

        val user = FireHelper.getCurrentUser()
        user.getDisplayName()
        user.getEmail()

        currentUser = subStringName(user.email!!)

        fromUseridentify = user.uid

        mFMessages = ArrayList()

        // Write a message to the database
        database = FirebaseDatabase.getInstance()
        myRef = database!!.getReference("message")

        mHandler = Handler()
        startRepeatingTask()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopRepeatingTask()
    }

    private fun getTimeStamp(): String {
        val tsLong = System.currentTimeMillis() / 1000
        return tsLong.toString()
    }

    fun updateFetchMessage() {
        fetchMessage()
    }

    private val mInterval = 1000 // 1 seconds by default, can be changed later
    private var mHandler: Handler? = null

    internal var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {

                updateFetchMessage()

            } finally {

                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    internal fun startRepeatingTask() {
        mStatusChecker.run()
    }

    internal fun stopRepeatingTask() {
        mHandler!!.removeCallbacks(mStatusChecker)
    }

    fun fetchMessage() {

        database!!.getReference().child("message").addListenerForSingleValueEvent(
            object : ValueEventListener {


                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    mFMessages = ArrayList()

                    for (ds in dataSnapshot.children) {
                        val fromUserId = ds.child("fromUserId").getValue(String::class.java)
                        val name = ds.child("name").getValue(String::class.java)
                        val text = ds.child("text").getValue(String::class.java)
                        val timestamp = ds.child("timeStamp").getValue(String::class.java)
                        Log.d("TAG", "$fromUserId / $name / $text / $timestamp")
                        mFMessages!!.add(FriendlyMessage(text, name, timestamp, fromUserId))
                    }

                    if (mFMessages!!.size > 0) {

                        mAdapter = CustomAdapter(mFMessages!!, fromUseridentify!!)
                        mRecyclerView!!.setAdapter(mAdapter)
                        mRecyclerView!!.scrollToPosition(mRecyclerView!!.getAdapter()!!.itemCount - 1)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("", "getUser:onCancelled", databaseError.toException())
                }


            })


    }

    private fun subStringName(str: String): String {

        val subString = str.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            ?: return ""

        return subString[0]
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.msgsend -> {


                if (msgText!!.getText() == null || msgText!!.getText().length < 0) return

                val friendlyMessage = FriendlyMessage(
                    msgText!!.getText().toString().trim { it <= ' ' },
                    currentUser,
                    getTimeStamp(),
                    fromUseridentify
                )
                myRef!!.push().setValue(friendlyMessage).addOnSuccessListener {
                    // Write was successful!
                    // ...
                    Log.w("", " Write was successful!")

//                    if (mAdapter == null) {
//                        mAdapter = CustomAdapter(mFMessages!!, fromUseridentify!!)
//                        mRecyclerView!!.setAdapter(mAdapter)
//                        mRecyclerView!!.scrollToPosition(mRecyclerView!!.getAdapter()!!.itemCount - 1)
//                    } else {
//
//                        mFMessages!!.add(
//                            FriendlyMessage(
//                                msgText!!.getText().toString().trim { it <= ' ' },
//                                currentUser,
//                                fromUseridentify,
//                                getTimeStamp()
//                            )
//                        )
//                        mAdapter!!.notifyItemRangeChanged(mFMessages!!.size, 1)
//                        mRecyclerView!!.scrollToPosition(mRecyclerView!!.getAdapter()!!.itemCount - 1)
//                    }
                }.addOnFailureListener {
                    // Write failed
                    // ...
                    Log.w("", " Write was failed!")
                }
            }
        }
    }

}