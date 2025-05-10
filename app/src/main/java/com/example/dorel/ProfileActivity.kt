package com.example.dorel

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var logoutButton: Button

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nameInput = findViewById(R.id.name_input)
        emailInput = findViewById(R.id.email_input)
        phoneInput = findViewById(R.id.phone_input)
        addressInput = findViewById(R.id.address_input)
        saveProfileButton = findViewById(R.id.save_profile_button)
        logoutButton = findViewById(R.id.logout_button)

        // Fetch existing profile data
        fetchProfileData()

        // Save profile data to Firestore
        saveProfileButton.setOnClickListener {
            saveProfileData()
        }

        // Logout user
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val viewCreditCardsButton: Button = findViewById(R.id.view_credit_cards_button)
        viewCreditCardsButton.setOnClickListener {
            val intent = Intent(this, AddCreditCardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            emailInput.setText(currentUser.email) // Populate email field
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nameInput.setText(document.getString("name"))
                        phoneInput.setText(document.getString("phone"))
                        addressInput.setText(document.getString("address"))
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to fetch profile data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun saveProfileData() {
        val name = nameInput.text.toString()
        val phone = phoneInput.text.toString()
        val address = addressInput.text.toString()

        if (name.isBlank() || phone.isBlank() || address.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userProfile = mapOf(
                "name" to name,
                "phone" to phone,
                "address" to address,
                "email" to currentUser.email
            )

            db.collection("users").document(currentUser.uid).set(userProfile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
