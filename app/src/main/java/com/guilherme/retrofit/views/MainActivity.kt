package com.guilherme.retrofit.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.guilherme.retrofit.R
import com.guilherme.retrofit.databinding.ActivityMainBinding
import com.guilherme.retrofit.databinding.ItemProdutoBinding
import com.guilherme.retrofit.model.Produto
import com.guilherme.retrofit.service.ProdutoService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chamarApiListProd()
    }

    fun chamarApiListProd(){

        //1-> Criar uma instância do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://oficinacordova.azurewebsites.net")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //2 -> Criar uma instância do serviço
        val service = retrofit.create(ProdutoService::class.java)

        //3 -> Criar uma chamada
        val chamada = service.listar()

        //4 -> Definir um callback de retorno
        val callback = object : Callback<List<Produto>>{
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful){

                    val listaProduto = response.body()

//                    val nomeProduto = listaProduto?.first()?.nomeProduto

//                    alert("Sucesso", "Nome do Primeiro Produto: $nomeProduto")

                    mostrarProdutos(listaProduto)

                }
                else {
                    alert("Erro", response.code().toString())
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                alert("Erro", t.message.toString())
            }

        }

        //5 -> Executar a chamada

        chamada.enqueue(callback)

    }

    //Função dos elementos dinâmicos
    fun mostrarProdutos(listaProdutos: List<Produto>?){

        //0 - Iterar pelos produtos
        listaProdutos?.forEach {

            //1 - inflar o layout do item da lista
            val itemProduto = ItemProdutoBinding .inflate(layoutInflater)

            //2 -  Configurar as views com os dados do backend
            itemProduto.txtNome.text = it.nomeProduto
            itemProduto.txtPreco.text = it.precProduto.toString()

            //2.2 - Obter img do item
            Picasso
                .get()
                .load("https://oficinacordova.azurewebsites.net/android/rest/produto/image/${it.idProduto}")
                .into(itemProduto.imageView)

            //3 - Adicionar o layout no container
            binding.container.addView(itemProduto.root)

        }


    }

    fun alert(titulo: String, msg: String){
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }
}