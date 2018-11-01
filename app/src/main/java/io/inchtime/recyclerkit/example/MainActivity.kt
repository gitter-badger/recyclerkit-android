package io.inchtime.recyclerkit.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.inchtime.recyclerkit.RecyclerAdapter
import io.inchtime.recyclerkit.RecyclerKit
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {

//        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        val adapter = RecyclerAdapter(this)
//
//        recyclerView.layoutManager = layoutManager
//        recyclerView.adapter = adapter
//
//        val models = ArrayList<RecyclerAdapter.Model>()
//        adapter.setItems(models)

        val adapter = RecyclerKit.adapter(this)
            .recyclerView(R.id.recyclerView)
            .useGridLayout()
            .build()

//        val models = ArrayList<RecyclerAdapter.Model>()
//        adapter.setItems(models)

    }
}
