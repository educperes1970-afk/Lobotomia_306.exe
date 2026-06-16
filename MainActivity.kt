package com.meujogo.terrorlobotomia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import kotlin.random.Random
import android.materialbutton

class MainActivity : Activity() {

    // Variáveis Globais do Jogador
    private var vidaJogador = 100
    private var cliquesRespirar = 0
    private var estaEsquecendoDeRespirar = false
    private var passoAtualMecanica = 0 
    private var modoComerOuBeber = "comer" 

    // Variáveis da Mecânica do Espelho
    private var olhandoParaEspelho = false
    private val handlerEspelho = Handler(Looper.getMainLooper())
    private lateinit var runnableEspelho: Runnable
    private val nomesCorrompidos = listOf(
        "Quem é este?",
        "Desconhecido",
        "Monstro",
        "Outra pessoa",
        "Ninguém",
        "PARE POR FAVOR",
        "S.O.S",
        "me ajude"
    )

    // Elementos da Interface (UI)
    private lateinit var txtDialogo: TextView
    private lateinit var txtErroAndroid: TextView
    private lateinit var imgAlucinacao: ImageView
    private lateinit var btnAcaoGeral: Button
    
    // Elementos do Espelho e Interação
    private lateinit var pontinhoTela: View        
    private lateinit var txtNomePonteiro: TextView 
    private lateinit var imgOlhoCensurado: ImageView 

    // Botões da mecânica de comer/beber
    private lateinit var btnAbrirBoca: Button
    private lateinit var btnMastigar: Button
    private lateinit var btnEngolir: Button
    private lateinit var btnBeber: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa os componentes da tela
        txtDialogo = findViewById(R.id.txtDialogo)
        txtErroAndroid = findViewById(R.id.txtErroAndroid)
        imgAlucinacao = findViewById(R.id.imgAlucinacao)
        btnAcaoGeral = findViewById(R.id.btnAcaoGeral)
        btnAbrirBoca = findViewById(R.id.btnAbrirBoca)
        btnMastigar = findViewById(R.id.btnMastigar)
        btnEngolir = findViewById(R.id.btnEngolir)
        btnBeber = findViewById(R.id.btnBeber)
        
        // Elementos do espelho
        pontinhoTela = findViewById(R.id.pontinhoTela)
        txtNomePonteiro = findViewById(R.id.txtNomePonteiro)
        imgOlhoCensurado = findViewById(R.id.imgOlhoCensurado)

        // --- SISTEMA DE PERMISSÕES ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 101)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
        }

        configurarCliquesComerBeber()
        iniciarLoopDoMal()
    }

    private fun iniciarLoopDoMal() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (vidaJogador > 0) {
                    if (!olhandoParaEspelho) {
                        val sorteio = Random.nextInt(1, 6)
                        when (sorteio) {
                            1 -> iniciarEventoRespirar()
                            2 -> dispararAlucinacaoVisual()
                            3 -> iniciarMecanicaComerOuBeber()
                            4 -> simularErroAndroid(Random.nextInt(1, 3))
                            5 -> abrirVideoDoMonstro("/storage/emulated/0/Movies/monstro.mp4")
                        }
                    }
                    handler.postDelayed(this, Random.nextLong(12000, 25000))
                }
            }
        }, 5000)
    }

    // --- MECÂNICA DO ESPELHO ---
    fun iniciarOlharEspelho() {
        olhandoParaEspelho = true
        imgOlhoCensurado.visibility = View.VISIBLE 
        txtNomePonteiro.visibility = View.VISIBLE
        txtNomePonteiro.text = "Henrique" 

        runnableEspelho = object : Runnable {
            override fun run() {
                if (olhandoParaEspelho) {
                    val indiceAleatorio = Random.nextInt(nomesCorrompidos.size)
                    txtNomePonteiro.text = nomesCorrompidos[indiceAleatorio]
                    handlerEspelho.postDelayed(this, 3000)
                }
            }
        }
        handlerEspelho.postDelayed(runnableEspelho, 3000) 
    }

    fun pararOlharEspelho() {
        olhandoParaEspelho = false
        handlerEspelho.removeCallbacks(runnableEspelho) 
        imgOlhoCensurado.visibility = View.GONE
        txtNomePonteiro.text = "" 
        txtNomePonteiro.visibility = View.GONE
    }

    // --- 1. ESQUECER COMO RESPIRAR ---
    fun iniciarEventoRespirar() {
        estaEsquecendoDeRespirar = true
        cliquesRespirar = 0
        txtDialogo.visibility = View.VISIBLE
        txtDialogo.text = "Eu esqueci como respirar..."
        btnAcaoGeral.visibility = View.VISIBLE

        btnAcaoGeral.setOnClickListener {
            if (estaEsquecendoDeRespirar) {
                cliquesRespirar++
                btnAcaoGeral.text = "RESPIRAR! ($cliquesRespirar/100)"
            }
        }

        object : CountDownTimer(10000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (cliquesRespirar >= 100) {
                    estaEsquecendoDeRespirar = false
                    txtDialogo.text = "Consegui respirar..."
                    btnAcaoGeral.visibility = View.GONE
                    cancel()
                }
            }

            override fun onFinish() {
                if (estaEsquecendoDeRespirar) {
                    vidaJogador -= 50
                    txtDialogo.text = "*Sufocando intensamente*"
                    btnAcaoGeral.visibility = View.GONE
                    checarFimDeJogo()
                }
            }
        }.start()
    }

    // --- 2. ALUCINAÇÃO VISUAL ---
    fun dispararAlucinacaoVisual() {
        val caminhoImagem = "/storage/emulated/0/Pictures/1 sem titulo_20260614230111.png"
        val file = File(caminhoImagem)
        
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(caminhoImagem)
            imgAlucinacao.setImageBitmap(bitmap)
            imgAlucinacao.visibility = View.VISIBLE
            
            Handler(Looper.getMainLooper()).postDelayed({
                imgAlucinacao.visibility = View.GONE
            }, 89)
        }
    }

    // --- 3. ESQUECER COMO COMER / BEBER ---
    fun iniciarMecanicaComerOuBeber() {
        modoComerOuBeber = if (Random.nextBoolean()) "comer" else "beber"
        passoAtualMecanica = 1 
        
        txtDialogo.visibility = View.VISIBLE
        exibirBotoesComerBeber(true)

        if (modoComerOuBeber == "comer") {
            txtDialogo.text = "Como eu comia mesmo? Rápido, 2 segundos!"
        } else {
            txtDialogo.text = "Como eu bebia mesmo? Rápido, 2 segundos!"
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if ((modoComerOuBeber == "comer" && passoAtualMecanica <= 3) || 
                (modoComerOuBeber == "beber" && passoAtualMecanica <= 2)) {
                
                vidaJogador -= 20
                txtDialogo.text = "Eu esqueci... e meu corpo está fraco."
                exibirBotoesComerBeber(false)
                checarFimDeJogo()
            }
        }, 2000)
    }

    private fun configurarCliquesComerBeber() {
        btnAbrirBoca.setOnClickListener {
            if (passoAtualMecanica == 1) {
                passoAtualMecanica = 2 
                Toast.makeText(this, "Boca aberta...", Toast.LENGTH_SHORT).show()
            } else {
                aplicarCastigoOrdemErrada()
            }
        }

        btnMastigar.setOnClickListener {
            if (modoComerOuBeber == "comer" && passoAtualMecanica == 2) {
                passoAtualMecanica = 3
                Toast.makeText(this, "Mastigando...", Toast.LENGTH_SHORT).show()
            } else {
                aplicarCastigoOrdemErrada()
            }
        }

        btnEngolir.setOnClickListener {
            if (modoComerOuBeber == "comer" && passoAtualMecanica == 3) {
                passoAtualMecanica = 4 
                txtDialogo.text = "Consegui comer."
                exibirBotoesComerBeber(false)
            } else {
                aplicarCastigoOrdemErrada()
            }
        }

        btnBeber.setOnClickListener {
            if (modoComerOuBeber == "beber" && passoAtualMecanica == 2) {
                passoAtualMecanica = 3 
                txtDialogo.text = "Consegui beber a água."
                exibirBotoesComerBeber(false)
            } else {
                aplicarCastigoOrdemErrada()
            }
        }
    }

    private fun aplicarCastigoOrdemErrada() {
        vidaJogador -= 15
        Toast.makeText(this, "Ação errada! Meu cérebro dói...", Toast.LENGTH_SHORT).show()
        exibirBotoesComerBeber(false)
        checarFimDeJogo()
    }

    private fun exibirBotoesComerBeber(mostrar: Boolean) {
        val visibilidade = if (mostrar) View.VISIBLE else View.GONE
        btnAbrirBoca.visibility = visibilidade
        
        if (mostrar) {
            if (modoComerOuBeber == "comer") {
                btnMastigar.visibility = View.VISIBLE
                btnEngolir.visibility = View.VISIBLE
                btnBeber.visibility = View.GONE
            } else {
                btnMastigar.visibility = View.GONE
                btnEngolir.visibility = View.GONE
                btnBeber.visibility = View.VISIBLE
            }
        } else {
            btnMastigar.visibility = View.GONE
            btnEngolir.visibility = View.GONE
            btnBeber.visibility = View.GONE
        }
    }

    // --- 4. ERRO DE REALIDADE ---
    fun simularErroAndroid(tipoErro: Int) {
        txtErroAndroid.visibility = View.VISIBLE
        if (tipoErro == 1) {
            txtErroAndroid.text = "錯誤\n請幫我"
        } else {
            txtErroAndroid.text = "止めて下さい止めて下さいFのために停止停止"
        }
        
        Handler(Looper.getMainLooper()).postDelayed({
            txtErroAndroid.visibility = View.GONE
        }, 4000)
    }

    // --- 5. ABRIR VÍDEO DO MONSTRO NO GOOGLE FILES ---
    fun abrirVideoDoMonstro(caminhoDoVideo: String) {
        val file = File(caminhoDoVideo)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/mp4")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.google.android.apps.nbu.files") 
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            val intentPadrao = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "video/mp4")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intentPadrao)
        }
    }

    private fun checarFimDeJogo() {
        if (vidaJogador <= 0) {
            txtDialogo.visibility = View.VISIBLE
            txtDialogo.text = "A lobotomia desfez você por completo. Fim de jogo."
        }
    }
}
