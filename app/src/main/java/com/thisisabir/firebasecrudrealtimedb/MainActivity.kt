package com.thisisabir.firebasecrudrealtimedb

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.firebase.database.*
import com.thisisabir.firebasecrudrealtimedb.adapter.studentAdapter
import com.thisisabir.firebasecrudrealtimedb.model.student
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var studentname: EditText
    lateinit var studentdepartmentname: EditText
    lateinit var savebutton: Button
    lateinit var progressBar: ProgressBar
    lateinit var studentdatabase: DatabaseReference
    lateinit var studentList: MutableList<student>
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // below 2 line is for add seperator line in peritem of reyclerview
        val itemDecor = DividerItemDecoration(this, VERTICAL)
        recyclerView.addItemDecoration(itemDecor)

        studentname = findViewById(R.id.editText);
        studentdepartmentname = findViewById(R.id.editText1);
        savebutton = findViewById(R.id.savedata);
        progressBar = findViewById(R.id.progressbar)
        studentdatabase = FirebaseDatabase.getInstance().getReference("Students")


        // initialize mutable list
        studentList = mutableListOf()

        // save data to database
        savebutton.setOnClickListener()
        {
            savedatatoserver()
            progressBar.visibility = View.VISIBLE
        }

        // call load data method in main thread
        LoadData()


    }

    // function for save operation
    private fun savedatatoserver()
    {
        // get value from edit text & spinner
        val name: String = studentname.text.toString().trim()
        val classname: String = studentdepartmentname.text.toString().trim()

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(classname))
        {
            val studentid = studentdatabase.push().key

            val STD = student(studentid.toString(),name,classname)
            studentdatabase.child(studentid.toString()).setValue(STD)

            studentdatabase.child(studentid.toString()).setValue(STD).addOnCompleteListener{

                Toast.makeText(this,"Successfull", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
                studentname.text = null
                studentdepartmentname.text = null

            }


        }
        else
        {
            Toast.makeText(this,"Please Enter the name of student", Toast.LENGTH_LONG).show()
        }

    }

    // load data from firebase database
    private fun LoadData()
    {

        // show progress bar when call method as loading concept
        progressBar.visibility = View.VISIBLE

        studentdatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError)
            {
                Toast.makeText(applicationContext,"Error Encounter Due to "+databaseError.message, Toast.LENGTH_LONG).show()/**/

            }

            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    //before fetch we have clear the list not to show duplicate value
                    studentList.clear()
                    // fetch data & add to list
                    for (data in dataSnapshot.children)
                    {
                        val std = data.getValue(student::class.java)
                        studentList.add(std!!)
                    }

                    // bind data to adapter
                    val adapter = studentAdapter(studentList,this@MainActivity)
                    recyclerview.adapter = adapter
                    progressBar.visibility = View.GONE
                    adapter.notifyDataSetChanged()

                }
                else
                {
                    // if no data found or you can check specefici child value exist or not here
                    Toast.makeText(applicationContext,"No data Found", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }

            }

        })
    }

}
