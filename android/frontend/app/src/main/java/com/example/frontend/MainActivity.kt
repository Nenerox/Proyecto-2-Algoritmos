package com.example.frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d("AUTH", "Login anónimo exitoso")

                    val functions = FirebaseFunctions.getInstance()

                    functions
                        .getHttpsCallable("testNeo4j")
                        .call()
                        .addOnSuccessListener { result ->

                            Log.d("FIREBASE", result.data.toString())

                        }
                        .addOnFailureListener {

                            Log.e("FIREBASE", it.message.toString())

                        }

                } else {

                    Log.e("AUTH", "Error auth")

                }
            }

        setContent {
            Text(text = "Firebase funcionando 🚀")
        }
    }
}