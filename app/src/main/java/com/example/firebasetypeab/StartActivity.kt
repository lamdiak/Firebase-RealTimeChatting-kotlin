package com.example.firebasechat_typek


import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetypeab.MainActivity
import com.example.firebasetypeab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList
import java.util.HashMap

class StartActivity : AppCompatActivity(), View.OnClickListener {

    private val mArrayList: ArrayList<Message>? = null
    private val mAdapter: CustomAdapter? = null
    private val count = -1

    private var dict1: HashMap<String, String>? = null
    private var dict2: HashMap<String, String>? = null
    private var dict3: HashMap<String, String>? = null
    private var dict4: HashMap<String, String>? = null
    private var dict5: HashMap<String, String>? = null

    private var arrMap: ArrayList<HashMap<String, String>>? = null

    private var startBtn: Button? = null
    private var disBtn: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        disBtn = this.findViewById(R.id.disbutton)
        disBtn!!.setOnClickListener(this)


        startBtn = findViewById(R.id.startbutton)
        startBtn!!.setOnClickListener(this)
        initializeMapData()
        mAuth = FireHelper.AuthInit()

    }

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            R.id.disbutton -> {
                val builderSingle = AlertDialog.Builder(this@StartActivity)
//                builderSingle.setIcon(R.drawable.ic_send_black_24dp);
                builderSingle.setTitle("Select!!..")

                val arrayAdapter = ArrayAdapter<String>(
                    this@StartActivity,
                    android.R.layout.select_dialog_singlechoice
                )
                arrayAdapter.add("bill")
                arrayAdapter.add("john")
                arrayAdapter.add("babarian")
                arrayAdapter.add("lara")
                arrayAdapter.add("nilson")

                builderSingle.setNegativeButton(
                    "cancel"
                ) { dialog, which -> dialog.dismiss() }

                builderSingle.setAdapter(
                    arrayAdapter
                ) { dialog, which ->
                    val strName = arrayAdapter.getItem(which)

                    val mapSelect = arrMap!!.get(which)

                    val str = mapSelect["email"]!!.trim { it <= ' ' }
                    val email = mapSelect["email"]!!.trim { it <= ' ' }

                    disBtn!!.setText(str)

                    signIn(email, "123456")
                }
                builderSingle.show()
            }

            else -> null
        }
    }

    fun initializeMapData() {

        arrMap = ArrayList()

        dict1 = HashMap()
        dict1!!["name"] = "bill"
        dict1!!["email"] = "bill@fbase.com"
        dict1!!["uid"] = "9D6gcyWbxtOq5CVC5m6ZIisjUsE3"

        arrMap!!.add(0, dict1!!)

        dict2 = HashMap()
        dict2!!["name"] = "john"
        dict2!!["email"] = "john@fbase.com"
        dict2!!["uid"] = "0DJvzYtd9LYaXqki9I31RpJfE1c2"

        arrMap!!.add(1, dict2!!)

        dict3 = HashMap()
        dict3!!["name"] = "babarian"
        dict3!!["email"] = "babarian@fbase.com"
        dict3!!["uid"] = "27gYiT7F4EZgJnZ6wzf4C2ulaci1"

        arrMap!!.add(2, dict3!!)

        dict4 = HashMap()
        dict4!!["name"] = "lara"
        dict4!!["email"] = "lara@fbase.com"
        dict4!!["uid"] = "HnDbDnRvoZhr6UVAOQG1WJr8mqa2"

        arrMap!!.add(3, dict4!!)

        dict5 = HashMap()
        dict5!!["name"] = "nilson"
        dict5!!["email"] = "nilson@fbase.com"
        dict5!!["uid"] = "lsAgtM3SL1ddffSMsV1s2XHiATo2"

        arrMap!!.add(4, dict5!!)

    }

    private fun subStringName(str: String): String {

        val subString = str.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            ?: return ""

        return subString[0]
    }

    private fun signIn(email:String, password:String) {
        Log.d("", "signIn:$email")

        // [START sign_in_with_email]
        val authResultTask = mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("", "signInWithEmail:success")


            // Write a message to the database
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("chat")

            val friendlyMessage = FChat(email, subStringName(email), mAuth!!.getCurrentUser()!!.uid)
            myRef.push().setValue(friendlyMessage)


            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()

            }
            else {
                // If sign in fails, display a message to the user.
                Log.w("", "signInWithEmail:failure", task.exception)
                Toast.makeText(this@StartActivity, "   " + task.exception + "   Authentication failed.", Toast.LENGTH_SHORT).show()
            }

            // [START_EXCLUDE]
            if (!task.isSuccessful) {

            }

            // [END_EXCLUDE]
        }


        // [END sign_in_with_email]
    }
}