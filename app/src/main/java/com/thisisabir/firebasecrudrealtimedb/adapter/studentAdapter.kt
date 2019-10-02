package com.thisisabir.firebasecrudrealtimedb.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.thisisabir.firebasecrudrealtimedb.R
import com.thisisabir.firebasecrudrealtimedb.model.student

class studentAdapter(val studentList: List<student>, val context: Context) : RecyclerView.Adapter<studentAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int
    {
        return studentList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int)
    {
        holder.studentname.text = studentList.get(position).name
        holder.deptname.text = studentList.get(position).departmentname

        // if user click on update icon for  update operation
        holder.edit.setOnClickListener()
        {
            val perItemPosition = studentList.get(position)
            updateDialog(perItemPosition)
        }

        // if user click on delete icon for delete operation
        holder.delete.setOnClickListener()
        {
            val perItemPosition = studentList.get(position)
            deletedata(perItemPosition.studentid)

        }

    }


    // holder class
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val studentname = itemView.findViewById(R.id.textView1) as TextView
        val deptname = itemView.findViewById(R.id.textView2) as TextView

        // action operation widget
        val edit = itemView.findViewById(R.id.editimage) as ImageView
        val delete = itemView.findViewById(R.id.deleteimage) as ImageView

    }

    // update dialog show method
    private fun updateDialog(perItemPosition: student) {

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_dialog, null)
        builder.setCancelable(false)

        val editext1 = view.findViewById<EditText>(R.id.editText1)
        val editext2 = view.findViewById<EditText>(R.id.updatespinerstring)

        // set exist data from recycler to dialog field
        editext1.setText(perItemPosition.name)
        editext2.setText(perItemPosition.departmentname)

        // now set view to builder
        builder.setView(view)
        // now set positive negative button in alertdialog
        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                // update operation below
                val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Students")

                val name = editext1.text.toString()
                val department = editext2.text.toString()

                if (name.isEmpty() && department.isEmpty())
                {
                    editext1.error = "please Fill up data"
                    editext1.requestFocus()
                    return
                }
                else
                {
                    // update data
                    val std_data = student(perItemPosition.studentid,name,department)
                    studentdatabaseref.child(perItemPosition.studentid).setValue(std_data)
                    Toast.makeText(context, "Data Updated", Toast.LENGTH_SHORT).show()

                }


            }
        })

        builder.setNegativeButton("No", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {


            }
        })
        // show dialog now
        val alert = builder.create()
        alert.show()
    }

    // delete operation
    private fun deletedata(student_id: String)
    {
        val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Students").child(student_id)
        studentdatabaseref.removeValue().addOnCompleteListener()
        {
            Toast.makeText(context, "Data Deleted Successfully", Toast.LENGTH_SHORT).show()
        }

    }


}