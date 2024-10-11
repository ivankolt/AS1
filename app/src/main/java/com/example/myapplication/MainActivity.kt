package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val products = mutableListOf<String>()
    private lateinit var editTextProduct: EditText
    private lateinit var editTextSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextProduct = findViewById(R.id.editTextProduct)
        editTextSearch = findViewById(R.id.editTextSearch)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = ProductAdapter(products) { position ->
            products.removeAt(position)
            adapter.notifyItemRemoved(position)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAdd.setOnClickListener {
            val product = editTextProduct.text.toString()
            if (product.isNotEmpty()) {
                products.add(product)
                adapter.notifyItemInserted(products.size - 1)
                editTextProduct.text.clear()
                adapter.filter(editTextSearch.text.toString()) // Обновляем фильтр после добавления
            }
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

class ProductAdapter(
    private val products: MutableList<String>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var filteredProducts: MutableList<String> = products.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewProduct.text = filteredProducts[position]
        holder.buttonRemove.setOnClickListener {
            listener(products.indexOf(filteredProducts[position]))
            filter("") // Reset filter after removal
        }
    }

    override fun getItemCount(): Int = filteredProducts.size

    fun filter(query: String) {
        filteredProducts.clear()
        if (query.isEmpty()) {
            filteredProducts.addAll(products)
        } else {
            products.filter { it.contains(query, ignoreCase = true) }
                .forEach { filteredProducts.add(it) }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewProduct: TextView = itemView.findViewById(R.id.textViewProduct)
        val buttonRemove: Button = itemView.findViewById(R.id.buttonRemove)
    }
}
