package br.edu.ifpr.stiehl.aula7_sqlite

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import br.edu.ifpr.stiehl.aula7_sqlite.bd.AppDatabase
import br.edu.ifpr.stiehl.aula7_sqlite.bd.dao.TarefaDao
import br.edu.ifpr.stiehl.aula7_sqlite.bd.migracoes.MIGRATIONS
import br.edu.ifpr.stiehl.aula7_sqlite.entidades.Tarefa
import br.edu.ifpr.stiehl.aula7_sqlite.ui.TarefaAdapter
import br.edu.ifpr.stiehl.aula7_sqlite.ui.TarefaListListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TarefaListListener {
    override fun buscaTarefa(id: Int): Tarefa {
        return tarefaDao.buscaTarefa(id)
    }

    lateinit var db: AppDatabase
    lateinit var tarefaDao: TarefaDao
    lateinit var adapter: TarefaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tarefa.db"
        )
            .allowMainThreadQueries()
            .addMigrations(*MIGRATIONS)
            .build()
        tarefaDao = db.tarefaDao()

        btAdicionarTarefa.setOnClickListener {

            var tarefa = Tarefa("", "", false)


            adapter.adicionarTarefaEditar(tarefa)
            adapter.adicionarTarefa(tarefa)
            listTarefas.scrollToPosition(0)

        }

        atualizaLista()

        configurarRecycler()
    }

    fun atualizaLista() {
        val tarefas = tarefaDao.buscaTodas()
        adapter = TarefaAdapter(tarefas.toMutableList(), this, tarefaDao)
        listTarefas.adapter = adapter
    }

    fun limpaCampos() {

    }

    fun configurarRecycler() {
        listTarefas.layoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL, false)
    }

    override fun tarefaDeletada(tarefa: Tarefa) {
        tarefaDao.apagar(tarefa)
    }

    override fun tarefaInserida(tarefa: Tarefa) {
        val id = tarefaDao.adicionarTarefa(tarefa).toInt()
        tarefa.id = id
    }

    override fun tarefaUpdate(tarefa: Tarefa) {
        tarefaDao.update(tarefa.id, tarefa.titulo, tarefa.desc, tarefa.status)
    }

}
