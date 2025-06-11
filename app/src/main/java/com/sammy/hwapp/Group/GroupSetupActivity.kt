package com.sammy.hwapp.Group

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sammy.hwapp.Group.Group
import com.sammy.hwapp.R

class GroupSetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val CLASS_GROUP_ID = "class_10a_group" // Уникальный ID для вашей единственной группы класса


    private fun createClassGroup() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("GroupSetup", "Пользователь не авторизован.")
            // Возможно, перенаправить на экран авторизации
            return
        }
        fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

//            val createGroupButton: Button = findViewById(R.id.create_group_button) // Добавьте такую кнопку в layout

//            createGroupButton.setOnClickListener {
//                createClassGroup()
//            }
        }

        // Проверяем, существует ли уже эта группа
        db.collection("groups").document(CLASS_GROUP_ID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    Log.d("GroupSetup", "Группа с ID $CLASS_GROUP_ID уже существует.")
                    // Если группа уже есть, можно обновить ее или просто уведомить пользователя.
                    // Если вы хотите убедиться, что текущий пользователь является членом,
                    // можно добавить его в список членов, если он еще не там.
                    val existingGroup = documentSnapshot.toObject(Group::class.java)
                    if (existingGroup != null && !existingGroup.members.contains(currentUser.uid)) {
                        existingGroup.members.add(currentUser.uid)
                        db.collection("groups").document(CLASS_GROUP_ID)
                            .set(existingGroup) // Обновляем документ
                            .addOnSuccessListener { Log.d("GroupSetup", "Пользователь добавлен в существующую группу.") }
                            .addOnFailureListener { e -> Log.e("GroupSetup", "Ошибка добавления пользователя в группу", e) }
                    }
                } else {
                    // Группа не существует, создаем новую
                    val newGroup = Group(
                        id = CLASS_GROUP_ID,
                        name = "Класс 10-А", // Название вашей группы
                        description = "Рабочий чат 10-А класса",
                        members = mutableListOf(currentUser.uid) // Добавляем создателя в члены группы
                    )

                    db.collection("groups").document(CLASS_GROUP_ID)
                        .set(newGroup) // Сохраняем группу в Firestore
                        .addOnSuccessListener {
                            Log.d("GroupSetup", "Группа 'Мой Класс 10-А' успешно создана!")
                            // Можно перейти на экран чата
                        }
                        .addOnFailureListener { e ->
                            Log.e("GroupSetup", "Ошибка создания группы", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("GroupSetup", "Ошибка проверки существования группы", e)
            }
    }
}