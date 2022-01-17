package com.example.orderrawmaterials

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.orderrawmaterials.databinding.ActivityMainBinding
import com.example.orderrawmaterials.models.Data
import com.example.orderrawmaterials.models.User
import com.example.orderrawmaterials.utils.FirebaseService
import com.example.orderrawmaterials.utils.SharedPref
import com.example.orderrawmaterials.utils.Statusbar
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var users_reference: DatabaseReference
    private lateinit var admins_reference: DatabaseReference
    private lateinit var rektor_reference: DatabaseReference
    private lateinit var reference: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var m1 = arrayListOf("user", "admin", "rektor")
    private val TAG = "MainActivity"
    private lateinit var m2: ArrayList<String>
    private var isDirector = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPref.getInstanceDis(this)
        Statusbar.startStatus(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        users_reference = firebaseDatabase.getReference("Users")
        admins_reference = firebaseDatabase.getReference("Admins")
        rektor_reference = firebaseDatabase.getReference("Directors")
        reference = firebaseDatabase.getReference("Director")

        rektor_reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                m2 = ArrayList()
                val children = snapshot.children
                m2.clear()
                for (i in children) {
                    m2.add(i.value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        if (firebaseAuth.currentUser != null) {
            when (SharedPref.user) {
                "1" -> {
                    val intent = Intent(this, UserPanel::class.java)//UserPanel
                    startActivity(intent)
                }
                "2" -> {
                    val intent = Intent(this, AdminPanel::class.java)
                    startActivity(intent)
                    Log.d(TAG, "onCreate: ${firebaseAuth.currentUser!!.uid}")
                }
                "3" -> {
                    val intent = Intent(this, DirectorView::class.java)
                    startActivity(intent)
                }
            }
            finish()
        }
        binding.google.setOnClickListener {
            signIn()
        }

        binding.btn.setOnClickListener {
            Toast.makeText(this, "${binding.spinner.selectedItemPosition}", Toast.LENGTH_SHORT)
                .show()
            if (binding.name.text.isNotEmpty() && binding.lastName.text.toString()
                    .isNotEmpty() && binding.phoneInput.text.toString().length > 7
            ) {
                val name = binding.name.text.toString()
                val last_name = binding.lastName.text.toString()
                if (binding.spinner.selectedItemPosition == 2) {
                    if (m2.contains(binding.phoneInput.text.toString())) {
                        sendCode(binding.phoneInput.text.toString())
                        Toast.makeText(this, "SMS yuborildi", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Director emassiz", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "SMS yuborildi", Toast.LENGTH_SHORT).show()
                    sendCode(binding.phoneInput.text.toString())
                }
                bindProgressButton(binding.btn)
                binding.btn.attachTextChangeAnimator()
                binding.btn.showProgress {
                    buttonTextRes = R.string.loading
                    progressColor = Color.WHITE
                }
                binding.btn.isCheckable = false
            } else {
                Toast.makeText(this, "Iltimos malumotlarni kiriting!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            m1
        )
        binding.spinner.adapter = arrayAdapter
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Log.d(TAG, "signInWithCredential:success")
                    val name = binding.name.text.toString()
                    val last_name = binding.lastName.text.toString()
                    addUser(name, last_name)
                    if (binding.spinner.selectedItemPosition == 0) {
                        val intent = Intent(this, UserPanel::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, AdminPanel::class.java)
                        startActivity(intent)
                        SharedPref.user = name
                    }
                    finish()
                    val user = task.result?.user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:${R.string.kirish}", task.exception)

                }
            }
    }

    private fun addUser(name: String, lastName: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                var firebaseToken = it.result.toString()
                when (binding.spinner.selectedItemPosition) {
                    0 -> {
                        val user = User(
                            name,
                            lastName,
                            m1[binding.spinner.selectedItemPosition],
                            firebaseAuth.currentUser!!.uid,
                            firebaseToken
                        )
                        users_reference.child(firebaseAuth.currentUser!!.uid).setValue(user)
//                firebaseAuth.currentUser.uid
                    }
                    1 -> {
                        val user = User(
                            name,
                            lastName,
                            m1[binding.spinner.selectedItemPosition],
                            firebaseAuth.currentUser!!.uid,
                            firebaseToken
                        )
                        admins_reference.child(firebaseAuth.currentUser!!.uid).setValue(user)
                    }
                    else -> {
                        val user =
                            User(
                                name,
                                lastName,
                                m1[binding.spinner.selectedItemPosition],
                                firebaseAuth.currentUser!!.uid,
                                firebaseToken
                            )
                        val key = reference.push().key
                        reference.child(key!!).setValue(user)
                    }
                }
            }
        }
    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.btn.hideProgress(R.string.kirish)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val name = binding.name.text.toString()
                    val last_name = binding.lastName.text.toString()
                    addUser(name, last_name)
                    when (binding.spinner.selectedItemPosition) {
                        0 -> {
                            val intent = Intent(this, UserPanel::class.java)
                            startActivity(intent)
                            SharedPref.user = "1"
                        }
                        1 -> {
                            val intent = Intent(this, AdminPanel::class.java)
                            startActivity(intent)
                            SharedPref.user = "2"
                        }
                        2 -> {
                            val intent = Intent(this, DirectorView::class.java)
                            startActivity(intent)
                            SharedPref.user = "3"
                        }
                    }
                    finish()
                    val user = task.result?.user
                    Log.d(TAG, "signInWithPhoneAuthCredential: ${user?.phoneNumber}")
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}